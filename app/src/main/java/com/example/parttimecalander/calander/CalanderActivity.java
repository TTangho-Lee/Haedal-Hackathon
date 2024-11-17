package com.example.parttimecalander.calander;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;
import com.example.parttimecalander.home.workplace.WorkPlaceAdapter;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;

public class CalanderActivity extends AppCompatActivity {
    //views
    MaterialCalendarView mcv_month;
    RecyclerView rcv_schedule;
    TextView tv_date;
    //자료형
    HashSet<CalendarDay> days; //일정 있는 날짜들(CalendarDay자료형), 년월일로 구성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calander);
        //날짜 표시할 텍스트뷰
        tv_date = findViewById(R.id.title_day);
        //일정 표시할 리사이클러뷰
        rcv_schedule = findViewById(R.id.recyclerView_schedule);
        //달력
        mcv_month = findViewById(R.id.calendarView);


        // 데이터베이스에서 일정 있는 날짜들 전부 받아서 days에 넣기
        setDays();
        
        //1. 날짜에 일정이 있으면 날짜 밑에 점으로 표시됨
        mcv_month.addDecorator(new EventDecorator(Color.RED, days));

        //2. 날짜를 선택하면 그 날짜의 일정이 아래쪽 리사이클러 뷰에 표시됨
        mcv_month.setOnDateChangedListener((widget, date, selected) -> {
            int year = date.getYear(), month = date.getMonth(), day = date.getDay();
            Toast.makeText(this, "date: " + date, Toast.LENGTH_SHORT).show();

            //날짜 받아와서 데이터베이스의 그 날짜의 일정을 아래쪽 리사이클러 뷰에다 표기
            setRecyclerView(year, month, day);
            
        });
    }
    private void setDays(){
        //TODO: db연결해서 일정이 있는 days를 hashset에 다 넣기
    }
    private void setRecyclerView(int year, int month, int day){
        rcv_schedule.setLayoutManager(new LinearLayoutManager(this));

        WorkPlaceDatabase database=WorkPlaceDatabase.getDatabase(this);
        WorkPlaceDao workPlaceDao = database.workPlaceDao();
        
        Executors.newSingleThreadExecutor().execute(() -> {
            // 데이터 조회 및 어댑터 설정을 위한 스레드 시작

            //TODO: year, month, day에 있는 일정의 근무지만 가져올 것
            //List<WorkPlace> places = workPlaceDao.getDataAll();

            Log.d("ww",""+places.size());
            // 어댑터 설정
            ScheduleAdapter adapter = new ScheduleAdapter(places);
            rcv_schedule.setAdapter(adapter);
        });

    }
}