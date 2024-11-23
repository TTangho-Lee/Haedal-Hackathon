package com.example.parttimecalander.calander;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Database.WorkDailyDatabase;
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.WorkDaily;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;

public class CalendarActivity extends AppCompatActivity {
    //views
    MaterialCalendarView mcv_month;
    RecyclerView rcv_schedule;
    TextView tv_date, add_schedule;
    ImageView back;
    //자료형
    HashSet<CalendarDay> days = new HashSet<>(); //일정 있는 날짜들(CalendarDay자료형), 년월일로 구성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // UI 요소 초기화
        tv_date = findViewById(R.id.title_day);
        rcv_schedule = findViewById(R.id.recyclerView_schedule);
        mcv_month = findViewById(R.id.calendarView);
        add_schedule = findViewById(R.id.add_schedule);
        back = findViewById(R.id.back);

        back.setOnClickListener(v->onBackPressed());
        
        add_schedule.setOnClickListener(v->{
            Intent intent = new Intent(CalendarActivity.this, SchduleRegisterActivity.class);
            startActivity(intent);
        });

        // 데이터베이스 작업: 백그라운드 스레드 실행
        Executors.newSingleThreadExecutor().execute(() -> {
            WorkDailyDatabase workDailyDatabase = WorkDailyDatabase.getDatabase(this);
            WorkDailyDao workDailyDao = workDailyDatabase.workDailyDao();
            List<WorkDaily> list = workDailyDao.getDataAll();

            // CalendarDay 데이터를 생성
            for (WorkDaily workDaily : list) {
                String dateString = workDaily.startTime;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);

                CalendarDay calendarDay = CalendarDay.from(
                        localDateTime.getYear(),
                        localDateTime.getMonthValue(),
                        localDateTime.getDayOfMonth()
                );
                days.add(calendarDay);
            }

            // UI 업데이트
            runOnUiThread(() -> {
                // 1. 날짜 밑에 점으로 표시
                mcv_month.addDecorator(new EventDecorator(Color.RED, days));

                // 2. 날짜를 선택하면 일정 표시
                mcv_month.setOnDateChangedListener((widget, date, selected) -> {
                    int year = date.getYear(), month = date.getMonth(), day = date.getDay();
                    tv_date.setText(String.format("%d-%d-%d", year, month, day));

                    // 선택한 날짜의 일정 불러오기
                    Executors.newSingleThreadExecutor().execute(() -> {
                        List<WorkPlace> places = new ArrayList<>();
                        WorkPlaceDatabase database = WorkPlaceDatabase.getDatabase(this);
                        WorkPlaceDao workPlaceDao = database.workPlaceDao();

                        for (WorkDaily workDaily : list) {
                            String dateString = workDaily.startTime;
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);

                            if (localDateTime.getYear() == year &&
                                    localDateTime.getMonthValue() == month &&
                                    localDateTime.getDayOfMonth() == day) {
                                int placeID = workDaily.placeId;
                                WorkPlace place = workPlaceDao.getByID(placeID);
                                if (place != null) places.add(place);
                            }
                        }

                        // UI 업데이트
                        runOnUiThread(() -> {
                            ScheduleAdapter adapter = new ScheduleAdapter(places);
                            rcv_schedule.setLayoutManager(new LinearLayoutManager(this));
                            rcv_schedule.setAdapter(adapter);
                        });
                    });
                });
            });
        });
    }


}