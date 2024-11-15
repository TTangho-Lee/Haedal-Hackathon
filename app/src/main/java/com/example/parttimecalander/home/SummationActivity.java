package com.example.parttimecalander.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.parttimecalander.R;
import com.example.parttimecalander.home.ui.summationmonth.SummationViewModel;

public class SummationActivity extends AppCompatActivity {

    private SummationViewModel summationViewModel;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summation);

        // ViewModel 초기화
        summationViewModel = new ViewModelProvider(this).get(SummationViewModel.class);

        // ViewPager 초기화
        viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
    }
}


