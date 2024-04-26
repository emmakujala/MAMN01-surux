package com.example.geobird;


import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.geobird.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {
    private static final String DEBUG_TAG = "Swipe";
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
    private GamePlay game;
    private String currentGoal;
    private GestureDetectorCompat mDetector;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.game = new GamePlay();
        this.currentGoal = game.randomCity();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        vibe = new CustomVibrator<>(getSystemService(Vibrator.class));
        //Google Maps overridar typ alla events så måste ha en separat view ovanpå för att registrera touchevents
        View mapOverlay = findViewById(R.id.mapOverlay);
        mapOverlay.setOnTouchListener((v, event) -> mDetector.onTouchEvent(event));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float angularSpeedX = event.values[0];
        float angularSpeedY = event.values[1];
        double speed = 0.02;
        if (isReady() && canMove) {
            keepFlying(angularSpeedX, angularSpeedY, speed);


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

        }

    }

    private void keepFlying(float angularSpeedX, float angularSpeedY, double speed) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        bird = new Bird(59,18,mMap, getResources());
        LatLng swedenCenter = new LatLng(62.0, 15.0);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bird.getBirdPos()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(swedenCenter, 5)); // Adjust the zoom level as needed
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        LatLngBounds swedenBounds = new LatLngBounds(
                new LatLng(55.34, 10.79), // Southwest bound of Sweden
                new LatLng(69.06, 24.19)  // Northeast bound of Sweden
        );
        TextView goal = findViewById(R.id.flyTo);
        TextView points = findViewById(R.id.points);
        TextView timer = findViewById(R.id.timer);
        points.setText("0");
        goal.setText(currentGoal);
        mMap.setLatLngBoundsForCameraTarget(swedenBounds);
        mDetector = new GestureDetectorCompat(this, new SwipeDetector(this, new GameController(this,scheduler,game,bird,goal,points, timer)));

    }

    private boolean isReady() {
        return mMap != null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


}

