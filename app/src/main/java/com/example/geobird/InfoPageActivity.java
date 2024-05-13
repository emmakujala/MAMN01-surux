package com.example.geobird;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
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
    private CustomVibrator<Float> vibe;
    private Booster sound;

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
        vibe = new CustomVibrator<>(getSystemService(Vibrator.class));
        try {
            //sound = new SoundMeter();
        } catch (Exception e) {
            Log.d("ERROR", "Unable to create sound meter: " + e.getMessage());
        }
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
        if (now - lastMove < 500) {
            return;
        }
        float angularSpeedX = event.values[0];
        float angularSpeedY = event.values[1];

        if (
            angularSpeedX > ROTATION_THRESHOLD ||
            angularSpeedX < -ROTATION_THRESHOLD ||
            angularSpeedY > ROTATION_THRESHOLD ||
            angularSpeedY < -ROTATION_THRESHOLD
        ) {
            String dir = DirectionMapper.direction(angularSpeedX, angularSpeedY);
            switch (dir) {
                case "right":
                    rotate(90);
                    break;
                case "upperRight":
                    rotate(45);
                    break;
                case "up":
                    rotate(0);
                    break;
                case "upperLeft":
                    rotate(-45);
                    break;
                case "left":
                    rotate(-90);
                    break;
                case "downLeft":
                    rotate(-135);
                    break;
                case "down":
                    rotate(180);
                    break;
                case "downRight":
                    rotate(135);
                    break;
                default:
                    Log.d("ERROR", "Unknown direction: " + dir);
            }
        }
    }

    private void rotate(float angle) {
        ImageView arrow = findViewById(R.id.bird);
        arrow.setRotation(angle);
        lastMove = System.currentTimeMillis();
        vibe.vibrateMedium(angle);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void start(View v) {
        vibe.vibrateLow();
        if (sound != null) {
            sound.stop();
        }
        Intent intent = new Intent(InfoPageActivity.this, MapsActivity.class);
        InfoPageActivity.this.startActivity(intent);
    }

    public void backFunc(View v) {
        finish();
    }
}