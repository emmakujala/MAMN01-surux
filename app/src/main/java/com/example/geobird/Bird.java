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
import android.util.Log;

public class Bird  {
    private double birdLat;
    private double birdLong;
    private LatLng birdPos;
    public GroundOverlayOptions bird;
    public GroundOverlay birdOverlayObject;

    private boolean hasLanded;

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
        this.hasLanded = false;
    }

    /**
     *              0
     *         315  |  45
     *            \ | /
     *     270 ---- * ---- 90
     *            / | \
     *         225  |  135
     *             180
     * */
    public LatLng updatePos(String dir, Double speed) {
        switch (dir) {
            case "upperLeft":
                birdOverlayObject.setBearing(315);
                birdLat += speed;
                birdLong -= speed;
                break;
            case "upperRight":
                birdOverlayObject.setBearing(45);
                birdLat += speed;
                birdLong += speed;
                break;
            case "downLeft":
                birdOverlayObject.setBearing(225);
                birdLat -= speed;
                birdLong -= speed;
                break;
            case "downRight":
                birdOverlayObject.setBearing(135);
                birdLat -= speed;
                birdLong += speed;
                break;
            case "down":
                birdOverlayObject.setBearing(180);
                birdLat -= speed;
                break;
            case "up":
                birdOverlayObject.setBearing(0);
                birdLat += speed;
                break;
            case "left":
                birdOverlayObject.setBearing(270);
                birdLong -= speed;
                break;
            case "right":
                birdOverlayObject.setBearing(90);
                birdLong += speed;
                break;
            default:
                break;
        }
        wrapAroundBird();
        birdPos = new LatLng(birdLat, birdLong);
        return birdPos;
    }

    private void wrapAroundBird() {
        // note this is a fairly ugly fix might want to find a better way
        /**
         * The idea is by transforming to a regular cord system with bottom left corner
         * being 0, 0 we can use mod to get the birds position then translate back
         * to normal cords for the location on map to make it like the bird is flying around the screen
         *
         * This is the normal box where sweden is in the middle
         *      69 +--------+ 69
         *         |        |
         *         |        |
         *         |        |
         *         |        |
         *     55  +--------+ 55
         *        10        24
         *
         * We transform to (numbers not accurate)
         *      13 +--------+ 13
         *         |        |
         *         |        |<-- Bird
         * Bird <--|        |
         *         |        |
         *       0 +--------+ 0
         *        0        14
         *
         *  14 will in this case be longMod and 13 will be latMod
         * */
        // long = x, lat = y
        double latMod = MapsActivity.swedenLatMax - MapsActivity.swedenLatMin;
        double longMod = MapsActivity.swedenLongMax - MapsActivity.swedenLongMin;
        double transLat = birdLat - MapsActivity.swedenLatMin;
        double transLong = birdLong - MapsActivity.swedenLongMin;

        // https://stackoverflow.com/questions/4412179/best-way-to-make-javas-modulus-behave-like-it-should-with-negative-numbers/4412200#4412200
        if (transLong < 0 || transLong > longMod) {
            // too far left or right
            double wrappedLong = (transLong % longMod + longMod) % longMod;
            birdLong = wrappedLong + MapsActivity.swedenLongMin;
            birdLat = (latMod - transLat) + MapsActivity.swedenLatMin;
        } else if (transLat < 0 || transLat > latMod) {
            // too far down or up
            double wrappedLat = (transLat % latMod + latMod) % latMod;
            birdLat = wrappedLat + MapsActivity.swedenLatMin;
            birdLong = (longMod - transLong) + MapsActivity.swedenLongMin;
        } else {
            Log.d("ERROR", "Translated long and lat failed to check side, Lat: " + transLat + ", Long: " + transLong);
        }
    }

    public void updateBird(String dir, Booster booster) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        if (!hasLanded) {
            mainHandler.post(() -> {
                birdPos = updatePos(dir, booster.getSpeed());
                birdOverlayObject.setPosition(birdPos);
            });
        }

    }
    public LatLng getBirdPos() {return this.birdPos;}
    public double getBirdLat() {
        return this.birdLat;
    }
    public double getBirdLong() {return this.birdLong;}

    public boolean hasLanded() {return this.hasLanded;}

    public void setHasLanded(boolean bool) {this.hasLanded = bool;}
}
