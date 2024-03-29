package com.example.geobird;


import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.telecom.Call;
import android.util.Log;
import android.view.View;

import kotlin.reflect.KCallable;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private static final float ROTATION_THRESHOLD = 2.0f;
    private final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        TaskScheduler.scheduleTask(() -> {});
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
        float speed = 0.4f;
        // rotation around the X axis (upwards/downwards)
        if (angularSpeedX > ROTATION_THRESHOLD) {
            // phone is rotated downwards
            TaskScheduler.updateTask(() -> canvasView.updateDeltas(-speed, 0));
        } else if (angularSpeedX < -ROTATION_THRESHOLD) {
            // phone is rotated upwards
            TaskScheduler.updateTask(() -> canvasView.updateDeltas(speed, 0));
        }

        // rotation around the Y axis (right/left)
        if (angularSpeedY > ROTATION_THRESHOLD) {
            // phone is rotated left
            TaskScheduler.updateTask(() -> canvasView.updateDeltas(0, -speed));
        } else if (angularSpeedY < -ROTATION_THRESHOLD) {
            // phone is rotated right
            TaskScheduler.updateTask(() -> canvasView.updateDeltas(0, speed));
        }
        canvasView.invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}



class TaskScheduler {
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final AtomicReference<Runnable> taskRef = new AtomicReference<>();

    public static void scheduleTask(Runnable task) {
        taskRef.set(task);
        executor.scheduleAtFixedRate(TaskScheduler::executeTask, 0, 15, TimeUnit.MILLISECONDS);
    }

    public static void updateTask(Runnable newTask) {
        taskRef.set(newTask);
    }

    private static void executeTask() {
        Runnable task = taskRef.get();
        if (task != null) {
            task.run();
        }
    }
}