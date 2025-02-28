package com.example.parttimecalander.timer;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.parttimecalander.R;
import com.example.parttimecalander.widget.TimerWidget;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimerService extends Service {

    private ZonedDateTime startTime, endTime;
    private Handler handler;
    private Runnable updateTimerRunnable;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "timer_service_channel";
    public static final String TIMER_BROADCAST = "com.example.parttimecalander.timer.TIMER_UPDATE";
    public static final String TIMER_WIDGET_BROADCAST = "com.example.parttimecalander.timer.TIMER_WIDGET_BROADCAST";

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        createNotificationChannel();

        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent == null) {
                Log.e("TimerService", "Intent is null, stopping service");
                stopSelf();
                return START_NOT_STICKY;
            }

            String startTimeStr = intent.getStringExtra("start_time");
            String endTimeStr = intent.getStringExtra("end_time");

            if (startTimeStr == null || endTimeStr == null) {
                Log.e("TimerService", "Received null startTime or endTime, stopping service");
                stopSelf();
                return START_NOT_STICKY;
            }

            // 날짜 형식 파싱
            startTime = ZonedDateTime.parse(startTimeStr);
            endTime = ZonedDateTime.parse(endTimeStr);

            // 서비스가 정상적으로 시작될 경우 startForeground 실행
            startForeground(NOTIFICATION_ID, createNotification());

            // 타이머 업데이트 시작
            startTimer();

        } catch (Exception e) {
            Log.e("TimerService", "Exception in onStartCommand: ", e);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void startTimer() {
        try {
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
        } catch (Exception e) {
            Log.e("TimerService", "Exception in startTimer: ", e);
        }
    }

    private void updateRemainingTime() {
        ZonedDateTime now = ZonedDateTime.now();

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
            String left_time = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            // 남은 시간을 브로드캐스트로 전달
            Intent broadcastIntent = new Intent(TIMER_BROADCAST);
            broadcastIntent.putExtra("remaining_time", left_time );
            sendBroadcast(broadcastIntent);

            updateWidgetDirectly(getApplicationContext(), left_time);

            Log.d("TimerService", "Broadcast sent: " + TIMER_BROADCAST);
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
        updateWidgetDirectly(getApplicationContext(), "타이머를 켜주세요.");
        handler.removeCallbacks(updateTimerRunnable);  // 서비스 종료 시 타이머 중지
    }

    private void updateWidgetDirectly(Context context, String timeText) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widget = new ComponentName(context, TimerWidget.class);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.timer_widget);
        views.setTextViewText(R.id.timer_title, "퇴근까지 남은 시간");
        views.setTextViewText(R.id.timer_content, timeText);

        appWidgetManager.updateAppWidget(widget, views);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "타이머 채널", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("타이머")
                .setContentText("타이머가 실행 중입니다.")
                .setSmallIcon(R.drawable.arrow_back)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
}