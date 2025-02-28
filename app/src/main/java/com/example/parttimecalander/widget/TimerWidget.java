package com.example.parttimecalander.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.parttimecalander.R;
import com.example.parttimecalander.timer.TimerService;

import java.util.Timer;

public class TimerWidget extends AppWidgetProvider {
    String timer_content_string = "00:00:00";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String timeText) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.timer_widget);
        // 제목과 남은 시간 설정
        views.setTextViewText(R.id.timer_title, "퇴근까지 남은 시간");
        views.setTextViewText(R.id.timer_content, timeText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //FATAL!!: broadCAST를 수신하지 않고 TimerService에서 직접 업데이트함

        if (intent.getAction().equals(TimerService.TIMER_WIDGET_BROADCAST)) {
            timer_content_string = intent.getStringExtra("remaining_time");
            Log.d("TimerWidget", "Remaining time: " + timer_content_string);
            // 모든 위젯 업데이트
            updateAllWidgets(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, timer_content_string);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widget = new ComponentName(context, TimerWidget.class);

        // 모든 위젯 ID 가져오기
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widget);

        for (int appWidgetId : appWidgetIds) {
            // 위젯 레이아웃 가져오기
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.timer_widget);

            views.setTextViewText(R.id.timer_content, timer_content_string);
            // 위젯 업데이트
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}