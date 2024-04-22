package com.example.geobird;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InfoPageActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private static final float ROTATION_THRESHOLD = 1.5f;
    private long lastMove = System.currentTimeMillis();
    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        vibe = getSystemService(Vibrator.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long now = System.currentTimeMillis();
        if (now - lastMove < 1000) {
            return;
        }
        float angularSpeedX = event.values[0];
        float angularSpeedY = event.values[1];
        if (angularSpeedX < -ROTATION_THRESHOLD && angularSpeedY > ROTATION_THRESHOLD) {
            // up to the left
            rotate(45);
        } else if (angularSpeedX < -ROTATION_THRESHOLD && angularSpeedY < -ROTATION_THRESHOLD) {
            // up to the right
            rotate(-45);
        } else if (angularSpeedX > ROTATION_THRESHOLD && angularSpeedY > ROTATION_THRESHOLD) {
            // down to the left
            rotate(135);
        } else if (angularSpeedX > ROTATION_THRESHOLD && angularSpeedY < -ROTATION_THRESHOLD) {
            // down to the right
            rotate(-135);
        } else if (angularSpeedX > ROTATION_THRESHOLD) { // rotation around the X axis (upwards/downwards)
            // phone is rotated downwards
            rotate(180);
        } else if (angularSpeedX < -ROTATION_THRESHOLD) {
            // phone is rotated upwards
            rotate(0);
        } else if (angularSpeedY > ROTATION_THRESHOLD) { // rotation around the Y axis (right/left)
            // phone is rotated left
            rotate(90);
        } else if (angularSpeedY < -ROTATION_THRESHOLD) {
            // phone is rotated right
            rotate(-90);
        }
    }

    private void rotate(float angle) {
        ImageView arrow = findViewById(R.id.arrow);
        arrow.setRotation(angle);
        lastMove = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibe.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void start(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // could maybe change to EFFECT_CLICK
            vibe.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK));
        }
        // go to game activity
    }
}