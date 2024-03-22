package com.example.geobird;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private static final float ROTATION_THRESHOLD = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
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

    public void click(View v) {
        CanvasView canvasView = findViewById(R.id.canvasView);
        canvasView.updateDeltas(-100, 0);
        canvasView.invalidate();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float angularSpeedX = event.values[0];
        float angularSpeedY = event.values[1];
        CanvasView canvasView = findViewById(R.id.canvasView);

        // rotation around the X axis (upwards/downwards)
        if (angularSpeedX > ROTATION_THRESHOLD) {
            // phone is rotated downwards
            canvasView.updateDeltas(-100, 0);
        } else if (angularSpeedX < -ROTATION_THRESHOLD) {
            // phone is rotated upwards
            canvasView.updateDeltas(100, 0);
        }

        // rotation around the Y axis (right/left)
        if (angularSpeedY > ROTATION_THRESHOLD) {
            // phone is rotated left
            canvasView.updateDeltas(0, -100);
        } else if (angularSpeedY < -ROTATION_THRESHOLD) {
            // phone is rotated right
            canvasView.updateDeltas(0, 100);
        }
        canvasView.invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}