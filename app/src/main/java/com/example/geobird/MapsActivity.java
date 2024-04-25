package com.example.geobird;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.geobird.databinding.ActivityMapsBinding;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private SensorManager sensorManager;
    private Sensor sensor;
    private static final float ROTATION_THRESHOLD = 1.5f;
    private final Scheduler scheduler = new Scheduler(70);
    Bird bird;
    private CustomVibrator<String> vibe;
    private boolean canMove = true;
    private final Handler handler = new Handler();
    private final Executor exe = Executors.newSingleThreadScheduledExecutor();
    private boolean isTimerRunning = false;
    private DirectionMapper maper = new DirectionMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        vibe = new CustomVibrator<>(getSystemService(Vibrator.class));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float angularSpeedX = event.values[0];
        float angularSpeedY = event.values[1];
        double speed = 0.01;
        if (isReady() && canMove) {
            /**
            canMove = false;
            // startTimer();
            exe.execute(() -> {
                try {
                    Thread.sleep(300);
                    canMove = true;
                } catch (InterruptedException e) {
                    Log.d("ERROR", "Sleep problem: " + e.getMessage());
                }
            });*/
            if (
                angularSpeedX > ROTATION_THRESHOLD ||
                angularSpeedX < -ROTATION_THRESHOLD ||
                angularSpeedY > ROTATION_THRESHOLD ||
                angularSpeedY < -ROTATION_THRESHOLD
            ) {
                String dir = maper.direction(angularSpeedX, angularSpeedY);
                Log.d("Dir", dir);
                vibe.vibrateMedium(dir);
                scheduler.updateTask(() -> bird.updateBird(dir, speed));
            }
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

    private void startTimer() {
        if (!isTimerRunning) {
            canMove = false;
            isTimerRunning = true;
            handler.postDelayed(() -> {
                isTimerRunning = false;
                canMove = true;
            }, 500);
        }
    }

    private void stopTimer() {
        handler.removeCallbacksAndMessages(null);
        isTimerRunning = false;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        bird = new Bird(59,18,mMap, getResources());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bird.getBirdPos()));
    }

    private boolean isReady() {
        return mMap != null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}