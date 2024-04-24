package com.example.geobird;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import android.os.Handler;

public class Bird  {

    private double birdLat;
    private double birdLong;
    private LatLng birdPos;
    public GroundOverlayOptions bird;
    public GroundOverlay birdOverlayObject;

    public Bird(double birdLat, double birdLong, GoogleMap mMap, Resources resources) {
        this.birdLat = birdLat;
        this.birdLong = birdLong;
        this.birdPos = new LatLng(birdLat,birdLong);
        Bitmap birdBitmap = BitmapFactory.decodeResource(resources, R.raw.bird);
        this.bird = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(birdBitmap))
                .position(birdPos, 100000f, 100000f)
                .bearing(0);
        birdOverlayObject = mMap.addGroundOverlay(bird);
    }


    public LatLng updatePos(String dir, double speed) {
        System.out.println("updatePos");
        switch (dir) {
            case "upperLeft":
                birdOverlayObject.setBearing(315);
                birdLat -= speed;
                birdLong -= speed;
                break;
            case "upperRight":
                birdOverlayObject.setBearing(45);
                birdLat += speed;
                birdLong -= speed;
                break;
            case "downLeft":
                birdOverlayObject.setBearing(225);
                birdLat -= speed;
                birdLong += speed;
                break;
            case "downRight":
                birdOverlayObject.setBearing(115);
                birdLat += speed;
                birdLong += speed;
                break;
            case "down":
                birdOverlayObject.setBearing(180);
                birdLat += speed;
                break;
            case "up":
                birdOverlayObject.setBearing(0);
                birdLat -= speed;
                break;
            case "left":
                birdOverlayObject.setBearing(270);
                birdLong -= speed;
                break;
            default:
                birdOverlayObject.setBearing(90);
                birdLong += speed;
                break;
        }

        birdPos = new LatLng(birdLat, birdLong);
        return birdPos;
    }

    public void updateBird(String dir, double speed) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
        birdPos = updatePos(dir,speed);
        birdOverlayObject.setPosition(birdPos);
        bird.bearing(315);
    });
    }

    public LatLng getBirdPos() {
        return this.birdPos;
    }
}
