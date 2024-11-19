package com.example.parttimecalander;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Simulate data loading using a handler and Runnable (as Kotlin's coroutine is replaced by this in Java)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Preload data and go to the next activity after the delay
            preloadMainActivityData();
            goToMainActivity();
        }, 200); // Delay time in milliseconds
    }

    private void preloadMainActivityData() {
        // This method would normally load data from a database, etc.
        // Simulated delay
        try {
            Thread.sleep(2000); // Simulating delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
