package com.se.kollus.component;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.Settings;

import org.jetbrains.annotations.NotNull;

public class AudioStreamVolumeObserver {
    public interface OnAudioStreamVolumeChangedListener {
        void onAudioStreamVolumeChanged(int audioStreamType, int volume);
    }

    private static class AudioStreamVolumeContentObserver extends ContentObserver {

        private final AudioManager audioManager;
        private final int audioStreamType;
        private final OnAudioStreamVolumeChangedListener listener;
        private int lastVolume;

        public AudioStreamVolumeContentObserver(@NotNull Handler handler,
                                                @NotNull AudioManager audioManager,
                                                int audioStreamType,
                                                @NotNull OnAudioStreamVolumeChangedListener listener) {
            super(handler);
            this.audioManager = audioManager;
            this.audioStreamType = audioStreamType;
            this.listener = listener;
            this.lastVolume = audioManager.getStreamVolume(audioStreamType);
        }

        @Override
        public void onChange(boolean selfChange) {
            int currentVolume = this.audioManager.getStreamVolume(this.audioStreamType);
            if (currentVolume != lastVolume) {
                if (currentVolume != 0) {
                    this.lastVolume = currentVolume;
                }
                this.listener.onAudioStreamVolumeChanged(this.audioStreamType, currentVolume);
            }
        }

    }

    private final Context context;
    private AudioStreamVolumeContentObserver audioStreamVolumeContentObserver;

    public AudioStreamVolumeObserver(@NotNull Context context) {
        this.context = context;
    }

    public int getVolume() {
        return this.audioStreamVolumeContentObserver.audioManager.getStreamVolume(audioStreamVolumeContentObserver.audioStreamType);
    }

    public void setVolume(int volume) {
        this.audioStreamVolumeContentObserver.audioManager.setStreamVolume(
                this.audioStreamVolumeContentObserver.audioStreamType, volume, 0
        );
    }

    public void setMute(boolean mute) {
        if (mute) {
            setVolume(0);
        } else {
            setVolume(this.audioStreamVolumeContentObserver.lastVolume);
        }
    }

    public void start(
            int audioStreamType,
            @NotNull OnAudioStreamVolumeChangedListener listener) {
        stop();
        Handler handler = new Handler();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.audioStreamVolumeContentObserver = new AudioStreamVolumeContentObserver(handler, audioManager, audioStreamType, listener);
        context.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, audioStreamVolumeContentObserver);
    }

    public void stop() {
        if (this.audioStreamVolumeContentObserver == null) return;
        this.context.getContentResolver().unregisterContentObserver(this.audioStreamVolumeContentObserver);
        this.audioStreamVolumeContentObserver = null;
    }

}