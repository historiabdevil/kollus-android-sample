package d.factory.haeming.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kollus.sdk.media.CaptureDetectLister;
import com.kollus.sdk.media.KollusPlayerLMSListener;
import com.kollus.sdk.media.MediaPlayer;
import com.kollus.sdk.media.content.BandwidthItem;
import com.kollus.sdk.media.content.KollusBookmark;
import com.kollus.sdk.media.content.KollusContent;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import d.factory.haeming.R;
import d.factory.haeming.component.ForegroundService;
import d.factory.haeming.data.ContentTypes;
import d.factory.haeming.data.EncryptTypes;
import d.factory.haeming.exception.KollusException;
import d.factory.haeming.player.PlayerStates;
import d.factory.haeming.player.PlayerWrapper;
import d.factory.haeming.player.PlayerWrapperEventListener;


public class AudioPlayerActivity extends AppCompatActivity
implements PlayerWrapperEventListener {

    private TextView contentTitle;
    private SurfaceView surfaceView;
    private ImageView contentImage;
    private ProgressBar progressBar;
    private ImageView poster;
    private ImageButton btnPlay;
    private ImageButton btnPause;
    private ImageButton btnRew;
    private ImageButton btnFF;
    private ImageButton btnMute;
    private SeekBar volumeBar;
    private ImageButton close;

    Handler handler = new Handler();

    private MediaPlayer mMediaPlayer;
    private AudioManager audioManager;
    private Uri playUrl;
    private KollusContent kollusContent;
    private boolean isClose;
    private boolean isMute;

    private PlayerWrapper player;


    private void loadUI() {
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        contentTitle = (TextView) findViewById(R.id.contentTitle);
        contentImage = (ImageView) findViewById(R.id.contentImage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        poster = (ImageView) findViewById(R.id.poster);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnPause = (ImageButton) findViewById(R.id.btnPause);
        btnRew = (ImageButton) findViewById(R.id.btnRew);
        btnFF = (ImageButton) findViewById(R.id.btnFF);
        btnMute = (ImageButton) findViewById(R.id.btnMute);
        volumeBar = (SeekBar) findViewById(R.id.seekBar);
        close = (ImageButton) findViewById(R.id.close);
        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        playUrl = Uri.parse(intent.getExtras().getString("playUrl"));
        if (playUrl == null) {
            finish();
        }

        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        getApplicationContext().sendBroadcast(i);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.audioplayer_view);
        loadUI();
        initEventListener();
        this.player = new PlayerWrapper(this.getApplicationContext(), MainActivity.kollusStorage,
                playUrl, ContentTypes.AOD, EncryptTypes.KOLLUS, this.surfaceView, 7772);
        this.player.setPlayerWrapperEventListener(this);
        this.player.setAutoplay(true);
        try {
            this.player.init();
        } catch (KollusException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                switch (view.getId()) {
                    case R.id.btnPlay:
                        player.play();
                        break;
                    case R.id.btnPause:
                        player.pause();
                        break;
                    case R.id.btnRew:
                        player.rewind();
                        break;
                    case R.id.btnFF:
                        player.forward();
                        break;
                    case R.id.btnMute:
                        player.mute();
                        break;
                    case R.id.close:
                        player.release();
                        finish();
                        break;
                }
            } catch (KollusException kex) {

            }
        }

    };

    private void initEventListener() {
        btnPlay.setOnClickListener(clickListener);
        btnPause.setOnClickListener(clickListener);
        btnRew.setOnClickListener(clickListener);
        btnFF.setOnClickListener(clickListener);
        btnMute.setOnClickListener(clickListener);
        close.setOnClickListener(clickListener);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                player.setVolume(value);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void progress(int current, PlayerStates playerState) {
        this.progressBar.setProgress(current, true);
        if(current % 10000 < 500){
            try {
                player.addOrUpdateBookmark(current, "북마크!");
            } catch (KollusException e) {
                Log.i(getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    @Override
    public void loadBookmark() {
        List<KollusBookmark> bookmarks = this.player.listBookmarks();
        Log.i(getClass().getSimpleName(), "Bookmark list size : " + bookmarks.size());
        for (KollusBookmark bookmark : bookmarks){
            Log.i(getClass().getSimpleName(), bookmark.toString());
        }
    }

    @Override
    public void prepared() {
        this.progressBar.setMax(this.player.getDuration());
        this.contentTitle.setText(this.player.getTitle());
        Glide.with(getApplicationContext()).load(this.player.getPosterUrl()).into(poster);
        //for test
        try {
            this.player.setRepeatA(5000);
            this.player.setRepeatB(10000);
        } catch (KollusException e) {
            Log.i(getClass().getSimpleName(), e.getMessage());
        }
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
