package com.example.parttimecalander.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.parttimecalander.R;
import com.example.parttimecalander.home.ui.summationmonth.SummationMonthFragment;
import com.example.parttimecalander.home.ui.summationmonth.SummationViewModel;

public class SummationActivity extends AppCompatActivity {

    private SummationViewModel summationViewModel;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summation);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SummationMonthFragment fragment=new SummationMonthFragment();
        // 프래그먼트를 컨테이너에 교체
        fragmentTransaction.replace(R.id.viewPager, fragment);
        fragmentTransaction.addToBackStack(null); // 뒤로가기 버튼으로 이전 상태로 돌아가기
        fragmentTransaction.commit(); // 변경사항 적용
    }
}


