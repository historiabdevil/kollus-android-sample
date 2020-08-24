package com.se.kollus.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.kollus.sdk.media.KollusPlayerLMSListener;
import com.kollus.sdk.media.MediaPlayer;
import com.kollus.sdk.media.content.BandwidthItem;
import com.kollus.sdk.media.content.KollusContent;

import com.se.kollus.ui.control.LiveControlView;
import com.se.kollus.R;
import com.se.kollus.component.ScreenBrightnessObserver;
import com.se.kollus.component.ForegroundService;
import com.se.kollus.component.AudioStreamVolumeObserver;
import com.se.kollus.component.AudioStreamVolumeObserver.OnAudioStreamVolumeChangedListener;




import java.util.List;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LivePlayerActivity extends AppCompatActivity
        implements MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnExternalDisplayDetectListener,
        MediaPlayer.OnBufferingUpdateListener,
        KollusPlayerLMSListener,
        OnAudioStreamVolumeChangedListener,
        ScreenBrightnessObserver.OnScreenBrightnessChangedListener,
        SurfaceHolder.Callback,
        LiveControlView.OnControlViewEventListener,
        View.OnClickListener {

    private static final String LOG_TAG = "LIVEPLAYER";
    //UI Control
    private LiveControlView liveControlView;

    //Kollus SDK
    private MediaPlayer mediaPlayer;
    private KollusContent kollusContent = new KollusContent();
    private SurfaceView videoSurfaceView;
    private Uri playUrl;
    private int serverPort;

    //System Configuration (Volume, Brightness...)

    private AudioStreamVolumeObserver audioStreamVolumeObserver;
    private ScreenBrightnessObserver screenBrightnessObserver;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.playUrl = this.getPlayUrl();
        if (this.playUrl == null) finish();

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        this.setContentView(R.layout.live_player_view);


    }



    @Override
    protected void onStart() {
        super.onStart();

        this.videoSurfaceView = (SurfaceView) this.findViewById(R.id.videoSurfaceView);
        this.videoSurfaceView.setSecure(true);
        this.videoSurfaceView.getHolder().addCallback(this);
        this.videoSurfaceView.setOnClickListener(this);
        this.liveControlView = (LiveControlView) this.findViewById(R.id.liveControlView);
        this.liveControlView.setListener(this);
        this.liveControlView.setStatus(LiveControlView.PlayerStatus.NOT_PREPARED);
        this.audioStreamVolumeObserver = new AudioStreamVolumeObserver(this.getApplicationContext());
        this.audioStreamVolumeObserver.start(AudioManager.STREAM_MUSIC, this);
        this.screenBrightnessObserver = new ScreenBrightnessObserver(this.getApplicationContext());
        try {
            this.screenBrightnessObserver.start(this);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getLocalizedMessage());
        }
    }

    private void initPlayer(Context context, int serverPort) {
        this.mediaPlayer = new MediaPlayer(context, MainActivity.kollusStorage, serverPort);
        this.mediaPlayer.setSurface(this.videoSurfaceView.getHolder().getSurface());

        //MediaPlayer Register Listener
        this.mediaPlayer.setOnInfoListener(this);
        this.mediaPlayer.setOnPreparedListener(this);
        this.mediaPlayer.setOnVideoSizeChangedListener(this);
        this.mediaPlayer.setOnErrorListener(this);
        this.mediaPlayer.setOnBufferingUpdateListener(this);
        this.mediaPlayer.setKollusPlayerLMSListener(this);
        this.mediaPlayer.setOnExternalDisplayDetectListener(this);

        //determine url & prepare
        this.mediaPlayer.setDataSourceByUrl(this.playUrl.toString(), null);
        this.mediaPlayer.prepareAsync();
    }

    private void releasePlayer() {
        if (this.mediaPlayer != null) {
            mediaPlayer.destroyDisplay();
        }
        mediaPlayer.release();
    }

    private Uri getPlayUrl() {
        Intent intent = getIntent();
        String strPlayUrl = intent.getExtras().getString("playUrl");
        if (strPlayUrl != null && !strPlayUrl.isEmpty()) {
            Uri uri = Uri.parse(strPlayUrl);
            String firstPath = uri.getPathSegments().get(0);
            if ("v-live-kr.kollus.com".equals(uri.getHost()) &&
                    ("si".equals(firstPath) || "i".equals(firstPath))) {
                return uri;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    //MediaPlayer Event Handler

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
        this.liveControlView.setStatus(LiveControlView.PlayerStatus.BUFFERING);

    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onFrameDrop(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onDownloadRate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onDetectBandwidthList(MediaPlayer mediaPlayer, List<BandwidthItem> list) {
        this.liveControlView.setResolutionList(list);
    }

    @Override
    public void onChangedBandwidth(MediaPlayer mediaPlayer, BandwidthItem bandwidthItem) {
        this.liveControlView.changedBandwidth(bandwidthItem);

    }

    @Override
    public void onCodecInitFail(MediaPlayer mediaPlayer, String s) {

    }


    private void fitAspectRatio(){
        int videowidth = mediaPlayer.getVideoWidth();
        int videoheight = mediaPlayer.getVideoHeight();

        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int surfaceHeight = videoheight * size.x / videowidth;
        videoSurfaceView.getHolder().setFixedSize(size.x, surfaceHeight);
    }
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        liveControlView.setStatus(LiveControlView.PlayerStatus.PREPARED);
        mediaPlayer.getKollusContent(kollusContent);
        liveControlView.setTitle(kollusContent.getSubCourse());
        fitAspectRatio();
        mediaPlayer.start();
        liveControlView.setStatus(LiveControlView.PlayerStatus.PLAY);
    }


    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {

    }


    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        return false;
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {

    }


    @Override
    public void onLMS(String request, String response) {

    }


    @Override
    public void onExternalDisplayDetect(int i, boolean b) {

    }


    @Override
    public void onAudioStreamVolumeChanged(int audioStreamType, int volume) {

    }

    @Override
    public void onScreenBrightnessChangedListener(int mode, float value) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = (float) value / 100;
        getWindow().setAttributes(params);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Surface 생성시 플레이어 초기화
        Log.i(LOG_TAG, "surface created");
        initPlayer(this.getApplicationContext(), serverPort);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        fitAspectRatio();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(LOG_TAG, "surface destroy");
        releasePlayer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fitAspectRatio();
    }

    @Override
    protected void onPause() {
        super.onPause();
        startService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService();
    }


    @Override
    public void OnPlayAndPauseButtonClicked(LiveControlView liveControlView, LiveControlView.PlayerStatus status) {
        switch (status) {
            case PAUSE:
            case PREPARED:
                this.mediaPlayer.start();
                this.liveControlView.setStatus(LiveControlView.PlayerStatus.PLAY);
                break;
            case PLAY:
                this.mediaPlayer.pause();
                this.liveControlView.setStatus(LiveControlView.PlayerStatus.PAUSE);
                break;
        }
    }

    @Override
    public void OnBackButtonClicked(LiveControlView liveControlView) {
        this.releasePlayer();
        finish();
    }

    @Override
    public void OnResolutionButtonClicked(LiveControlView liveControlView) {
        Log.i(LOG_TAG, "Resolution Button Clicked");
    }

    @Override
    public void OnResolutionChanged(BandwidthItem item) {
        this.mediaPlayer.setBandwidth(item.getBandwidthName());
    }

    @Override
    public void OnScreenLockButtonClicked(LiveControlView liveControlView, boolean screenLocked) {
        Log.i(LOG_TAG, "Screen Lock Button Clicked screen lock : " + screenLocked);

    }

    @Override
    public void OnMuteButtonClicked(LiveControlView liveControlView, boolean muted) {
        Log.i(LOG_TAG, "Screen Lock Button Clicked screen lock : " + muted);
        audioStreamVolumeObserver.setMute(muted);
    }

    @Override
    public void OnBrightnessUpTouched(LiveControlView liveControlView) {
        float currentBrightness = screenBrightnessObserver.getBrightness();
        screenBrightnessObserver.setBrightness(currentBrightness + 10.0f);
        liveControlView.setBrightness(currentBrightness + 10.0f);
    }

    @Override
    public void OnBrightnessDownTouched(LiveControlView liveControlView) {
        float currentBrightness = screenBrightnessObserver.getBrightness();
        screenBrightnessObserver.setBrightness(currentBrightness - 10.0f);
        liveControlView.setBrightness(currentBrightness - 10.0f);
    }

    @Override
    public void OnVolumeUpTouched(LiveControlView liveControlView) {
        int currentVolume = audioStreamVolumeObserver.getVolume();
        audioStreamVolumeObserver.setVolume(currentVolume + 1);
        liveControlView.setVolume(currentVolume + 1);
    }

    @Override
    public void OnVolumeDownTouched(LiveControlView liveControlView) {
        int currentVolume = audioStreamVolumeObserver.getVolume();
        audioStreamVolumeObserver.setVolume(currentVolume - 1);
        liveControlView.setVolume(currentVolume - 1);
    }

    @Override
    public void OnFullscreenButtonClicked(LiveControlView liveControlView, boolean fullscreen) {

    }


    private Handler liveControlViewHandler = new Handler();
    @Override
    public void onClick(View v) {
        Log.i(LOG_TAG, String.format("%d",v.getId()));
        switch (v.getId()){
            case R.id.videoSurfaceView:
                Log.i(LOG_TAG, "Clicked Video SurfaceView");
                this.liveControlView.setVisibility(View.VISIBLE);
//                this.liveControlView.bringToFront();
                liveControlViewHandler.removeCallbacksAndMessages(null);
                liveControlViewHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        liveControlView.setVisibility(View.GONE);
//                        videoSurfaceView.bringToFront();
                    }
                }, 10 * 1000L);
                break;
        }
    }
    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("playUrl", this.playUrl);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

}
