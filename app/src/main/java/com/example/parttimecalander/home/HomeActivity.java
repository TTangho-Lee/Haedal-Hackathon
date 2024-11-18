package com.example.parttimecalander.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.WorkDailyDatabase;
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.WorkDaily;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.MainActivity;
import com.example.parttimecalander.R;
import com.example.parttimecalander.home.ui.summationmonth.RecyclerItem;
import com.example.parttimecalander.home.ui.summationmonth.SummationMonthAdapter;
import com.example.parttimecalander.home.ui.summationmonth.SummationMonthFragment;
import com.example.parttimecalander.home.workplace.WorkPlaceActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    CalendarDay today, sunday, saturday;
    MaterialCalendarView mcv;
    int dayOfWeekNumber;
    public int[][] time_calander = new int[6][7];
    public int[][] real_calander=new int[6][7];
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private WorkDailyDatabase dailyDatabase;
    private WorkPlaceDatabase placeDatabase;
    private WorkDailyDao dailyDao;
    private WorkPlaceDao placeDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_home);
        ConstraintLayout constraintLayout=(ConstraintLayout)findViewById(R.id.my_workplace);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, WorkPlaceActivity.class);
                startActivity(intent);
            }
        });
        ConstraintLayout monthly_summation=(ConstraintLayout)findViewById(R.id.monthly_summation);
        monthly_summation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,SummationActivity.class);
                startActivity(intent);
            }
        });
        TextView summation_title=(TextView)findViewById(R.id.summation_title);
        Calendar calendar = Calendar.getInstance(); // 현재 날짜와 시간 가져오기
        int currentMonth = calendar.get(Calendar.MONTH); // 0 = 1월, 11 = 12월
        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay=calendar.get(Calendar.DAY_OF_MONTH);
        summation_title.setText(currentMonth+1+"월 요약");


        TextView worktime=(TextView)findViewById(R.id.worktime);
        TextView earnmoney=(TextView)findViewById(R.id.earnmoney);
        TextView willmoney=(TextView)findViewById(R.id.willmoney);
        LocalDate firstDay = LocalDate.of(currentYear, currentMonth+1, 1);
        dayOfWeekNumber = firstDay.getDayOfWeek().getValue() - 1;
        dailyDatabase = WorkDailyDatabase.getDatabase(this);
        dailyDao = dailyDatabase.workDailyDao();
        placeDatabase = WorkPlaceDatabase.getDatabase(this);
        placeDao = placeDatabase.workPlaceDao();




        Executors.newSingleThreadExecutor().execute(() -> {
            double real_time=0;
            double real_money=0;
            double all_money=0;
            List<WorkPlace> placeList = placeDao.getDataAll();
            List<WorkDaily> dailyList = dailyDao.getDataAll();
            for (int i = 0; i < placeList.size(); i++) {
                WorkPlace place = placeList.get(i);
                for (int ii = 0; ii < 6; ii++) {
                    for (int j = 0; j < 7; j++) {
                        time_calander[ii][j] = 0;
                        real_calander[ii][j]=0;
                    }
                }

                for (int j = 0; j < dailyList.size(); j++) {
                    WorkDaily dailyWork = dailyList.get(j);
                    if (dailyWork.placeId == place.ID) {
                        LocalDateTime startTime = LocalDateTime.parse(dailyWork.startTime, formatter);
                        LocalDateTime endTime = LocalDateTime.parse(dailyWork.endTime, formatter);
                        if (startTime.getYear() == currentYear && startTime.getMonthValue() == currentMonth+1) {
                            set_time(startTime.getDayOfMonth(), (int) Duration.between(startTime, endTime).getSeconds());
                            if(startTime.getDayOfMonth()<currentDay){
                                set_real_time(startTime.getDayOfMonth(), (int) Duration.between(startTime, endTime).getSeconds());
                            }
                        }
                    }
                }
                int[][] new_calander = new int[6][7];
                for (int ii = 0; ii < 6; ii++) {
                    System.arraycopy(time_calander[ii], 0, new_calander[ii], 0, 7);
                }
                RecyclerItem new_item = new RecyclerItem(place.placeName, new_calander, place.isJuhyu, place.usualPay);
                double normal_hour=0;
                double over_hour=0;
                for(int ii=0;ii<6;ii++){
                    double second=0;
                    for(int j=0;j<7;j++){
                        second+=new_item.worked_time[ii][j];
                    }
                    second/=3600;
                    if(second>=15&&new_item.juhyu){
                        normal_hour+=15;
                        over_hour+=second-15;
                    }
                    else{
                        normal_hour+=second;
                    }
                }
                all_money+=normal_hour*new_item.pay+over_hour*new_item.pay*1.5;

                int[][] new_calander1 = new int[6][7];
                for (int ii = 0; ii < 6; ii++) {
                    System.arraycopy(real_calander[ii], 0, new_calander1[ii], 0, 7);
                }
                RecyclerItem new_item1 = new RecyclerItem(place.placeName, new_calander1, place.isJuhyu, place.usualPay);
                double normal_hour1=0;
                double over_hour1=0;
                for(int ii=0;ii<6;ii++){
                    double second=0;
                    for(int j=0;j<7;j++){
                        second+=new_item1.worked_time[ii][j];
                    }
                    second/=3600;
                    if(second>=15&&new_item1.juhyu){
                        normal_hour1+=15;
                        over_hour1+=second-15;
                    }
                    else{
                        normal_hour1+=second;
                    }
                }
                real_time=normal_hour1+over_hour1;
                real_money=normal_hour1* new_item1.pay+over_hour1* new_item1.pay*1.5;

            }

            double finalReal_time = real_time;
            double finalAll_money = all_money;
            double finalReal_money = real_money;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    worktime.setText(finalReal_time +"");
                    earnmoney.setText(finalReal_money +"");
                    willmoney.setText(finalAll_money +"");
                }
            });
        });



        //오늘을 포함한 일주일의 날짜를 선택
        setWeekStartEnd();
        //materialCalendarView 세팅
        mcv=(MaterialCalendarView)findViewById(R.id.calendarView);
        mcv.setTopbarVisible(false);
        mcv.state().edit().setMinimumDate(sunday).setMaximumDate(saturday).commit();


    }
    public void set_time(int day, int worked_time) {
        int d = day + dayOfWeekNumber - 1;
        time_calander[d / 7][d % 7] += worked_time;
    }
    public void set_real_time(int day, int worked_time) {
        int d = day + dayOfWeekNumber - 1;
        real_calander[d / 7][d % 7] += worked_time;
    }
    private void enableEdgeToEdge() {
        // Edge-to-edge 모드를 활성화하는 코드
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }
    //오늘을 기준으로 일요일~토요일까지 한 주의 날짜만 선택
    private void setWeekStartEnd(){
        today = CalendarDay.today();

        LocalDate td_local = LocalDate.now();
        LocalDate sun, sat;

        // 0 일요일, 1 월요일, ..., 6 토요일
        int dayOfWeek = td_local.getDayOfWeek().getValue() % 7;

        // 오늘 날짜를 기준으로 주의 일요일과 토요일 계산
        sun = td_local.minusDays(dayOfWeek);        // 오늘에서 dayOfWeek만큼 빼기 -> 일요일
        sat = td_local.plusDays(6 - dayOfWeek + 1);     // 오늘에서 (6 - dayOfWeek)만큼 더하기 -> 토요일

        // 결과 확인
        sunday = CalendarDay.from(sun.getYear(), sun.getMonth().getValue(), sun.getDayOfMonth());
        saturday = CalendarDay.from(sat.getYear(),sat.getMonth().getValue(),sat.getDayOfMonth());
    }
}
