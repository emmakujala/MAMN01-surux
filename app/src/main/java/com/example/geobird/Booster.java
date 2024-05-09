package com.example.geobird;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;

public class Booster {

    private MediaRecorder recorder = new MediaRecorder();
    private final File soundFile = File.createTempFile("geobirdsound", "mp3");
    private boolean charging = false;
    private long startTime;
    private long accumulated = 0;
    private double speedBoost = 0;
    private final double baseSpeed = 0.02;

    public Booster() throws Exception {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
            Log.d("ERROR", "SDK VERSION IS TO LOW RIP");
            throw new Exception("SDK VERSION IS TO LOW TO USE MEDIA RECORDER");
        }
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(soundFile);
        recorder.prepare();

        // todo might want to start it with a function call instead of in constructor
        recorder.start();   // Recording is now started
    }

    public void charge() {
        charging = true;
        startTime = System.currentTimeMillis();
    }

    /**
     * Takes a function to run if the user got a speedboost from the blowing like haptic feedback
     * */
    public void release(Runnable func) {
        // to avoid any problems when we get a double up event
        if (charging == false) {
            return;
        }
        charging = false;
        double delta = (System.currentTimeMillis() - startTime);
        double res = (double) (accumulated / (delta * 500));
        accumulated = 0;
        Log.d("BOOST", "" + res);
        speedBoost = res;
        if (res > 0) {
            func.run();
        }
    }

    /**
     * Call this to charge up the booster when charge() has been called prior
     * Or to get the current speed after release(), Takes a func to call when charging
     * to do something (like haptic feedback or something)
     * */
    public double getSpeed(Runnable func) {
        if (charging) {
            int level = getAmp();
            Log.d("BOOST", "Level: " + level);
            if (level > 10000) {
                accumulated += level;
                func.run();
            }
            return baseSpeed;
        }

        Log.d("BOOST", "Speed: " + speedBoost);
        if (speedBoost < 0.0) {
            return baseSpeed;
        }
        // todo maybe better falloff
        speedBoost -= 0.008;
        return baseSpeed + speedBoost;
    }

    public void stop() {
        try {
            if (recorder == null) {
                Log.d("ERROR", "Attempted to stop SoundMeter when it already has been stopped");
            } else {
                recorder.stop();
                recorder.reset();   // You can reuse the object by going back to setAudioSource() step
                recorder.release(); // Now the object cannot be reused
                recorder = null;
            }
            if (soundFile.delete() == false) {
                Log.d("ERROR", "Unable to delete sound file path should be: " + soundFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.d("ERROR", "Exception when stopping media recorder: " + e.getMessage());
        }
    }

    private int getAmp() {
        if (recorder == null) {
            Log.d("ERROR", "Attempted to get amplitude after SoundMeter has been stopped");
        }
        return recorder.getMaxAmplitude();
    }
}
