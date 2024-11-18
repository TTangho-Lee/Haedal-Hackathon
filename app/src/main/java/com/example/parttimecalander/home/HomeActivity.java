package com.example.parttimecalander.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parttimecalander.MainActivity;
import com.example.parttimecalander.R;
import com.example.parttimecalander.home.ui.summationmonth.SummationMonthFragment;
import com.example.parttimecalander.home.workplace.WorkPlaceActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.time.LocalDate;

public class HomeActivity extends AppCompatActivity {
    CalendarDay today, sunday, saturday;
    MaterialCalendarView mcv;

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

        //오늘을 포함한 일주일의 날짜를 선택
        setWeekStartEnd();
        //materialCalendarView 세팅
        mcv=(MaterialCalendarView)findViewById(R.id.calendarView);
        mcv.setTopbarVisible(false);
        mcv.state().edit().setMinimumDate(sunday).setMaximumDate(saturday).commit();


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
