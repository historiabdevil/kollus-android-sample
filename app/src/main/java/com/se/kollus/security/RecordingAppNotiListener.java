package com.se.kollus.security;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class RecordingAppNotiListener extends NotificationListenerService {
    public final static String TAG = "RecordingAppNotiListener";

    private boolean isRecordingIssue(String text){
        String[] strBan = new String[]{
                "녹화", "캡쳐", "record", "capture"
        };
        String low = text.toLowerCase();
        for(String ban : strBan){
            if(low.contains(ban)) return true;
        }
        return false;
    }
    private boolean isRecordingIssue(Bundle extras){
        String title = extras.getString(Notification.EXTRA_TITLE);
        if (title !=null && isRecordingIssue(title)) return true;
        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
        if (text != null && isRecordingIssue(text.toString())) return true;
        CharSequence subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
        if (subText != null && isRecordingIssue(subText.toString())) return true;
        return false;
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        RecordingAppObserver.getInstance().observe(getPackageManager());
        StatusBarNotification[] statusBarNotifications = getActiveNotifications();
        for (StatusBarNotification sbn : statusBarNotifications){
            if(RecordingAppObserver.getInstance().getRecordingApps().contains(sbn.getPackageName())){
                RecordingAppObserver.getInstance().addNotiApp(sbn.getPackageName());
            }
            if(isRecordingIssue(sbn.getNotification().extras)){
                RecordingAppObserver.getInstance().getRecordingApps().add(sbn.getPackageName());
                RecordingAppObserver.getInstance().addNotiApp(sbn.getPackageName());
            }
        }
        if(!RecordingAppObserver.getInstance().isClear()){
            Intent intent = new Intent("RecordingAppNoti");
            intent.putExtra("message", "Running Recording App Foreground Service");
            intent.putExtra("count", RecordingAppObserver.getInstance().getOnNotirecordingApps().size());
            intent.putExtra("list", RecordingAppObserver.getInstance().getOnNotirecordingApps().toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        if(RecordingAppObserver.getInstance().getRecordingApps().contains(sbn.getPackageName())){
            RecordingAppObserver.getInstance().removeNotiApp(sbn.getPackageName());
            Intent intent = new Intent("RecordingAppNoti");
            intent.putExtra("message", "Remove Recording App Foreground Service");
            intent.putExtra("count", RecordingAppObserver.getInstance().getOnNotirecordingApps().size());
            intent.putExtra("list", RecordingAppObserver.getInstance().getOnNotirecordingApps().toString());
            intent.putExtra("current", sbn.getPackageName());
            intent.putExtra("type", "REMOVE");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if(RecordingAppObserver.getInstance().getRecordingApps().contains(sbn.getPackageName())){
            RecordingAppObserver.getInstance().addNotiApp(sbn.getPackageName());
        }
        if(isRecordingIssue(sbn.getNotification().extras)){
            RecordingAppObserver.getInstance().getRecordingApps().add(sbn.getPackageName());
            RecordingAppObserver.getInstance().addNotiApp(sbn.getPackageName());
        }
        if(!RecordingAppObserver.getInstance().isClear()){
            Intent intent = new Intent("RecordingAppNoti");
            intent.putExtra("message", "Add Recording App Foreground Service");
            intent.putExtra("count", RecordingAppObserver.getInstance().getOnNotirecordingApps().size());
            intent.putExtra("list", RecordingAppObserver.getInstance().getOnNotirecordingApps().toString());
            intent.putExtra("current", sbn.getPackageName());
            intent.putExtra("type", "ADD");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

}
