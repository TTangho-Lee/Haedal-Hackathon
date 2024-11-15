package com.example.parttimecalander.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.parttimecalander.home.ui.summationmonth.SummationMonthFragment;
import com.example.parttimecalander.home.ui.summationmonth.SummationWeekFragment;

// ViewPager를 사용하여 month, week fragment를 양옆으로 swipe
// 클래스 이름, 생성자 매개변수(이름)
public class ViewPagerAdapter extends FragmentStateAdapter { // FragmentStateAdapter 상속

    public ViewPagerAdapter(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public int getItemCount() {
        return 2; // 두 개의 프래그먼트
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SummationMonthFragment();
            default:
                return new SummationWeekFragment();
        }
    }
}