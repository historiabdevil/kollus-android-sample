package com.se.kollus.component;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

public class ScreenBrightnessObserver {
    private static final String LOG_TAG = "SETTING";

    public float getBrightness() {
        try {
            return Settings.System.getFloat(this.context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            return -1.0f;
        }
    }

    public interface OnScreenBrightnessChangedListener {
        void onScreenBrightnessChangedListener(int mode, float value);
    }

    private static class ScreenBrightnessContentObserver extends ContentObserver {

        private final OnScreenBrightnessChangedListener listener;
        private final ContentResolver contentResolver;
        private float lastBrightness = -1;
        private int screenBrightnessMode = -1;

        public ScreenBrightnessContentObserver(Handler handler,
                                               @NotNull ContentResolver contentResolver,
                                               @NotNull OnScreenBrightnessChangedListener listener) throws Exception {
            super(handler);
            this.listener = listener;
            this.contentResolver = contentResolver;
            try {
                this.screenBrightnessMode = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
            } catch (Settings.SettingNotFoundException e) {
                throw new Exception("밝기 모드 정보를 가져오지 못했습니다.");
            }
            try {
                this.lastBrightness = Settings.System.getFloat(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                throw new Exception("화면 밝기깂 정보를 가져오지 못했습니다.");
            }
        }

        @Override
        public void onChange(boolean selfChange) {
            boolean eventFired = false;
            int currentScreenBrightnessMode = -1;
            try {
                currentScreenBrightnessMode = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
            } catch (Settings.SettingNotFoundException e) {
                Log.d(LOG_TAG, "밝기 모드 정보를 가져오지 못했습니다.");
            }
            float currentBrightness = -1;
            try {
                currentBrightness = Settings.System.getFloat(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                Log.d(LOG_TAG, "화면 밝기깂 정보를 가져오지 못했습니다.");
            }
            if (currentScreenBrightnessMode != this.screenBrightnessMode && currentBrightness != -1) {
                this.screenBrightnessMode = currentScreenBrightnessMode;
                this.lastBrightness = currentBrightness;
                eventFired = true;
                this.listener.onScreenBrightnessChangedListener(currentScreenBrightnessMode, currentBrightness);
            }
            if (!eventFired) {
                if (currentBrightness != this.lastBrightness) {
                    this.lastBrightness = currentBrightness;
                    this.listener.onScreenBrightnessChangedListener(currentScreenBrightnessMode, currentBrightness);
                }
            }
        }
    }

    private final Context context;
    private ScreenBrightnessContentObserver screenBrightnessContentObserver;

    public ScreenBrightnessObserver(@NotNull Context context) {
        this.context = context;
    }

    public void setBrightness(float value) {
        Settings.System.putFloat(this.context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
    }

    public void start(@NotNull OnScreenBrightnessChangedListener listener) throws Exception {
        stop();
        Handler handler = new Handler();
        this.screenBrightnessContentObserver = new ScreenBrightnessContentObserver(handler, this.context.getContentResolver(), listener);
        this.context.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, screenBrightnessContentObserver);
    }

    public void stop() {
        if (this.screenBrightnessContentObserver == null) return;
        this.context.getContentResolver().unregisterContentObserver(this.screenBrightnessContentObserver);
        this.screenBrightnessContentObserver = null;
    }
}
