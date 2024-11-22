package com.example.parttimecalander.timer;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Database.WorkDailyDatabase;
import com.example.parttimecalander.Database.WorkDaily;
import com.example.parttimecalander.R;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {
    public static final String ACTION_UPDATE_TIMER = "com.example.parttimecalander.timer.ACTION_UPDATE_TIMER";
    public static final String EXTRA_TIME_LEFT = "EXTRA_TIME_LEFT";
    private CountDownTimer countDownTimer;
    private Handler handler = new Handler();
    private Runnable runnable;
    WorkDailyDao dailyDao;
    WorkDailyDatabase dailyDatabase;
    private ScheduledExecutorService scheduler;
    private LocalDateTime lastCheckedDate;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dailyDatabase = WorkDailyDatabase.getDatabase(this);
        dailyDao = dailyDatabase.workDailyDao();

        // 서비스가 시작될 때 현재 날짜 설정
        lastCheckedDate = LocalDateTime.now().toLocalDate().atStartOfDay();
        int year = lastCheckedDate.getYear();
        int month = lastCheckedDate.getMonthValue();
        int day = lastCheckedDate.getDayOfMonth();
        String todayString = String.format("%04d-%02d-%02d", year, month, day);

        // 오늘의 일정 데이터를 가져옴
        List<WorkDaily> workDailyList = dailyDao.getSchedulesForDate(todayString);

        // 매시간마다 현재 시간을 확인하여 날짜가 바뀌었는지 확인
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> checkDateAndUpdate(workDailyList), 0, 1, TimeUnit.HOURS);


        // Foreground 서비스로 설정
        //startForeground(1, createNotification("Service running"));

        return START_STICKY;
    }

    // 현재 날짜를 체크하여 날짜가 바뀌면 일정을 새로 받아옴
    private void checkDateAndUpdate(List<WorkDaily> workDailyList) {
        LocalDateTime now = LocalDateTime.now().toLocalDate().atStartOfDay();

        // 날짜가 바뀌었으면 새로 일정을 받아옴
        if (!now.isEqual(lastCheckedDate)) {
            lastCheckedDate = now; // 날짜 업데이트
            Log.d("TimerService", "Date changed, updating schedule...");

            int year = lastCheckedDate.getYear();
            int month = lastCheckedDate.getMonthValue();
            int day = lastCheckedDate.getDayOfMonth();
            String todayString = String.format("%04d-%02d-%02d", year, month, day);

            // 새로운 날짜의 일정을 가져오고 타이머를 새로 시작
            workDailyList.clear();  // 이전 일정을 지우고
            workDailyList.addAll(dailyDao.getSchedulesForDate(todayString));  // 새 일정을 받아옴
            startTimersForToday(workDailyList);  // 새 타이머 시작
        }
    }
    // 오늘의 일정에 대해 타이머를 시작하는 메서드
    private void startTimersForToday(List<WorkDaily> workDailyList) {
        LocalDateTime now = LocalDateTime.now();

        // 오늘 일정에 대해 각 일정마다 타이머를 시작합니다.
        for (WorkDaily workDaily : workDailyList) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

            // 시작 시간과 종료 시간을 LocalDateTime으로 변환
            //startTime: yyyy-mm-dd 00:00:00
            String start = workDaily.startTime + ".000";
            String end = workDaily.endTime + ".000";
            LocalDateTime startTime = LocalDateTime.parse(start, formatter);
            LocalDateTime endTime = LocalDateTime.parse(end, formatter);

            // 현재 시간과 시작 시간 비교하여 타이머 시작
            if (now.isAfter(startTime)) {
                long startDelay = Duration.between(now, startTime).toMillis();
                long duration = Duration.between(startTime, endTime).toMillis();

                // 일정의 시작 시간이 되었을 때 타이머 시작
                new CountDownTimer(startDelay, 10000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // 시작까지 남은 시간 업데이트
                        Log.d("Timer", "Time left to start: " + millisUntilFinished / 1000);
                    }

                    @Override
                    public void onFinish() {
                        // 타이머 시작 후 종료 시간까지의 카운트다운 시작
                        startEndTimer(duration);
                    }
                }.start();
            }
        }
    }

    private void startEndTimer(long duration) {
        // 종료 시간까지 카운트다운
        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 종료까지 남은 시간 업데이트
                broadcastTimeLeft(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                // 종료 시 타이머 종료
                Log.d("Timer", "Timer finished");
                broadcastTimeLeft(0);
            }
        }.start();
    }

    private void broadcastTimeLeft(long timeLeftMillis) {
        // 남은 시간을 텍스트 형식으로 만들어 Broadcast로 전송
        Intent intent = new Intent(ACTION_UPDATE_TIMER);
        intent.putExtra(EXTRA_TIME_LEFT, timeLeftMillis);
        sendBroadcast(intent);  // 메인 액티비티로 브로드캐스트 전송
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduler != null) {
            scheduler.shutdown();  // 서비스 종료 시 스케줄러 종료
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}