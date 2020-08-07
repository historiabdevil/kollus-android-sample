package d.factory.haeming.ui.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kollus.sdk.media.content.BandwidthItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;


import d.factory.haeming.R;
import d.factory.haeming.ui.bandwidth.BandwidthListViewAdapter;

import static android.R.drawable.ic_lock_silent_mode;
import static android.R.drawable.ic_lock_silent_mode_off;
import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;


/**
 * TODO: document your custom view class.
 */
public class LiveControlView extends ConstraintLayout
        implements View.OnClickListener, View.OnLongClickListener, AdapterView.OnItemClickListener {

    private final static String LOG_TAG="LIVECONTROL";

    private ConstraintLayout topbarLayout;
    private ConstraintLayout leftLayout;
    private ConstraintLayout rightLayout;


    //Center
    private ImageButton playAndPauseButton;
    private ProgressBar bufferingSpinner;
    private TextView actionView;
    private ImageButton screenUnlockButton;

    //Topbar
    private ImageButton backButton;
    private TextView titleView;
    private Button resolutionButton;
    private ImageButton fullscreenButton;

    //left
    private ImageButton brightnessUpButton;
    private ImageButton screenLockButton;
    private ImageButton brightnessDownButton;

    //right
    private ImageButton volumeUpButton;
    private ImageButton muteButton;
    private ImageButton volumeDownButton;

    //resolution popup
    private ConstraintLayout resolutionPopup;
    private ListView resolutionList;

    private boolean screenLocked = false;
    private boolean muted = false;
    private boolean fullscreen = false;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.controlView:
                if(resolutionPopup.getVisibility() == VISIBLE)
                    resolutionPopup.setVisibility(GONE);
                break;
            case R.id.playAndPauseButton:
                this.onControlViewEventListener.OnPlayAndPauseButtonClicked(this, this.status);
                break;
            case R.id.backButton:
                this.onControlViewEventListener.OnBackButtonClicked(this);
                break;
            case R.id.resolutionButton:
                this.resolutionPopup.setVisibility(VISIBLE);
                this.resolutionPopup.bringToFront();
                this.onControlViewEventListener.OnResolutionButtonClicked(this);
                break;
            case R.id.fullscreenButton:
                this.fullscreen  = !this.fullscreen;
                this.onControlViewEventListener.OnFullscreenButtonClicked(this, fullscreen);
                break;
            case R.id.screenLockButton:
                this.screenLocked = true;
                this.resolutionButton.setEnabled(false);
                this.leftLayout.setVisibility(GONE);
                this.rightLayout.setVisibility(GONE);
                this.playAndPauseButton.setVisibility(GONE);
                this.bufferingSpinner.setVisibility(GONE);
                this.screenUnlockButton.setVisibility(VISIBLE);
                this.onControlViewEventListener.OnScreenLockButtonClicked(this, this.screenLocked);
                break;
            case R.id.screenUnlockButton:
                setStatus(this.status);
                this.screenUnlockButton.setVisibility(GONE);
                this.resolutionButton.setEnabled(true);
                this.onControlViewEventListener.OnScreenLockButtonClicked(this, this.screenLocked);
                break;
            case R.id.muteButton:
                this.muted = !this.muted;
                if(this.muted) {
                    this.muteButton.setImageResource(ic_lock_silent_mode);
                }
                else {
                    this.muteButton.setImageResource(ic_lock_silent_mode_off);
                }
                this.onControlViewEventListener.OnMuteButtonClicked(this, this.muted);
                break;
            case R.id.volumUpButton:
            case R.id.volumDownButton:
            case R.id.brightnessUpButton:
            case R.id.brightnessDownButton:
                longclickHandler.removeMessages(0);
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BandwidthItem item = (BandwidthItem) parent.getItemAtPosition(position);
        this.onControlViewEventListener.OnResolutionChanged(item);
        this.resolutionButton.setText(item.getBandwidthName());
        this.resolutionPopup.setVisibility(GONE);
    }
    Handler longclickHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (longclickedView){
                case R.id.brightnessUpButton:
                    onControlViewEventListener.OnBrightnessUpTouched(LiveControlView.this);
                    break;
                case R.id.brightnessDownButton:
                    onControlViewEventListener.OnBrightnessDownTouched(LiveControlView.this);
                    break;
                case R.id.volumUpButton:
                    onControlViewEventListener.OnVolumeUpTouched(LiveControlView.this);
                    break;
                case R.id.volumDownButton:
                    onControlViewEventListener.OnVolumeDownTouched(LiveControlView.this);
                    break;
            }
            longclickHandler.sendEmptyMessageDelayed(0, 200);
        }
    };
    private int longclickedView = 0;
    @Override
    public boolean onLongClick(View v) {
        this.longclickedView = v.getId();
        longclickHandler.sendEmptyMessageDelayed(0, 200);
        return false;
    }

    public enum PlayerStatus {
        NOT_PREPARED, PREPARED, BUFFERING, PLAY, PAUSE
    }

    public interface OnControlViewEventListener {
        void OnPlayAndPauseButtonClicked(LiveControlView liveControlView, PlayerStatus status);
        void OnBackButtonClicked(LiveControlView liveControlView);
        void OnResolutionButtonClicked(LiveControlView liveControlView);
        void OnResolutionChanged(BandwidthItem item);
        void OnScreenLockButtonClicked(LiveControlView liveControlView, boolean screenLocked);
        void OnMuteButtonClicked(LiveControlView liveControlView, boolean muted);
        void OnBrightnessUpTouched(LiveControlView liveControlView);
        void OnBrightnessDownTouched(LiveControlView liveControlView);
        void OnVolumeUpTouched(LiveControlView liveControlView);
        void OnVolumeDownTouched(LiveControlView liveControlView);
        void OnFullscreenButtonClicked(LiveControlView liveControlView, boolean fullscreen);
    }

    private OnControlViewEventListener onControlViewEventListener;
    private PlayerStatus status = PlayerStatus.NOT_PREPARED;

    public LiveControlView(Context context) {
        super(context);
        init(null, 0);
    }

    public LiveControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LiveControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.LiveControlView, defStyle, 0);
        a.recycle();
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.live_control_view,this, false);
        addView(v);
        //layout
        topbarLayout = this.findViewById(R.id.topbarLayout);
        leftLayout = this.findViewById(R.id.leftLayout);
        rightLayout = this.findViewById(R.id.rightLayout);

        //Center
        playAndPauseButton = this.findViewById(R.id.playAndPauseButton);
        bufferingSpinner = this.findViewById(R.id.bufferingSpinner);
        actionView = this.findViewById(R.id.actionView);
        screenUnlockButton = this.findViewById(R.id.screenUnlockButton);

        //Topbar
        backButton = this.findViewById(R.id.backButton);
        titleView = this.findViewById(R.id.titleView);
        resolutionButton = this.findViewById(R.id.resolutionButton);
        fullscreenButton = this.findViewById(R.id.fullscreenButton);


        //left
        brightnessUpButton = this.findViewById(R.id.brightnessUpButton);
        screenLockButton = this.findViewById(R.id.screenLockButton);
        brightnessDownButton = this.findViewById(R.id.brightnessDownButton);

        //right
        volumeUpButton = this.findViewById(R.id.volumUpButton);
        muteButton = this.findViewById(R.id.muteButton);
        volumeDownButton = this.findViewById(R.id.volumDownButton);

        //resolution popup
        resolutionPopup = this.findViewById(R.id.resolutionPopup);
        resolutionList = this.findViewById(R.id.resolutionList);

        this.setListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        playAndPauseButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        resolutionButton.setOnClickListener(this);
        screenLockButton.setOnClickListener(this);
        screenUnlockButton.setOnClickListener(this);
        muteButton.setOnClickListener(this);

        brightnessUpButton.setOnClickListener(this);
        brightnessDownButton.setOnClickListener(this);
        volumeUpButton.setOnClickListener(this);
        volumeDownButton.setOnClickListener(this);

        brightnessUpButton.setOnLongClickListener(this);
        brightnessDownButton.setOnLongClickListener(this);
        volumeUpButton.setOnLongClickListener(this);
        volumeDownButton.setOnLongClickListener(this);

        resolutionList.setOnItemClickListener(this);
    }

    public void setListener(OnControlViewEventListener listener) {
        this.onControlViewEventListener = listener;
    }

    public void setResolutionList(List<BandwidthItem> list){
        BandwidthListViewAdapter adapter =  new BandwidthListViewAdapter();
        adapter.addAll(list);
        resolutionList.setAdapter(adapter);
    }
    public void changedBandwidth(BandwidthItem item){
        this.resolutionButton.setText(item.getBandwidthName());
    }
    public void setTitle(String title){
        this.titleView.setText(title);
    }
    public void setVolume(int volume) {
        this.actionView.setCompoundDrawablesWithIntrinsicBounds(ic_lock_silent_mode_off, 0x0, 0x0, 0x0);
        this.actionView.setText(String.format("%d%%", volume));

    }
    public void setBrightness(float brightness) {
        this.actionView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_brightness, 0x0, 0x0, 0x0);
        this.actionView.setText(String.format("%f%%", brightness));
    }
    public PlayerStatus getStatus(){
        return this.status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
        switch (this.status) {
            case NOT_PREPARED:
                this.topbarLayout.setVisibility(GONE);
                this.leftLayout.setVisibility(GONE);
                this.rightLayout.setVisibility(GONE);
                this.playAndPauseButton.setVisibility(GONE);
                this.bufferingSpinner.setVisibility(VISIBLE);
                this.actionView.setText("준비중");
                break;
            case PREPARED:
                this.topbarLayout.setVisibility(VISIBLE);
                this.leftLayout.setVisibility(VISIBLE);
                this.rightLayout.setVisibility(VISIBLE);
                this.playAndPauseButton.setVisibility(VISIBLE);
                this.bufferingSpinner.setVisibility(GONE);
                this.actionView.setText("");
                break;
            case BUFFERING:
                this.topbarLayout.setVisibility(VISIBLE);
                this.leftLayout.setVisibility(VISIBLE);
                this.rightLayout.setVisibility(VISIBLE);
                this.playAndPauseButton.setVisibility(GONE);
                this.bufferingSpinner.setVisibility(VISIBLE);
                this.actionView.setText("버퍼링중");
                break;
            case PLAY:
                this.topbarLayout.setVisibility(VISIBLE);
                this.leftLayout.setVisibility(VISIBLE);
                this.rightLayout.setVisibility(VISIBLE);
                this.playAndPauseButton.setVisibility(VISIBLE);
                this.playAndPauseButton.setImageResource(ic_media_pause);
                this.bufferingSpinner.setVisibility(GONE);
                this.actionView.setText("");
                break;
            case PAUSE:
                this.topbarLayout.setVisibility(VISIBLE);
                this.leftLayout.setVisibility(VISIBLE);
                this.rightLayout.setVisibility(VISIBLE);
                this.playAndPauseButton.setVisibility(VISIBLE);
                this.playAndPauseButton.setImageResource(ic_media_play);
                this.bufferingSpinner.setVisibility(GONE);
                this.actionView.setText("");
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
