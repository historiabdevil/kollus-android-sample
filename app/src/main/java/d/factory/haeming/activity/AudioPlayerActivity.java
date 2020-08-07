package d.factory.haeming.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
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

import com.kollus.sdk.media.CaptureDetectLister;
import com.kollus.sdk.media.KollusPlayerLMSListener;
import com.kollus.sdk.media.MediaPlayer;
import com.kollus.sdk.media.content.BandwidthItem;
import com.kollus.sdk.media.content.KollusContent;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import d.factory.haeming.R;


public class AudioPlayerActivity extends AppCompatActivity  {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        playUrl = Uri.parse(intent.getExtras().getString("playUrl"));
        if(playUrl == null){
            finish();
        }

        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        getApplicationContext().sendBroadcast(i);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.audioplayer_view);
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
        initEventListener();
        initPlayer(getApplicationContext(), 8388);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(surfaceCallback);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    boolean prepared = false;
    private int jump_step = 5000;
    private View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnPlay:
                    if(!mMediaPlayer.isPlaying() && prepared){
                        mMediaPlayer.start();
                    }
                    break;
                case R.id.btnPause:
                    if(mMediaPlayer.isPlaying()){
                        mMediaPlayer.pause();
                    }
                    break;
                case R.id.btnRew:
                    int current = mMediaPlayer.getCurrentPosition();
                    mMediaPlayer.seekTo(current - 5000 > 0 ? current - 5000 : 0);
                    mMediaPlayer.start();
                    break;
                case R.id.btnFF:
                    int current2 = mMediaPlayer.getCurrentPosition();
                    int duration = mMediaPlayer.getDuration();
                    int position = current2 + 5000 > duration ? duration : current2 + 5000;
                    mMediaPlayer.seekTo(position);
                    mMediaPlayer.start();
                    break;
                case R.id.btnMute:
                    isMute = !isMute;
                    mMediaPlayer.setMute(isMute);
                    break;
                case R.id.close:
                    if(mMediaPlayer.isPlaying()){
                        mMediaPlayer.pause();
                        mMediaPlayer.stop();
                    }
                    isClose = true;
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mMediaPlayer.release();
                    finish();
                    break;
            }
        }
    };
    private void initEventListener(){
        btnPlay.setOnClickListener(clickListener);
        btnPause.setOnClickListener(clickListener);
        btnRew.setOnClickListener(clickListener);
        btnFF.setOnClickListener(clickListener);
        btnMute.setOnClickListener(clickListener);
        close.setOnClickListener(clickListener);

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void initPlayer(Context context, int serverPort) {
        mMediaPlayer = new MediaPlayer(context, MainActivity.kollusStorage, serverPort);


        mMediaPlayer.setOnCencDrmListener(mCencDrmListener);
        mMediaPlayer.setOnPreparedListener(mPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
        mMediaPlayer.setOnCompletionListener(mCompletionListener);
        mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
        mMediaPlayer.setOnErrorListener(mErrorListener);
        mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
        mMediaPlayer.setOnInfoListener(mInfoListener);
        mMediaPlayer.setKollusPlayerLMSListener(mKollusPlayerLMSListener);
        mMediaPlayer.setOnExternalDisplayDetectListener(mOnExternalDisplayDetectListener);
        mMediaPlayer.setCaptureDetectLister(mCaptureDetectLister);
        mMediaPlayer.setOnTimedTextDetectListener(mOnTimedTextDetectListener);

        mMediaPlayer.setOnTimedTextListener(mOnTimedTextListener);
        mMediaPlayer.setDataSourceByUrl(playUrl.toString(), "");
        volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    private MediaPlayer.OnTimedTextDetectListener mOnTimedTextDetectListener = new MediaPlayer.OnTimedTextDetectListener() {
        @Override
        public void onTimedTextDetect(MediaPlayer mediaPlayer, int i) {
            Log.i("Subtitle Detect : ", String.format("%d", i));
            mediaPlayer.selectTrack(i);
        }
    };

    int current_subtitle = 3;
    private MediaPlayer.OnTimedTextListener mOnTimedTextListener = new MediaPlayer.OnTimedTextListener() {
        @Override
        public void onTimedText(MediaPlayer mediaPlayer, String s) {
            if(s != null) {
                Log.i("Subtitle : ", s);


                int cnt_subtitle = kollusContent.getSubtitleInfo().size();
                current_subtitle = current_subtitle + 1 < cnt_subtitle ? current_subtitle + 1 : 0;
                try {
                    Log.i("Subtitle Select: ", String.format("%d", current_subtitle));
                    Log.i("Subtitle Select: ", kollusContent.getSubtitleInfo().get(current_subtitle).url);
//                    mediaPlayer.addTimedTextSource(kollusContent.getSubtitleInfo().get(current_subtitle).url);
//                    mediaPlayer.pause();
                    mediaPlayer.addTimedTextSource(null, Uri.fromFile(new File(kollusContent.getSubtitleInfo().get(current_subtitle).url.replace("file://", ""))));
//                    mediaPlayer.start();
                } catch (IOException e) {
                    Log.e("Subtittle Error: ", e.getLocalizedMessage());
                }
            }
        }

        @Override
        public void onTimedImage(MediaPlayer mediaPlayer, byte[] bytes, int i, int i1) {
            Log.i("Subtitle Image", String.format("%d   %d", i, i1));
        }
    };

    private MediaPlayer.OnCencDrmListener mCencDrmListener = new MediaPlayer.OnCencDrmListener() {
        @Override
        public void onProxyError(int i, String s) {
            Log.d("DRM CENC Error : ", s);
        }
    };

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(final MediaPlayer mediaPlayer) {
            kollusContent = new KollusContent();
            if(mediaPlayer.getKollusContent(kollusContent)){
                Log.i("", kollusContent.toString());
                contentTitle.setText(kollusContent.getSubCourse());
                kollusContent.setDisablePlayRate(false);
                progressBar.setMax(kollusContent.getDuration());
//                Glide.with(getApplicationContext()).load(kollusContent.getThumbnailPath()).into(poster);
//                Glide.with(getApplicationContext()).load(kollusContent.getThumbnailPath()).into(contentImage);
                surfaceView.bringToFront();
                t.start();
                prepared = true;

                for(KollusContent.SubtitleInfo info : kollusContent.getSubtitleInfo()){
                    Log.i("Subtitle Info : ", info.url);
                }
//                for(MediaPlayerBase.TrackInfo inf : mediaPlayer.getTrackInfo()){
//                    Log.i("TrackInfo", inf.getLanguage());
//                }
                Uri subtitleUri = Uri.fromFile(new File(kollusContent.getSubtitleInfo().get(0).url));
                try {

//                    mediaPlayer.addTimedTextSource(getBaseContext(), Uri.parse(kollusContent.getSubtitleInfo().get(current_subtitle).url));
                    mediaPlayer.addTimedTextSource(kollusContent.getSubtitleInfo().get(current_subtitle).url);
                } catch (IOException e) {
                    Log.e("Subtittle Error: ", e.getLocalizedMessage());
                }
                mediaPlayer.start();

            }
        }
    };
    private MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
        }
    };
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

        }
    };
    private MediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {

        }
    };
    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            return false;
        }
    };
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

        }
    };
    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
            return false;
        }

        @Override
        public void onBufferingStart(MediaPlayer mediaPlayer) {

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

        }

        @Override
        public void onChangedBandwidth(MediaPlayer mediaPlayer, BandwidthItem bandwidthItem) {

        }

        @Override
        public void onCodecInitFail(MediaPlayer mediaPlayer, String s) {

        }

    };
    private KollusPlayerLMSListener mKollusPlayerLMSListener = new KollusPlayerLMSListener() {
        @Override
        public void onLMS(String s, String s1) {

        }
    };
    private MediaPlayer.OnExternalDisplayDetectListener mOnExternalDisplayDetectListener = new MediaPlayer.OnExternalDisplayDetectListener() {
        @Override
        public void onExternalDisplayDetect(int i, boolean b) {

        }
    };
    private CaptureDetectLister mCaptureDetectLister = new CaptureDetectLister() {
        @Override
        public void onCaptureDetected(String s, String s1) {

        }
    };

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            

            if (mMediaPlayer != null && holder != null) {
                mMediaPlayer.setDisplay(holder);
                mMediaPlayer.prepareAsync();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            if (mMediaPlayer != null) {
                mMediaPlayer.destroyDisplay();
            }
        }
    };

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() { // Thread 로 작업할 내용을 구현
            while(!isClose) {

                if (mMediaPlayer.isPlaying()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() { // 화면에 변경하는 작업을 구현
                            progressBar.setProgress(mMediaPlayer.getCurrentPosition());
                        }
                    });

                    try {
                        Thread.sleep(100); // 시간지연
                    } catch (InterruptedException e) {    }
                }
            } // end of while
        }
    });







}
