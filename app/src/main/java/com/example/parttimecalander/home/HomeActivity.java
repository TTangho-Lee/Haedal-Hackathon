package com.example.parttimecalander.home;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.PartTimeDatabase;
import com.example.parttimecalander.Database.data.User;
import com.example.parttimecalander.Database.data.WorkDaily;
import com.example.parttimecalander.Database.data.WorkPlace;
import com.example.parttimecalander.calander.EventDecorator;
import com.example.parttimecalander.databinding.ActivityHomeBinding;
import com.example.parttimecalander.home.goal.GoalActivity;
import com.example.parttimecalander.R;
import com.example.parttimecalander.calander.CalendarActivity;
import com.example.parttimecalander.home.resume.ResumeActivity;
import com.example.parttimecalander.home.scheduledialog.ScheduleDialogFragment;
import com.example.parttimecalander.home.ui.summationmonth.SummationActivity;
import com.example.parttimecalander.home.workplace.WorkPlaceActivity;
import com.example.parttimecalander.timer.TimerService;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity implements ScheduleDialogFragment.TimerDialogListener {

    ActivityHomeBinding binding;
    CalendarDay today, sunday, saturday;
    PartTimeDatabase partTimeDatabase;
    private WorkDailyDao dailyDao;
    private WorkPlaceDao placeDao;
    private UserDao userDao;
    private TimerReceiver timerReceiver;

    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 1;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //알림 권한 요청
        request_noti();

        partTimeDatabase = PartTimeDatabase.getDatabase(this);
        userDao = partTimeDatabase.userDao();

        resetForIntent();
    }
    private void request_noti(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // 권한 요청
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_NOTIFICATION_PERMISSION);
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        reset_layout();
    }
    @Override

    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    // 인터페이스 메서드 구현
    @Override
    public void onTimeSet(String startTime, String endTime) {

        LocalDateTime startDateTime = LocalDateTime.parse(startTime);
        LocalDateTime endDateTime = LocalDateTime.parse(endTime);
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 현재 시간 < 시작 시간 < 끝 시간 인지 확인
        if (currentTime.isAfter(startDateTime) && currentTime.isBefore(endDateTime)) {

            timerReceiver = new TimerReceiver();
            IntentFilter filter = new IntentFilter(TimerService.TIMER_UPDATE_ACTION);
            registerReceiver(timerReceiver, filter,Context.RECEIVER_EXPORTED);

            // 📢 서비스 시작
            Intent serviceIntent = new Intent(this, TimerService.class);
            serviceIntent.putExtra("endTime", endTime);
            startService(serviceIntent);
        }
    }
    private void resetForIntent(){

        binding.weekCalander.setOnClickListener(v->{
            Intent intent=new Intent(HomeActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        //월별&주별 요약
        binding.monthlySummation.setOnClickListener(v->{
            Intent intent=new Intent(HomeActivity.this, SummationActivity.class);
            startActivity(intent);
        });

        //나의 근무지
        binding.myWorkplace.setOnClickListener(v->{
            Intent intent = new Intent(HomeActivity.this, WorkPlaceActivity.class);
            startActivity(intent);
        });

        //나의 목표
        binding.myGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GoalActivity.class);
                startActivity(intent);
            }
        });

        //나의 이력서
        binding.myResume.setOnClickListener(v->{
            Intent intent=new Intent(HomeActivity.this,ResumeActivity.class);
            startActivity(intent);
        });
    }
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void reset_layout(){

        // 오늘 날짜 구하기
        LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        int currentDay = today.getDayOfMonth();

        LocalDateTime startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDateTime endOfWeek = today.with(DayOfWeek.SUNDAY);

        // 비동기실행 -> 데이터베이스 사용
        Executors.newSingleThreadExecutor().execute(() -> {

            // 유저가 없으면 빈 깡통 유저 인스턴스 생성(오류방지)
            User user;
            if(userDao.getDataAll().isEmpty()){
                user=new User();
                // 첫 어플 적석 시 오류를 위해 오늘 날짜로 초기화
                // 원래는 나중에 업데이트날짜 초기화
                user.recentUpdate = today.toString();
                userDao.setInsertData(user);
                return;
            }else{
                user=userDao.getDataAll().get(0);
            }

            // 요약 타이틀
            binding.summationTitle.setText(String.format("%d월 요약", currentMonth));
            partTimeDatabase = PartTimeDatabase.getDatabase(this);

            dailyDao = partTimeDatabase.workDailyDao();
            placeDao = partTimeDatabase.workPlaceDao();
            userDao  = partTimeDatabase.userDao();

            // 이번달 1일을 선언 -> 이번달 수익과 예상수익 계산에 활용
            LocalDateTime firstDay = today.withDayOfMonth(1);
            LocalDateTime lastDay = today.withDayOfMonth(1).plusMonths(1).minusDays(1);

            // 유저 생성을 아직 안함 or 유저 이름이 기록이 안됨 -> 이력서 작성 텍스트

            // 스케줄이 있는 날을 기록하기 위한 Set(중복금지) Hash(검색가능)
            HashSet<CalendarDay> days = new HashSet<>();

            double monthWorkingTime=0; // 이번달 일한 시간
            double monthWorkingMoney=0; // 벌어들인 돈
            double monthWorkingAllMoney=0; // 이번달 총 벌어들일 액수

            List<WorkPlace> placeList = placeDao.getDataAll(); // 일하는 모든 장소
            // 최근 업데이트 이전내용은 당연히 그떄 전부 처리됐기 때문에 그 이후 내용만 가져온다.
            List<WorkDaily> monthDailyList = dailyDao.getSchedulesBetweenDays(firstDay.toString(), lastDay.toString());
            List<WorkDaily> updateDailyList = dailyDao.getSchedulesBetweenDays(user.recentUpdate, today.toString());
            List<WorkDaily> weekDailyList = dailyDao.getSchedulesBetweenDays(startOfWeek.toString(), endOfWeek.toString());
            // 업데이트 할 스케쥴 순회
            for(WorkDaily workDaily : updateDailyList){

                double cTime=0; // 이번 스케쥴에서 시간
                double cMoney=0; //이번 스케쥴에서 벌어들인 돈

                // 문자열 시간으로 변환
                LocalDateTime startTime = LocalDateTime.parse(workDaily.startTime);
                LocalDateTime endTime = LocalDateTime.parse(workDaily.endTime);

                // 두 시간 사이의 간격
                Duration duration = Duration.between(startTime, endTime);

                long hours = duration.toHours();
                double hoursWithFraction = duration.toMinutes() / 60.0;
                cTime = hours + hoursWithFraction;

                WorkPlace place = findWorkPlace(placeList,workDaily);

                assert place != null;
                cMoney = cTime * place.usualPay;

                if(place.isJuhyu) cMoney *= 1.2;

                user.money += (int)cMoney;
                user.goalSaveMoney += (int)cMoney;
            }

            // 이번달 스케쥴 순회
            for (WorkDaily workDaily : monthDailyList) {

                double cTime=0; // 이번 스케쥴에서 시간
                double cMoney=0; //이번 스케쥴에서 벌어들인 돈

                // 문자열 시간으로 변환
                LocalDateTime startTime = LocalDateTime.parse(workDaily.startTime);
                LocalDateTime endTime = LocalDateTime.parse(workDaily.endTime);


                runOnUiThread(() ->{
                    onTimeSet(workDaily.startTime,workDaily.endTime);
                });

                // 두 시간 사이의 간격
                Duration duration = Duration.between(startTime, endTime);

                // 몇시간 몇분 일했냐
                cTime = duration.toMinutes() / 60.0;

                WorkPlace place = findWorkPlace(placeList,workDaily);
                assert place != null;
                cMoney = cTime * place.usualPay;
                if(place.isJuhyu) cMoney *= 1.2;

                if(endTime.isBefore(today)) {
                    monthWorkingTime += cTime;
                    monthWorkingMoney += cMoney;
                }
                // 총수익에도 더해줌 -> 이미 했던 스케줄도 이번달 총수익에 포함
                monthWorkingAllMoney += cMoney;
            }
            for(WorkDaily workDaily : weekDailyList){
                CalendarDay localDateTime = CalendarDay.from(
                        LocalDateTime.parse(workDaily.startTime).getYear(),
                        LocalDateTime.parse(workDaily.startTime).getMonthValue(),
                        LocalDateTime.parse(workDaily.startTime).getDayOfMonth()
                );
                days.add(localDateTime);
            }

            // 업데이트처리가 끝났기때문에 마지막 업데이트를 현재로 바꾼다.
            user.recentUpdate = today.toString();
            // 저장해준다.
            userDao.setUpdateData(user);

            // monthWorkingTime, monthWorkingMoney, monthWorkingAllMoney 모두 선언된 이후 변경이 있는 값이다.
            // runOnUiThread에서는 해당 람다식 밖에서 선언되고 변경이 있었던 값은 사용할 수 없으므로 새롭게 다시 선언.
            double finalMonthWorkingTime = monthWorkingTime;
            double finalMonthWorkingMoney = monthWorkingMoney;
            double finalMonthWorkingAllMoney = monthWorkingAllMoney;

            runOnUiThread(() -> {

                if(user.name == null){
                    binding.userText.setText("이력서 작성 탭에서\n이력서를 작성해주세요!");
                } else{
                    binding.userText.setText(String.format("%s님, 열심히 땀 흘려\n%d원이나 모았어요!", user.name, user.money));
                }

                if(user.goalImage!=null){
                    binding.profileImage.setBackground(byteArrayToDrawable(HomeActivity.this,user.goalImage));
                }

                String formattedAmount = NumberFormat.getNumberInstance(Locale.getDefault()).format(user.goal);
                binding.goalMoney.setText("목표 달성까지" + "\n" + formattedAmount + "원");

                List<String> dataList = new ArrayList<>();

                for (int i = 0; i < placeList.size(); i++) {
                    dataList.add(placeList.get(i).placeName+"///"+placeList.get(i).startDate+"~"+placeList.get(i).endDate+"///"+placeList.get(i).ColorHex);
                }

                homeRecyclerviewAdapter adapter = new homeRecyclerviewAdapter(dataList);

                binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                binding.homeRecyclerView.setAdapter(adapter);

                binding.worktime.setText(String.format("%.1f 시간", finalMonthWorkingTime));
                binding.earnmoney.setText(String.format("%.0f 원", finalMonthWorkingMoney));
                binding.willmoney.setText(String.format("%.0f 원", finalMonthWorkingAllMoney));

                //오늘을 포함한 일주일의 날짜를 선택
                setWeekStartEnd();

                binding.calendarView.setTopbarVisible(false);
                binding.calendarView.state().edit().setMinimumDate(sunday).setMaximumDate(saturday).commit();

                binding.calendarView.addDecorator(new EventDecorator(Color.RED, days));
                binding.calendarView.setOnDateLongClickListener((widget, date) -> {
                    int year = date.getYear();
                    int month = date.getMonth();
                    int day = date.getDay();

                    String selectedDate = String.format("%04d-%02d-%02d", year, month, day);

                    ScheduleDialogFragment dialog = ScheduleDialogFragment.newInstance(selectedDate);
                    dialog.show(getSupportFragmentManager(), "ScheduleDialog");
                });
            });
        });

    }
    private WorkPlace findWorkPlace(List<WorkPlace> placeList,WorkDaily workDaily){
        for(WorkPlace place : placeList) {
            // 일하는 곳 찾았으면
            if(place.ID==workDaily.placeId) {
                return place;
            }
        }
        return null;
    }
    private void enableEdgeToEdge() {
        // Edge-to-edge 모드를 활성화하는 코드
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }
    //오늘을 기준으로 일요일~토요일까지 한 주의 날짜만 선택
    private void setWeekStartEnd(){
        today = CalendarDay.today();

        LocalDate td_local = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate sun, sat;

        // 0 일요일, 1 월요일, ..., 6 토요일
        int dayOfWeek = td_local.getDayOfWeek().getValue() % 7;

        // 오늘 날짜를 기준으로 주의 일요일과 토요일 계산
        sun = td_local.minusDays(dayOfWeek);        // 오늘에서 dayOfWeek만큼 빼기 -> 일요일
        sat = td_local.plusDays(6 - dayOfWeek);     // 오늘에서 (6 - dayOfWeek)만큼 더하기 -> 토요일

        // 결과 확인
        sunday = CalendarDay.from(sun.getYear(), sun.getMonth().getValue(), sun.getDayOfMonth());
        saturday = CalendarDay.from(sat.getYear(),sat.getMonth().getValue(),sat.getDayOfMonth());
    }
    public static Drawable byteArrayToDrawable(Context context, byte[] imageData) {
        // byte[]를 Bitmap으로 변환
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

        // Bitmap을 Drawable로 변환
        return new BitmapDrawable(context.getResources(), bitmap);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timerReceiver);
    }

    // 📢 서비스에서 남은 시간을 수신하는 BroadcastReceiver 정의
    private class TimerReceiver extends BroadcastReceiver {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), TimerService.TIMER_UPDATE_ACTION)) {
                String remainingTime = intent.getStringExtra("remaining_time");
                binding.timerContent.setText("남은 시간: " + remainingTime); // 📢 UI 업데이트
            }
        }
    }
}
