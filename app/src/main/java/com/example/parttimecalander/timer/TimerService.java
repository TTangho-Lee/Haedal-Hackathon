package com.example.parttimecalander.timer;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.parttimecalander.R;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimerService extends Service {

    private LocalDateTime endTime;
    private Handler handler;
    private Runnable updateTimerRunnable;
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 알림 설정 (포그라운드 서비스로 실행)
        startForeground(NOTIFICATION_ID, createNotification());
        // 시작 시간과 끝 시간 받기
        String startTimeStr = intent.getStringExtra("start_time");
        String endTimeStr = intent.getStringExtra("end_time");

        // 날짜 형식 파싱
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
        endTime = LocalDateTime.parse(endTimeStr);

        // 타이머 업데이트 시작
        startTimer();

        return START_STICKY;  // 서비스가 종료될 때 자동으로 재시작하도록 설정
    }

    private void startTimer() {
        // 주기적으로 타이머를 갱신할 Runnable 정의
        updateTimerRunnable = new Runnable() {
            @Override
            public void run() {
                updateRemainingTime();
                handler.postDelayed(this, 1000);  // 1초마다 타이머 갱신
            }
        };

        // 타이머 시작
        handler.post(updateTimerRunnable);
    }

    private void updateRemainingTime() {
        LocalDateTime now = LocalDateTime.now();

        // 남은 시간 계산
        Duration duration = Duration.between(now, endTime);

        // 남은 시간이 0이 되면 타이머를 멈추고 종료
        if (duration.isNegative() || duration.isZero()) {
            stopSelf();
            Toast.makeText(getApplicationContext(), "타이머 종료!", Toast.LENGTH_SHORT).show();
        } else {
            long secondsRemaining = duration.getSeconds();
            long hours = secondsRemaining / 3600;
            long minutes = (secondsRemaining % 3600) / 60;
            long seconds = secondsRemaining % 60;

            // 남은 시간 출력
            String remainingTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            Toast.makeText(getApplicationContext(), "남은 시간: " + remainingTime, Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;  // 이 서비스는 바인딩을 사용하지 않음
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimerRunnable);  // 서비스 종료 시 타이머 중지
    }

    private Notification createNotification() {
        NotificationChannel channel = new NotificationChannel(
                "timer_channel", "타이머 채널", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, "timer_channel")
                .setContentTitle("타이머")
                .setContentText("타이머가 실행 중입니다.")
                .setSmallIcon(R.drawable.arrow_back)
                .build();
    }
}
