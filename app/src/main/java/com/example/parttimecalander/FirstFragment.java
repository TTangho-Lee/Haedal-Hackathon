package com.example.parttimecalander;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;


public class FirstFragment extends Fragment {

    TextView tvTest;
    private MaterialCalendarView calendarView;
    private  CalendarDay today, sunday, saturday;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 레이아웃을 직접 Inflate
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 버튼과 달력 참조
        Button buttonFirst = view.findViewById(R.id.button_first);
        tvTest = view.findViewById(R.id.tv_test);
        calendarView = view.findViewById(R.id.calendarView);

        // 버튼 클릭 리스너 설정
        buttonFirst.setOnClickListener(v -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        });

        //달력 일주일로 제한
        setWeekStartEnd();

        calendarView.state().edit()
                .setMinimumDate(sunday)
                .setMaximumDate(saturday)
                .commit();


        // 달력 설정
        calendarView.setTopbarVisible(false);
    }

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
        Toast.makeText(getContext(),"Sat: " + sat,Toast.LENGTH_SHORT).show();
        System.out.println("Saturday: " + sat);

        sunday = CalendarDay.from(sun.getYear(), sun.getMonth().getValue(), sun.getDayOfMonth());
        saturday = CalendarDay.from(sat.getYear(),sat.getMonth().getValue(),sat.getDayOfMonth());
    }
}
