package com.example.parttimecalander.timer;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.parttimecalander.R;

public class TimerService extends Service {
    public static final String ACTION_UPDATE_TIMER = "com.example.parttimecalander.timer.ACTION_UPDATE_TIMER";
    public static final String EXTRA_TIME_LEFT = "EXTRA_TIME_LEFT";
    private CountDownTimer countDownTimer;
    private long timeLeftMillis = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 타이머 시작 시간 설정
        long duration = intent.getLongExtra("duration", 0);
        startTimer(duration);

        // Foreground 알림 생성
        startForeground(1, createNotification("Timer started"));
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // 바인딩하지 않고 Broadcast로 UI에 전달
    }

    private void startTimer(long duration) {
        timeLeftMillis = duration;
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;

                // Broadcast로 UI 업데이트
                Intent broadcastIntent = new Intent(ACTION_UPDATE_TIMER);
                broadcastIntent.putExtra(EXTRA_TIME_LEFT, millisUntilFinished);
                sendBroadcast(broadcastIntent);

                // Foreground 알림 업데이트
                updateNotification(formatTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                stopSelf(); // 서비스 종료
            }
        }.start();
    }

    private void updateNotification(String timeLeft) {
        Notification notification = createNotification("Time left: " + timeLeft);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(this).notify(1, notification);
    }

    private Notification createNotification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "TIMER_CHANNEL")
                .setContentTitle("Timer Service")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private String formatTime(long millis) {
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis / (1000 * 60)) % 60;
        long seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}