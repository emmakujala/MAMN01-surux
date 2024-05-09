package com.example.geobird;

import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class CustomVibrator<T> {
    private final Vibrator vibe;
    private T last = null;

    public CustomVibrator(Vibrator vibe) {
        this.vibe = vibe;
    }

    /**
     * Will vibrate
     * */
    public void vibrateMedium() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibe.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
        }
    }

    /**
     * Will only vibrate if the current input is different from the previous one
     * */
    public void vibrateMedium(T input) {
        if (input.equals(last)) {
            return;
        }
        last = input;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibe.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
        }
    }

    public void vibrateLow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // could maybe change to EFFECT_CLICK
            vibe.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK));
        }
    }

    public void vibrateDoubleClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // could maybe change to EFFECT_CLICK
            vibe.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK));
        }
    }

    public void vibrateHeavyClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // could maybe change to EFFECT_CLICK
            vibe.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK));
        }
    }
}
