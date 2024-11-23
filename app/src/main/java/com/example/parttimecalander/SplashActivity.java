package com.example.parttimecalander;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parttimecalander.home.HomeActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Simulate data loading using a handler and Runnable (as Kotlin's coroutine is replaced by this in Java)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Preload data and go to the next activity after the delay
            preloadMainActivityData();
            goToHomeActivity();
        }, 200); // Delay time in milliseconds
    }

    private void preloadMainActivityData() {
        // This method would normally load data from a database, etc.
        // Simulated delay
        try {
            Thread.sleep(1000); // Simulating delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
