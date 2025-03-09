package com.example.parttimecalander.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationDismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context, TimerService.class));
    }
}