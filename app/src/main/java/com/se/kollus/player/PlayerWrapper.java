package com.se.kollus.player;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kollus.sdk.media.CaptureDetectLister;
import com.kollus.sdk.media.EmulatorCheckerListener;
import com.kollus.sdk.media.KollusPlayerBookmarkListener;
import com.kollus.sdk.media.KollusPlayerContentMode;
import com.kollus.sdk.media.KollusPlayerLMSListener;
import com.kollus.sdk.media.KollusStorage;
import com.kollus.sdk.media.MediaPlayer;
import com.kollus.sdk.media.content.BandwidthItem;
import com.kollus.sdk.media.content.KollusBookmark;
import com.kollus.sdk.media.content.KollusContent;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.se.kollus.component.AudioStreamVolumeObserver;
import com.se.kollus.component.ScreenBrightnessObserver;
import com.se.kollus.data.ContentTypes;
import com.se.kollus.data.EncryptTypes;
import com.se.kollus.exception.KollusException;

public class PlayerWrapper implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnCencDrmListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnTimedTextDetectListener,
        MediaPlayer.OnTimedTextListener,
        MediaPlayer.OnExternalDisplayDetectListener,
        KollusPlayerBookmarkListener,
        KollusPlayerLMSListener,
        CaptureDetectLister,
        EmulatorCheckerListener, SurfaceHolder.Callback, AudioStreamVolumeObserver.OnAudioStreamVolumeChangedListener, ScreenBrightnessObserver.OnScreenBrightnessChangedListener {

    private final String TAG = "KOLLUSPLAYERWRAPPER";
    private Context context;
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private KollusStorage kollusStorage;
    private KollusContent kollusContent;
    private int serverPort;
    private PlayerWrapperEventListener playerWrapperEventListener;

    private List<BandwidthItem> bandwidthItemList;
    private List<KollusBookmark> bookmarkList;

    private AudioStreamVolumeObserver audioStreamVolumeObserver;
    private ScreenBrightnessObserver screenBrightnessObserver;

    private Uri playUrl;

    private boolean muted;
    private ContentTypes contentType;
    private EncryptTypes encryptType;
    private PlayerStates playerState;

    private int volume = 0;
    private int volumeStep = 1;
    private float brightness = 0.0f;
    private float brightnessStep = 10.0f;

    private int seekStep = 5000;
    private int repeatA = -1;
    private int repeatB = -1;
    private boolean autoplay;


    private Thread progressThread;


    public PlayerWrapper() {
        this.playerState = PlayerStates.NONE;
    }

    public PlayerWrapper(Context context,
                         KollusStorage kollusStorage,
                         Uri playUrl,
                         ContentTypes contentType,
                         EncryptTypes encryptType,
                         SurfaceView surfaceView,
                         int serverPort) {
        this.playerState = PlayerStates.NONE;
        this.context = context;
        this.kollusStorage = kollusStorage;
        this.playUrl = playUrl;
        this.contentType = contentType;
        this.encryptType = encryptType;
        this.surfaceView = surfaceView;
        this.serverPort = serverPort;
    }
//region wrapper method
    public void init() throws KollusException {
        if (context == null) {
            throw new KollusException("CONTEXT 설정이 NULL 입니다.");
        }
        if (kollusStorage == null) {
            throw new KollusException("Kollus Storage 값이 NULL 입니다.");
        }
        if (serverPort <= 0) {
            throw new KollusException("Server Port 값이 설정 되어 있지 않습니다.");
        }
        if (playUrl == null) {
            throw new KollusException("재생 URL 값이 설정 되어 있지 않습니다.");
        }
        if (surfaceView == null) {
            throw new KollusException("재생할수 있는 뷰가 설정 되어 있지 않습니다.");
        }
        this.mediaPlayer = new MediaPlayer(this.context, kollusStorage, this.serverPort);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnCencDrmListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        mediaPlayer.setOnTimedTextDetectListener(this);
        mediaPlayer.setOnTimedTextListener(this);
        mediaPlayer.setOnExternalDisplayDetectListener(this);
        mediaPlayer.setKollusPlayerBookmarkListener(this);
        mediaPlayer.setKollusPlayerLMSListener(this);
        mediaPlayer.setCaptureDetectLister(this);
        mediaPlayer.setEmulatorCheckerListener(this);


        this.audioStreamVolumeObserver = new AudioStreamVolumeObserver(this.context);
        this.audioStreamVolumeObserver.start(AudioManager.STREAM_MUSIC, this);
        this.screenBrightnessObserver = new ScreenBrightnessObserver(this.context);
        try {
            this.screenBrightnessObserver.start(this);
        } catch (Exception ex) {
            throw new KollusException("화면 밝기 감시자 시작 Fail: " + ex.getMessage());
        }
        mediaPlayer.setDataSourceByUrl(playUrl.toString(), "");
        this.surfaceView.setSecure(true);
        this.mediaPlayer.setSurface(this.surfaceView.getHolder().getSurface());
        this.surfaceView.getHolder().addCallback(this);
    }

    public void release() throws KollusException {
        if (this.mediaPlayer != null) {
            if (this.playerState == PlayerStates.PLAY ||
                    this.playerState == PlayerStates.BUFFERING) {
                this.pause();
            }
            this.mediaPlayer.destroyDisplay();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
            this.audioStreamVolumeObserver.stop();
            this.audioStreamVolumeObserver = null;
            this.screenBrightnessObserver.stop();
            this.screenBrightnessObserver = null;
            this.playerState = PlayerStates.NONE;
            try {
                this.progressThread.join();
            } catch (InterruptedException e) {
                throw new KollusException("플레이어 릴리즈 중 에러가 발생하였습니다.");
            }
            this.progressThread = null;
        } else {
            throw new KollusException("플레이어는 이미 Release 된상태입니다.");
        }
    }

    public void prepare() throws KollusException {
        if (this.mediaPlayer == null) {
            throw new KollusException("플레이어가 초기화 되지 않았습니다.");
        }
        if (this.playUrl == null) {
            throw new KollusException("재생 URL 값이 설정 되어 있지 않습니다.");
        }
        if (this.playerState != PlayerStates.NONE) {
            throw new KollusException("이미 영상이 준비 되어 있습니다.");
        }
        this.mediaPlayer.prepareAsync();
    }

    public void play() throws KollusException {
        if (this.mediaPlayer == null) {
            throw new KollusException("플레이어가 초기화 되지 않았습니다.");
        }
        if (this.playerState == PlayerStates.NONE) {
            throw new KollusException("영상이 준비 되어 있지 않습니다.");
        }
        if (this.playerState == PlayerStates.PLAY) {
            throw new KollusException("영상이 이미 재생중입니다.");
        }
        if (this.playerState == PlayerStates.BUFFERING) {
            throw new KollusException("영상이 버퍼링중입니다. 잠시 기다려 주세요");
        }
        this.mediaPlayer.start();
    }

    public void pause() throws KollusException {
        if (this.mediaPlayer == null) {
            throw new KollusException("플레이어가 초기화 되지 않았습니다.");
        }
        if (this.playerState == PlayerStates.NONE) {
            throw new KollusException("영상이 준비 되어 있지 않습니다.");
        }
        if (this.playerState == PlayerStates.PAUSED) {
            throw new KollusException("영상이 이미 정지중입니다.");
        }
        if (this.playerState == PlayerStates.BUFFERING) {
            throw new KollusException("영상이 버퍼링중입니다. 잠시 기다려 주세요");
        }
        this.mediaPlayer.pause();
    }

    public void seek(int point) throws KollusException {
        if (this.mediaPlayer == null) {
            throw new KollusException("플레이어가 초기화 되지 않았습니다.");
        }
        if (this.playerState == PlayerStates.NONE) {
            throw new KollusException("영상이 준비 되어 있지 않습니다.");
        }
        if (this.contentType == ContentTypes.LIVE) {
            throw new KollusException("라이브영상은 지원하지 않습니다.");
        }

        if (this.kollusContent == null) {
            this.kollusContent = new KollusContent();
            this.mediaPlayer.getKollusContent(kollusContent);
        }
        int duration = kollusContent.getDuration();
        if (point < 0) {
            point = 0;
        }
        if (point > duration) {
            point = duration;
        }
        this.mediaPlayer.seekToExact(point);
        this.mediaPlayer.start();
    }

    public void rewind() throws KollusException {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        this.seek(currentPosition - seekStep);
    }

    public void forward() throws KollusException {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        this.seek(currentPosition + seekStep);
    }
    public void resetRepeatPosition() throws KollusException {
        this.setRepeatA(-1);
        this.setRepeatB(-1);
    }

    public void upVolume() {
        int currentVolume = audioStreamVolumeObserver.getVolume();
        volume = currentVolume + volumeStep;
        audioStreamVolumeObserver.setVolume(volume);

    }

    public void downVolume() {
        int currentVolume = audioStreamVolumeObserver.getVolume();
        volume = currentVolume - volumeStep;
        audioStreamVolumeObserver.setVolume(volume);
    }

    public void mute() {
        if (this.muted) {
            audioStreamVolumeObserver.setVolume(this.volume);
        } else {
            audioStreamVolumeObserver.setMute(true);
        }
        this.muted = !this.muted;
    }

    public void upBrightness() {
        float currentBrightness = screenBrightnessObserver.getBrightness();
        brightness = currentBrightness + brightnessStep;
        screenBrightnessObserver.setBrightness(brightness);
    }

    public void downBrightness() {
        float currentBrightness = screenBrightnessObserver.getBrightness();
        brightness = currentBrightness - brightnessStep;
        screenBrightnessObserver.setBrightness(brightness);
    }

    public void selectBitrate(String bandwithName) throws KollusException {
        if (this.mediaPlayer == null) {
            throw new KollusException("플레이어가 초기화 되지 않았습니다.");
        }
        if (this.playerState == PlayerStates.NONE) {
            throw new KollusException("영상이 준비 되어 있지 않습니다.");
        }
        BandwidthItem selectedBandwidth = null;
        for (BandwidthItem bandwidthItem : this.bandwidthItemList) {
            if (bandwithName.equals(bandwidthItem.getBandwidthName())) {
                selectedBandwidth = bandwidthItem;
                break;
            }
        }
        if (selectedBandwidth == null) {
            throw new KollusException(String.format("해당하는 Bandwidth(%s)이 없습니다.", bandwithName));
        }
        this.mediaPlayer.setBandwidth(bandwithName);
    }

    public void selectBitrate(int bandwith) throws KollusException {
        if (this.mediaPlayer == null) {
            throw new KollusException("플레이어가 초기화 되지 않았습니다.");
        }
        if (this.playerState == PlayerStates.NONE) {
            throw new KollusException("영상이 준비 되어 있지 않습니다.");
        }
        BandwidthItem selectedBandwidth = null;
        for (BandwidthItem bandwidthItem : this.bandwidthItemList) {
            if (bandwith == bandwidthItem.getBandwidth()) {
                selectedBandwidth = bandwidthItem;
                break;
            }
        }
        if (selectedBandwidth == null) {
            throw new KollusException(String.format("해당하는 Bandwidth(%d)이 없습니다.", bandwith));
        }
        this.mediaPlayer.setBandwidth(selectedBandwidth.getBandwidthName());
    }

    public void selectSubtitleByName(String subtitleName) throws KollusException {
        if (this.mediaPlayer == null) {
            throw new KollusException("플레이어가 초기화 되지 않았습니다.");
        }
        if (this.playerState == PlayerStates.NONE) {
            throw new KollusException("영상이 준비 되어 있지 않습니다.");
        }
        if (this.kollusContent == null) {
            throw new KollusException("영상이 정상적으로 로드 되지 않았습니다.");
        }
        if (this.contentType == ContentTypes.LIVE) {
            throw new KollusException("라이브영상은 지원하지 않습니다.");
        }
        List<KollusContent.SubtitleInfo> subtitleInfoList = this.kollusContent.getSubtitleInfo();
        KollusContent.SubtitleInfo selectedSubtitleInfo = null;
        for (int index = 0; index < subtitleInfoList.size(); index++) {
            KollusContent.SubtitleInfo subtitleInfo = subtitleInfoList.get(index);
            if (subtitleInfo.name.equals(subtitleName)) {
                selectedSubtitleInfo = subtitleInfo;
                break;
            }
        }
        if (selectedSubtitleInfo == null) {
            throw new KollusException(String.format("해당하는 자막(%s)이 없습니다.", subtitleName));
        }
        try {
            this.mediaPlayer.addTimedTextSource(context, Uri.fromFile(new File(selectedSubtitleInfo.url.replace("file://", ""))));
        } catch (IOException e) {
            throw new KollusException(String.format("%s 자막을 적용하는데 문제가 발생하였습니다.\n%s", subtitleName, e.getMessage()));
        }
    }

    public List<KollusBookmark> listBookmarks() {
        return this.bookmarkList;
    }

    public void selectBookmark(int bookmarkIndex) throws KollusException {
        if (this.mediaPlayer == null) {
            throw new KollusException("플레이어가 초기화 되지 않았습니다.");
        }
        if (this.playerState == PlayerStates.NONE) {
            throw new KollusException("영상이 준비 되어 있지 않습니다.");
        }
        if (this.kollusContent == null) {
            throw new KollusException("영상이 정상적으로 로드 되지 않았습니다.");
        }
        if (this.contentType == ContentTypes.LIVE) {
            throw new KollusException("라이브영상은 지원하지 않습니다.");
        }

        if (listBookmarks() == null) {
            throw new KollusException("북마크가 없습니다.");
        }
        if (bookmarkIndex < 0 || listBookmarks().size() < bookmarkIndex) {
            throw new KollusException("지정한 북마크가 없습니다.");
        }
        KollusBookmark bookmark = listBookmarks().get(bookmarkIndex);
        seek(bookmark.getTime());
    }

    public void addOrUpdateBookmark(int position, String label) throws KollusException {
        if (this.mediaPlayer == null) {
            throw new KollusException("플레이어가 초기화 되지 않았습니다.");
        }
        if (this.playerState == PlayerStates.NONE) {
            throw new KollusException("영상이 준비 되어 있지 않습니다.");
        }
        if (this.kollusContent == null) {
            throw new KollusException("영상이 정상적으로 로드 되지 않았습니다.");
        }
        if (this.contentType == ContentTypes.LIVE) {
            throw new KollusException("라이브영상은 지원하지 않습니다.");
        }
        mediaPlayer.updateKollusBookmark(position, label);
    }

//    public void updateBookmark(int position, String label) throws KollusException {
//        if (this.mediaPlayer == null) {
//            throw new KollusException("플레이어가 초기화 되지 않았습니다.");
//        }
//        if (this.playerState == PlayerStates.NONE) {
//            throw new KollusException("영상이 준비 되어 있지 않습니다.");
//        }
//        if (this.kollusContent == null) {
//            throw new KollusException("영상이 정상적으로 로드 되지 않았습니다.");
//        }
//        if (this.contentType == ContentTypes.LIVE) {
//            throw new KollusException("라이브영상은 지원하지 않습니다.");
//        }
//        if (listBookmarks() == null) {
//            this.bookmarkList = new ArrayList<KollusBookmark>();
//        }
//
//        Iterator<KollusBookmark> iterBookmark = listBookmarks().iterator();
//        int index = -1;
//        KollusBookmark selectedBookmark = null;
//        while (iterBookmark.hasNext()) {
//            index += 1;
//            KollusBookmark bookmark = iterBookmark.next();
//            if (bookmark.getTime() == position) {
//                selectedBookmark = bookmark;
//                break;
//            }
//        }
//        selectedBookmark.setLabel(label);
//
//        if (index == -1) {
//            listBookmarks().set(index, selectedBookmark);
//        } else {
//            listBookmarks().add()
//        }
//        mediaPlayer.updateKollusBookmark(position, label);
//    }

    public void removeBookmark(int position) throws KollusException {
        if (this.mediaPlayer == null) {
            throw new KollusException("플레이어가 초기화 되지 않았습니다.");
        }
        if (this.playerState == PlayerStates.NONE) {
            throw new KollusException("영상이 준비 되어 있지 않습니다.");
        }
        if (this.kollusContent == null) {
            throw new KollusException("영상이 정상적으로 로드 되지 않았습니다.");
        }
        if (this.contentType == ContentTypes.LIVE) {
            throw new KollusException("라이브영상은 지원하지 않습니다.");
        }
        mediaPlayer.deleteKollusBookmark(position);
    }

    public void changeScreenMode(int kollusPlayerContentMode) {
        switch (kollusPlayerContentMode) {
            case KollusPlayerContentMode.ScaleAspectFit:
                break;
            case KollusPlayerContentMode.ScaleAspectFill:
                break;
            case KollusPlayerContentMode.ScaleAspectFillStretch:
                break;
            case KollusPlayerContentMode.ScaleCenter:
                break;
            case KollusPlayerContentMode.ScaleZoom:
                break;
        }
    }
//endregion
    private Runnable checkProgress = new Runnable(){
        @Override
        public void run() {
            while(playerState != PlayerStates.NONE) {
                if (mediaPlayer != null) {
                    if (playerState != PlayerStates.BUFFERING) {
                        if (mediaPlayer.isPlaying()) {
                            playerState = PlayerStates.PLAY;
                        } else {
                            playerState = PlayerStates.PAUSED;
                        }
                    }
                    int current = mediaPlayer.getCurrentPosition();
                    if (playerWrapperEventListener != null) {
                        playerWrapperEventListener.progress(current, playerState);
                    }
                    if (repeatA >= 0 && repeatB >= 0 && current >= repeatB) {
                        try {
                            seek(repeatA);
                        } catch (KollusException e) {
                            Log.i(TAG, e.getMessage());
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                }
            }

        }
    };
    //region implement setter & setter
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    public KollusStorage getKollusStorage() {
        return kollusStorage;
    }

    public void setKollusStorage(KollusStorage kollusStorage) {
        this.kollusStorage = kollusStorage;
    }

    public KollusContent getKollusContent() {
        return kollusContent;
    }

    public void setKollusContent(KollusContent kollusContent) {
        this.kollusContent = kollusContent;
    }

    public Uri getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(Uri playUrl) {
        this.playUrl = playUrl;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public ContentTypes getContentType() {
        return contentType;
    }

    public void setContentType(ContentTypes contentType) {
        this.contentType = contentType;
    }

    public EncryptTypes getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(EncryptTypes encryptType) {
        this.encryptType = encryptType;
    }

    public PlayerStates getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerStates playerState) {
        this.playerState = playerState;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
        this.audioStreamVolumeObserver.setVolume(volume);
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public int getRepeatA() {
        return repeatA;
    }

    public void setRepeatA(int repeatA) throws KollusException {
        if(this.kollusContent == null || this.playerState == PlayerStates.NONE){
            throw new KollusException("컨텐츠가 준비 되기 전에는 설정 할수 없습니다.");
        }
        if(this.kollusContent.getDuration() < repeatA){
            throw new KollusException("구간 반복 시작점을 컨텐츠 크기보다 크게 설정 할수 없습니다.");
        }
        this.repeatA = repeatA;
    }

    public int getRepeatB() {
        return repeatB;
    }

    public void setRepeatB(int repeatB) throws KollusException {
        if(this.kollusContent == null || this.playerState == PlayerStates.NONE){
            throw new KollusException("컨텐츠가 준비 되기 전에는 설정 할수 없습니다.");
        }
        if(this.kollusContent.getDuration() < this.repeatB){
            throw new KollusException("구간 반복 끝점을 컨텐츠 크기보다 크게 설정 할수 없습니다.");
        }
        if(this.repeatA > repeatB){
            throw new KollusException("구간 반복 시작점보다 끝점을 앞 서 설정 할수 없습니다.");
        }
        this.repeatB = repeatB;
    }

    public float getBrightnessStep() {
        return brightnessStep;
    }

    public void setBrightnessStep(float brightnessStep) {
        this.brightnessStep = brightnessStep;
    }

    public int getSeekStep() {
        return seekStep;
    }

    public void setSeekStep(int seekStep) {
        this.seekStep = seekStep;
    }

    public boolean isAutoplay() {
        return autoplay;
    }

    public void setAutoplay(boolean autoplay) {
        this.autoplay = autoplay;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public PlayerWrapperEventListener getPlayerWrapperEventListener() {
        return playerWrapperEventListener;
    }

    public void setPlayerWrapperEventListener(PlayerWrapperEventListener playerWrapperEventListener) {
        this.playerWrapperEventListener = playerWrapperEventListener;
    }

    public List<BandwidthItem> getBandwidthItemList() {
        return bandwidthItemList;
    }

    public void setBandwidthItemList(List<BandwidthItem> bandwidthItemList) {
        this.bandwidthItemList = bandwidthItemList;
    }

    public List<KollusBookmark> getBookmarkList() {
        return bookmarkList;
    }

    public void setBookmarkList(List<KollusBookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }
    public String getTitle(){
        if(this.kollusContent != null && this.playerState != PlayerStates.NONE){
            return kollusContent.getCourse();
        }
        return "";
    }
    public Uri getThumbnailUrl(){
        if(this.kollusContent != null && this.playerState != PlayerStates.NONE){
            return Uri.parse(kollusContent.getThumbnailPath());
        }
        return null;
    }
    public Uri getPosterUrl(){
        if(this.kollusContent != null && this.playerState != PlayerStates.NONE){
            return Uri.parse(kollusContent.getScreenShotPath());
        }
        return null;
    }
    public int getDuration(){
        if(this.kollusContent != null && this.playerState != PlayerStates.NONE){
            return kollusContent.getDuration();
        }
        return 0;
    }

    //endregion
//region implement event handler
    @Override
    public void onCaptureDetected(String appName, String packageName) {
        Log.i(TAG, "CAPTURE APP DETECTED");
        Log.i(TAG, "APP NAME : " + appName);
        Log.i(TAG, "PACKAGE NAME : " + packageName);
        Log.i(TAG, "Kollus Player released by capture app");
        try {
            release();
        } catch (KollusException kex) {
            Log.i(TAG, kex.getStackTrace()[0].getMethodName());
            Log.i(TAG, kex.getMessage());
        }

    }

    @Override
    public void onRunningEmulator() {
        Log.i(TAG, "Kollus Player released by Emulator");
        try {
            release();
        } catch (KollusException kex) {
            Log.i(TAG, kex.getStackTrace()[0].getMethodName());
            Log.i(TAG, kex.getMessage());
        }
    }

    @Override
    public void onBookmark(List<KollusBookmark> list, boolean isWritable) {
        Log.i(TAG, "load bookmark list: " + list.size());
        this.bookmarkList = list;
        if (!isWritable) {
            Log.i(TAG, "북마크를 수정 할수 없습니다.");
        }
        this.playerWrapperEventListener.loadBookmark();
    }

    @Override
    public void onGetBookmarkError(int errorCode) {
        Log.i(TAG, "raised error on getting bookmark list: " + errorCode);
    }

    @Override
    public void onBookmarkUpdated(int position, boolean isUpdated) {
        if (isUpdated) {
            Log.i(TAG, "update bookmark at " + position);
        } else {
            Log.i(TAG, "fail updating bookmark at " + position);
        }
    }

    @Override
    public void onBookmarkDeleted(int position, boolean isDeleted) {
        if (isDeleted) {
            Log.i(TAG, "delete bookmark at " + position);
        } else {
            Log.i(TAG, "fail deleting bookmark at " + position);
        }
    }

    @Override
    public void onLMS(String request, String response) {

        Log.d(TAG, "LMS REQUEST : " + request);
        Log.d(TAG, "LMS RESPONSE " + response);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        Log.i(TAG, "onBufferingUpdate " + percent);
    }

    @Override
    public void onProxyError(int errorCode, String errorMessage) {
        Log.i(TAG, "raised proxy Error : " + errorCode);
        Log.i(TAG, "raised proxy Error : " + errorMessage);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.i(TAG, "재생 완료");
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        Log.i(TAG, "raised Error : " + what);
        Log.i(TAG, "raised Error : " + extra);
        return false;
    }

    @Override
    public void onExternalDisplayDetect(int type, boolean isPlugged) {

        if (this.playerState != PlayerStates.NONE && kollusContent.getDisableTvOut() && isPlugged) {
            Log.i(TAG, "외부 디스플레이 감지");
            try {
                release();
            } catch (KollusException kex) {
                Log.i(TAG, kex.getStackTrace()[0].getMethodName());
                Log.i(TAG, kex.getMessage());
            }
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        Log.i(TAG, "onInfo : " + what);
        Log.i(TAG, "onInfo : " + extra);
        return false;
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
        Log.i(TAG, "Raised Buffering");
        this.playerState = PlayerStates.BUFFERING;
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
        Log.i(TAG, "End Buffering");
        this.playerState = PlayerStates.PLAY;
    }

    @Override
    public void onFrameDrop(MediaPlayer mediaPlayer) {
        Log.i(TAG, "Raise framedrop");
    }

    @Override
    public void onDownloadRate(MediaPlayer mediaPlayer, int downloadRate) {
        Log.i(TAG, "downloadrate : " + downloadRate);
    }

    @Override
    public void onDetectBandwidthList(MediaPlayer mediaPlayer, List<BandwidthItem> list) {
        this.bandwidthItemList = list;
    }

    @Override
    public void onChangedBandwidth(MediaPlayer mediaPlayer, BandwidthItem bandwidthItem) {
        Log.i(TAG, "change bandwidth : " + bandwidthItem.getBandwidthName());
    }

    @Override
    public void onCodecInitFail(MediaPlayer mediaPlayer, String s) {
        Log.i(TAG, "Raised codec init fail : " + s);

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.i(TAG, "컨텐츠 재생 준비 완료");
        this.playerState = PlayerStates.PREPARED;
        this.kollusContent = new KollusContent();
        mediaPlayer.getKollusContent(this.kollusContent);
        this.progressThread = new Thread(checkProgress);
        this.progressThread.start();
        if (this.autoplay) {
            try {
                play();
            } catch (KollusException kex) {
                Log.i(TAG, kex.getStackTrace()[0].getMethodName());
                Log.i(TAG, kex.getMessage());
            }
        }
        this.playerWrapperEventListener.prepared();
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        Log.i(TAG, "재생 위치 변경 완료 : " + mediaPlayer.getCurrentPosition());
        try {
            if (this.playerState == PlayerStates.PLAY) {
                play();
            }
        } catch (KollusException kex) {
            Log.i(TAG, kex.getStackTrace()[0].getMethodName());
            Log.i(TAG, kex.getMessage());
        }
    }

    @Override
    public void onTimedTextDetect(MediaPlayer mediaPlayer, int trackIndex) {
        Log.i(TAG, "자막 데이터 탐지 완료 : " + trackIndex);
        mediaPlayer.selectTrack(trackIndex);
    }

    @Override
    public void onTimedText(MediaPlayer mediaPlayer, String text) {
        Log.i(TAG, "자막 데이터 발생 : " + text);
    }

    @Override
    public void onTimedImage(MediaPlayer mediaPlayer, byte[] image, int width, int height) {
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (this.playerState == PlayerStates.NONE) {
                prepare();
            }
        }catch (KollusException kex){
            Log.i(TAG, kex.getStackTrace()[0].getMethodName());
            Log.i(TAG, kex.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        try {
//            if (this.playerState != PlayerStates.NONE) {
//                release();
//            }
//        }catch (KollusException kex){
//            Log.i(TAG, kex.getStackTrace()[0].getMethodName());
//            Log.i(TAG, kex.getMessage());
//        }
    }

    @Override
    public void onAudioStreamVolumeChanged(int audioStreamType, int volume) {
        this.volume = volume;
    }

    @Override
    public void onScreenBrightnessChangedListener(int mode, float value) {
        this.brightness = value;
    }
    //endregion
}
