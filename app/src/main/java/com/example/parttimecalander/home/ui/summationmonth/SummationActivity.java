package com.example.parttimecalander.home.ui.summationmonth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.WorkDailyDatabase;
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.WorkDaily;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;


public class SummationActivity extends AppCompatActivity {


    private WorkDailyDatabase dailyDatabase;
    private WorkPlaceDatabase placeDatabase;
    private WorkDailyDao dailyDao;
    private WorkPlaceDao placeDao;
    private int year;
    private int month;
    private int previousYear; // 이전에 선택된 연도
    private int previousMonth;
    private SharedPreferences sharedPreferences;
    int dayOfWeekNumber;
    public int[][] time_calander = new int[6][7];
    List<RecyclerItem> items = new ArrayList<>();
    public RecyclerView recyclerView;
    private SummationMonthAdapter adapter;  // 어댑터 선언
    public TextView time_text;
    private ImageView back;

    public int first_start=0;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summation_month);
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        // 현재 연도와 월 가져오기
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH); // 0부터 시작 (0 = 1월)
        year = sharedPreferences.getInt("selectedYear", currentYear); // 기본값 2024
        month = sharedPreferences.getInt("selectedMonth", currentMonth+1);  // 기본값 11월
        previousYear = year;
        previousMonth = month;

        LocalDate firstDay = LocalDate.of(year, month, 1);
        dayOfWeekNumber = firstDay.getDayOfWeek().getValue() - 1;
        time_text=(TextView)findViewById(R.id.time_text);
        dailyDatabase = WorkDailyDatabase.getDatabase(this);
        dailyDao = dailyDatabase.workDailyDao();
        placeDatabase = WorkPlaceDatabase.getDatabase(this);
        placeDao = placeDatabase.workPlaceDao();
        Spinner spinnerYear = findViewById(R.id.spinner_year);
        Spinner spinnerMonth = findViewById(R.id.spinner_month);

        back = findViewById(R.id.back);
        back.setOnClickListener(v->onBackPressed());



        // 연도 리스트 생성 (-10년 ~ +10년)
        List<String> yearList = new ArrayList<>();
        for (int i = currentYear - 10; i <= currentYear + 10; i++) {
            yearList.add(String.valueOf(i));
        }

        // 월 리스트 생성 (1월 ~ 12월)
        List<String> monthList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthList.add(i + "월");
        }

        // 연도 Spinner 어댑터 설정
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // 월 Spinner 어댑터 설정
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, monthList);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // 기본값 설정 (현재 연도와 월 선택)
        spinnerYear.setSelection(yearList.indexOf(String.valueOf(currentYear)));
        spinnerMonth.setSelection(currentMonth); // 0부터 시작하므로 그대로 사용



        // Spinner 선택 이벤트 처리
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedYear = Integer.parseInt(yearList.get(position));

                // 연도가 변경되었을 때만 recreate 호출
                if (selectedYear != previousYear) {
                    previousYear = selectedYear;
                    year = selectedYear;

                    // 선택된 연도를 SharedPreferences에 저장
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("selectedYear", year);
                    editor.apply();

                    recreate(); // 새로운 값이 선택되었을 때만 Activity 재생성
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택되지 않을 때 처리 (필요한 경우 구현)
            }
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedMonth = position + 1;  // 월은 0부터 시작하므로 1을 더해줍니다.

                // 월이 변경되었을 때만 recreate 호출
                if (selectedMonth != previousMonth) {
                    previousMonth = selectedMonth;
                    month = selectedMonth;

                    // 선택된 월을 SharedPreferences에 저장
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("selectedMonth", month);
                    editor.apply();

                    recreate(); // 새로운 값이 선택되었을 때만 Activity 재생성
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택되지 않을 때 처리 (필요한 경우 구현)
            }
        });

        Executors.newSingleThreadExecutor().execute(() -> {
            double all_time=0;
            double all_money=0;
            List<WorkPlace> placeList = placeDao.getDataAll();
            List<WorkDaily> dailyList = dailyDao.getDataAll();
            items.clear();
            for (int i = 0; i < placeList.size(); i++) {
                WorkPlace place = placeList.get(i);
                for (int ii = 0; ii < 6; ii++) {
                    for (int j = 0; j < 7; j++) {
                        time_calander[ii][j] = 0;
                    }
                }

                for (int j = 0; j < dailyList.size(); j++) {
                    WorkDaily dailyWork = dailyList.get(j);
                    if (dailyWork.placeId == place.ID) {
                        LocalDateTime startTime = LocalDateTime.parse(dailyWork.startTime, formatter);
                        LocalDateTime endTime = LocalDateTime.parse(dailyWork.endTime, formatter);
                        if (startTime.getYear() == year && startTime.getMonthValue() == month) {
                            set_time(startTime.getDayOfMonth(), (int) Duration.between(startTime, endTime).getSeconds());
                        }
                    }
                }

                int[][] new_calander = new int[6][7];
                for (int ii = 0; ii < 6; ii++) {
                    System.arraycopy(time_calander[ii], 0, new_calander[ii], 0, 7);
                }
                RecyclerItem new_item = new RecyclerItem(year,month,place.placeName, new_calander, place.isJuhyu, place.usualPay,place.ColorHex);
                items.add(new_item);
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
                all_time+=normal_hour+over_hour;
                all_money+=normal_hour*new_item.pay+over_hour*new_item.pay*1.5;

            }



            // UI 스레드에서 RecyclerView 업데이트
            double finalAll_time = all_time;
            double finalAll_money = all_money;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DecimalFormat df = new DecimalFormat("###,###");
                    time_text.setText(finalAll_time +"시간 일하고\n"+ df.format((int)finalAll_money) +"원 벌 예정이에요");
                    recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SummationActivity.this));


                    int[] week_money= {0,0,0,0,0,0};
                    for(RecyclerItem item:items){
                        int[][] week_money_each=item.worked_time;
                        for(int i=0;i<6;i++){
                            int week_time_all=0;
                            for(int j=0;j<7;j++){
                                week_time_all+=week_money_each[i][j];
                            }
                            int over_time=0;
                            if(item.juhyu){
                                over_time=week_time_all-54000;
                            }
                            if(over_time>0){
                                week_time_all+= (int) (over_time*0.5);
                            }
                            week_money[i]+=week_time_all*item.pay/3600;
                        }
                    }
                    // BarChart 초기화
                    BarChart barChart = findViewById(R.id.time_gragh);

                    // 막대 데이터 생성
                    ArrayList<BarEntry> barEntries = new ArrayList<>();

                    for(int i=0;i<getWeeksInMonth(year,month);i++){
                        barEntries.add(new BarEntry(i+1, week_money[i])); // (x, y)
                    }
                    // 데이터셋 설정
                    BarDataSet barDataSet = new BarDataSet(barEntries, "주별 번 돈");
                    barDataSet.setColor(getResources().getColor(android.R.color.holo_blue_light)); // 막대 색상 설정
                    barDataSet.setValueTextSize(12f); // 값 텍스트 크기

                    // BarData에 데이터셋 추가
                    BarData barData = new BarData(barDataSet);
                    barData.setBarWidth(0.7f);
                    barChart.setData(barData);

                    // 설명 비활성화
                    Description description = new Description();
                    description.setText(""); // 설명을 비워줌
                    barChart.setDescription(description);
                    barChart.animateY(1500);
                    barChart.getXAxis().setDrawLabels(false);  // x축 레이블 숨기기
                    barChart.getAxisLeft().setDrawLabels(false);  // y축 레이블 숨기기
                    barChart.getAxisRight().setDrawLabels(false);
                    // 차트 새로고침
                    barChart.invalidate();

                    // 어댑터 설정
                    if (items != null && !items.isEmpty()) {

                        adapter = new SummationMonthAdapter(items, SummationActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.e("SummationMonthFragment", "No data available for adapter");
                    }
                }
            });
        });
    }
    public static int getWeeksInMonth(int year, int month1) {
        Calendar calendar = Calendar.getInstance();

        // 시작 날짜 설정: 주의 첫날(월요일)로 시작
        calendar.setFirstDayOfWeek(Calendar.MONDAY); // 한 주의 시작을 월요일로 설정
        calendar.setMinimalDaysInFirstWeek(4); // 첫 주에 최소 4일 포함해야 첫 주로 인정

        // 시작 주 계산
        calendar.set(year, month1 - 1, 1);  // 1일로 설정
        int firstWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        // 마지막 주 계산
        calendar.set(year, month1 - 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));  // 해당 월의 마지막 날로 설정
        int lastWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        // 만약 첫 주와 마지막 주가 다른 해에 속하는 경우, 마지막 주가 새 해로 넘어가는 경우를 고려
        if (lastWeek < firstWeek) {
            // 마지막 주가 1번 주로 넘어가는 경우, 그 해의 마지막 주는 lastWeek + 52
            lastWeek += calendar.getActualMaximum(Calendar.WEEK_OF_YEAR);
        }

        return lastWeek - firstWeek + 1;  // 시작 주와 마지막 주 사이의 차이를 계산
    }

    public void set_time(int day, int worked_time) {
        int d = day + dayOfWeekNumber - 1;
        time_calander[d / 7][d % 7] += worked_time;
    }
}


