package com.example.parttimecalander.calander;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Database.PartTimeDatabase;
import com.example.parttimecalander.Database.data.WorkDaily;
import com.example.parttimecalander.Database.data.WorkPlace;
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
    List<WorkDaily> todayList = new ArrayList<>();


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
            PartTimeDatabase workDailyDatabase = PartTimeDatabase.getDatabase(this);
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

            runOnUiThread(() -> {
                // 1. 날짜 밑에 점으로 표시
                mcv_month.addDecorator(new EventDecorator(Color.RED, days));

                // 2. 날짜를 선택하면 일정 표시
                mcv_month.setOnDateChangedListener((widget, date, selected) -> {
                    int year = date.getYear(), month = date.getMonth(), day = date.getDay();
                    String selectedDate = String.format("%d-%02d-%02d", year, month, day); // 날짜 형식 보정
                    tv_date.setText(selectedDate);

                    // 선택한 날짜의 일정 불러오기
                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            todayList = workDailyDao.getSchedulesForDate(selectedDate); // Room 작업 비동기 실행
                            List<Pair<WorkPlace, WorkDaily>> places = new ArrayList<>();
                            PartTimeDatabase database = PartTimeDatabase.getDatabase(this);
                            WorkPlaceDao workPlaceDao = database.workPlaceDao();

                            for (WorkDaily todayWork : todayList) {
                                int placeID = todayWork.placeId;

                                // Room 작업은 반드시 비동기로 실행
                                WorkPlace place = workPlaceDao.getByID(placeID);
                                if (place != null) {
                                    places.add(new Pair<>(place, todayWork));
                                }
                            }

                            // UI 업데이트
                            runOnUiThread(() -> {
                                ScheduleDayAdapter adapter = new ScheduleDayAdapter(places);
                                adapter.setOnItemLongClickListener((item, position) -> {
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        workDailyDao.delete(item.second); // WorkDaily 삭제

                                        runOnUiThread(() -> {
                                            // 리스트에서 삭제하고 어댑터 갱신
                                            places.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            adapter.notifyItemRangeChanged(position, places.size());

                                            mcv_month.removeDecorators(); // 기존 데코레이터 제거
                                            mcv_month.addDecorator(new EventDecorator(Color.RED, days));

                                            Toast.makeText(this, "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        });
                                    });
                                });
                                rcv_schedule.setLayoutManager(new LinearLayoutManager(this));
                                rcv_schedule.setAdapter(adapter);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
            });
        });
    }


}