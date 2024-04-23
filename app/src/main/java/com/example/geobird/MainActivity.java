package com.example.geobird;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private static final float ROTATION_THRESHOLD = 1.5f;
    private GestureDetectorCompat mDetector;
    private boolean isFlying = true;
    private long lastMove = System.currentTimeMillis();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        TaskScheduler.scheduleTask(() -> {});
        mDetector = new GestureDetectorCompat(this, new SwipeDetector(this));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
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
        canvasView.updateDeltas(0, 0);
        TaskScheduler.updateTask(() -> {});
        canvasView.invalidate();
        Intent intent = new Intent(MainActivity.this, InfoPageActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void land() {
        isFlying = false;
        CanvasView canvasView = findViewById(R.id.canvasView);
        TaskScheduler.pause();
        canvasView.landed();
        canvasView.invalidate();
    }

    public void takeOff() {
        isFlying = true;
        CanvasView canvasView = findViewById(R.id.canvasView);
        canvasView.takeOff();
        canvasView.invalidate();
        TaskScheduler.resume();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long timestamp = System.currentTimeMillis();
        if (isFlying == false) {
            return;
        }
        float angularSpeedX = event.values[0];
        float angularSpeedY = event.values[1];
        float angularSpeedZ = event.values[2];
        Log.d("Gyro: ", angularSpeedX + " | " + angularSpeedY + " | " + angularSpeedZ);
        CanvasView canvasView = findViewById(R.id.canvasView);
        float speed = 0.4f;
        if (angularSpeedX < -ROTATION_THRESHOLD && angularSpeedY > ROTATION_THRESHOLD) {
            // up to the left
            canvasView.rotate(45);
            TaskScheduler.updateTask(() -> canvasView.updateDeltas(speed, -speed));
        } else if (angularSpeedX < -ROTATION_THRESHOLD && angularSpeedY < -ROTATION_THRESHOLD) {
            // up to the right
            canvasView.rotate(-45);
            TaskScheduler.updateTask(() -> canvasView.updateDeltas(speed, speed));
        } else if (angularSpeedX > ROTATION_THRESHOLD && angularSpeedY > ROTATION_THRESHOLD) {
            // down to the left
            canvasView.rotate(135);
            TaskScheduler.updateTask(() -> canvasView.updateDeltas(-speed, -speed));
        } else if (angularSpeedX > ROTATION_THRESHOLD && angularSpeedY < -ROTATION_THRESHOLD) {
            // down to the right
            canvasView.rotate(-135);
            TaskScheduler.updateTask(() -> canvasView.updateDeltas(-speed, speed));
        } else if (angularSpeedX > ROTATION_THRESHOLD) { // rotation around the X axis (upwards/downwards)
            // phone is rotated downwards
            canvasView.rotate(180);
            TaskScheduler.updateTask(() -> canvasView.updateDeltas(-speed, 0));
        } else if (angularSpeedX < -ROTATION_THRESHOLD) {
            // phone is rotated upwards
            canvasView.rotate(0);
            TaskScheduler.updateTask(() -> canvasView.updateDeltas(speed, 0));
        } else if (angularSpeedY > ROTATION_THRESHOLD) { // rotation around the Y axis (right/left)
            // phone is rotated left
            canvasView.rotate(90);
            TaskScheduler.updateTask(() -> canvasView.updateDeltas(0, -speed));
        } else if (angularSpeedY < -ROTATION_THRESHOLD) {
            // phone is rotated right
            canvasView.rotate(-90);
            TaskScheduler.updateTask(() -> canvasView.updateDeltas(0, speed));
        }
        canvasView.invalidate();
        lastMove = System.currentTimeMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}