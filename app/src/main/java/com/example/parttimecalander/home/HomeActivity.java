package com.example.parttimecalander.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.parttimecalander.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_home);
    }

    private void enableEdgeToEdge() {
        // Edge-to-edge 모드를 활성화하는 코드
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }
}
