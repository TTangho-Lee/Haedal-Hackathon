package com.example.parttimecalander.timer;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.parttimecalander.R;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimerService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "timer_service_channel";
    public static final String TIMER_UPDATE_ACTION = "com.example.PartTimeCalander.TIMER_UPDATE"; // 브로드캐스트 액션 정의
    private Handler handler;
    private Runnable updateTimerRunnable;
    private String remainingTime = "00:00:00";

    private LocalDateTime startTime;
    private String endTime;
    private Duration duration;

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        createNotificationChannel();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent dismissIntent = new Intent(this, NotificationDismissReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 🔹 기본 알림 생성 (NotificationBuilder를 멤버 변수로 저장)
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("타이머 실행 중")
                .setContentText("남은 시간: " + remainingTime)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setDeleteIntent(pendingIntent);

        startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();
        endTime = intent.getStringExtra("endTime");
        return START_STICKY;
    }

    private void startTimer() {

        if (updateTimerRunnable != null) {
            handler.removeCallbacks(updateTimerRunnable);
        }

        updateTimerRunnable = new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {

                startTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
                duration = Duration.between(startTime, LocalDateTime.parse(endTime)); // 1초 감소

                if (duration.isNegative()) {
                    stopSelf(); // 타이머 종료
                    return;
                }

                remainingTime = String.format("%02d:%02d:%02d",duration.toHoursPart(),duration.toMinutesPart(),duration.toSecondsPart());
                Log.d("TimerService", "남은 시간: " + remainingTime);

                // 📢 브로드캐스트로 남은 시간 전송
                Intent intent = new Intent(TIMER_UPDATE_ACTION);
                intent.putExtra("remaining_time", remainingTime);
                sendBroadcast(intent);

                notificationBuilder.setContentText("남은 시간: " + remainingTime);
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

                handler.postDelayed(this, 1000); // 1초마다 실행
            }
        };

        handler.post(updateTimerRunnable);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "타이머 채널", NotificationManager.IMPORTANCE_LOW);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimerRunnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
