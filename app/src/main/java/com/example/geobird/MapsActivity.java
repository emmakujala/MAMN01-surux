package com.example.geobird;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.geobird.databinding.ActivityMapsBinding;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private SensorManager sensorManager;
    private Sensor sensor;
    private static final float ROTATION_THRESHOLD = 1.5f;
    private final Handler handler = new Handler();

    Bird bird;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        TaskScheduler.scheduleTask(() -> {});
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float angularSpeedX = event.values[0];
        float angularSpeedY = event.values[1];
        double speed = 0.01;

        if (isReady()) {

            if (angularSpeedX < -ROTATION_THRESHOLD && angularSpeedY > ROTATION_THRESHOLD) {
             // up to the left

             TaskScheduler.updateTask(() ->  bird.updateBird("upperLeft", speed));
             } else if (angularSpeedX < -ROTATION_THRESHOLD && angularSpeedY < -ROTATION_THRESHOLD) {
             // up to the right
             TaskScheduler.updateTask(() -> bird.updateBird("upperRight",speed));
             } else if (angularSpeedX > ROTATION_THRESHOLD && angularSpeedY > ROTATION_THRESHOLD) {
             // down to the left
             TaskScheduler.updateTask(() ->   bird.updateBird("downLeft", speed));
             } else if (angularSpeedX > ROTATION_THRESHOLD && angularSpeedY < -ROTATION_THRESHOLD) {
             // down to the right
             TaskScheduler.updateTask(() ->  bird.updateBird("downRight", speed));
             } else if (angularSpeedX > ROTATION_THRESHOLD) { // rotation around the X axis (upwards/downwards)
             // phone is rotated downwards
             TaskScheduler.updateTask(() ->  bird.updateBird("down", speed));
             } else if (angularSpeedX < -ROTATION_THRESHOLD) {
             // phone is rotated upwards
             TaskScheduler.updateTask(() -> bird.updateBird("up",speed));
             } else if (angularSpeedY > ROTATION_THRESHOLD) { // rotation around the Y axis (right/left)
             // phone is rotated left
             TaskScheduler.updateTask(() ->  bird.updateBird("left",speed));
             } else if (angularSpeedY < -ROTATION_THRESHOLD) {
             // phone is rotated right
             TaskScheduler.updateTask(() -> bird.updateBird("right", speed));
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

        //TaskScheduler.scheduleTask(() -> bird.updateBird("up", 0.1));

    }
    private boolean isReady() {
        return mMap != null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

class TaskScheduler {
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final AtomicReference<Runnable> taskRef = new AtomicReference<>();

    public static void scheduleTask(Runnable task) {
         taskRef.set(task);
        executor.scheduleAtFixedRate(TaskScheduler::executeTask, 0, 70, TimeUnit.MILLISECONDS);
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