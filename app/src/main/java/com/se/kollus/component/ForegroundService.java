package com.se.kollus.component;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.kollus.sdk.media.MediaPlayer;
import com.se.kollus.activity.AudioPlayerActivity;
import com.se.kollus.R;
import com.se.kollus.activity.LivePlayerActivity;
import com.se.kollus.activity.MainActivity;
import com.se.kollus.activity.VodPlayerActivity;
import com.se.kollus.data.ContentTypes;


public class ForegroundService extends Service implements MediaPlayer.OnPreparedListener{
    public static final String CHANNEL_ID = "EXAMPLESERVICE";

    @Override
    public void onCreate() {
        super.onCreate();
    }
    private MediaPlayer mediaPlayer;
    private int position;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String playUrl = intent.getExtras().get("playUrl").toString();
        String title = intent.getExtras().get("title").toString();
        ContentTypes contentTypes = (ContentTypes) intent.getExtras().get("type");
        int serverPort = intent.getIntExtra("serverPort", 8388);
        position = intent.getIntExtra("position", 0);
        this.mediaPlayer = new MediaPlayer(getApplicationContext(), MainActivity.kollusStorage, serverPort);
        this.mediaPlayer.setOnPreparedListener(this);
        this.mediaPlayer.setDataSourceByUrl(playUrl, null);
        this.mediaPlayer.prepareAsync();
        createNotificationChannel();
        Intent notificationIntent = null;
        switch (contentTypes) {
            case AOD:
                notificationIntent = new Intent(this, AudioPlayerActivity.class);
                break;
            case VOD:
                notificationIntent = new Intent(this, VodPlayerActivity.class);
                break;
            case LIVE:
                notificationIntent = new Intent(this, LivePlayerActivity.class);
                break;
        }
        notificationIntent.putExtra("playUrl", playUrl);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        notificationIntent.setAction(Intent.ACTION_VIEW);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1001, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Kollus Player Foreground Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mediaPlayer.stop();
        this.mediaPlayer.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        mediaPlayer.seekToExact(position);
    }
}
