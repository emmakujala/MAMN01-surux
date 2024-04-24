package com.example.geobird;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;

public class SoundMeter {

    private MediaRecorder recorder = new MediaRecorder();
    private final File soundFile = File.createTempFile("geobirdsound", "mp3");

    public SoundMeter() throws Exception {
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

    public void stop() {
        if (recorder == null) {
            Log.d("ERROR", "Attempted to stop SoundMeter when it already has been stopped");
        }
        recorder.stop();
        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
        recorder.release(); // Now the object cannot be reused
        recorder = null;
        try {
            if (soundFile.delete() == false) {
                Log.d("ERROR", "Unable to delete sound file path should be: " + soundFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.d("ERROR", "Exception when stopping media recorder: " + e.getMessage());
        }
    }

    public float getAmp() {
        if (recorder == null) {
            Log.d("ERROR", "Attempted to get amplitude after SoundMeter has been stopped");
        }
        return recorder.getMaxAmplitude();
    }
}
