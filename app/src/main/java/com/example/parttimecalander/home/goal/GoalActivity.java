package com.example.parttimecalander.home.goal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parttimecalander.R;
import com.example.parttimecalander.timer.TimerService;


public class GoalActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        // 시작 시간과 끝 시간 설정 (예시)
        String startTime = "2024-11-23T03:00:00";
        String endTime = "2024-11-23T04:00:00";

        // 서비스 시작
        Intent serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.putExtra("start_time", startTime);
        serviceIntent.putExtra("end_time", endTime);
        startService(serviceIntent);
    }
}
