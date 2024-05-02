package com.example.geobird;


import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GamePlay {

    private static final String DEBUG_TAG = "latlong";
    Map<String, double[]> locationMap = new HashMap<>();
    String currentGoal;
    double goalLat;
    double goalLong;
    private int prevIndex = -1;
    private final Random generator = new Random();

    private int points = 0;

    public GamePlay() {
        addLocations();
    }

    private void addLocations() {
        double[] sto = new double[2];
        sto[0] = 59.3294;
        sto[1] = 18.0686;
        this.locationMap.put("Stockholm", sto);
        double[] gbg = new double[2];
        gbg[0] = 57.7075;
        gbg[1] = 11.9675;
        this.locationMap.put("Göteborg", gbg);
        double[] malmo = new double[2];
        malmo[0] = 55.6058;
        malmo[1] = 13.0358;
        this.locationMap.put("Malmö", malmo);
    }

    public String randomCity() {
        Object[] values = locationMap.keySet().toArray();
        int index = generator.nextInt(values.length);
        while (index == prevIndex) {
            index = generator.nextInt(values.length);
        }
        prevIndex = index;
        currentGoal = (String) values[index];
        goalLat = locationMap.get(currentGoal)[0];
        goalLong = locationMap.get(currentGoal)[1];
        return currentGoal;
    }

    public double[] getCoordinates(String key) {
        return locationMap.get(key);
    }

    boolean isCorrectLocation(double birdLat, double birdLong) {
        double deltaLat = Math.abs(birdLat - goalLat);
        double deltaLong = Math.abs(birdLong - goalLong);
        Log.d(DEBUG_TAG, Double.toString(deltaLat) + " " + Double.toString(deltaLong));
        return deltaLong < 0.5 && deltaLat < 0.5;
    }

    public void increasePoints() {
        this.points +=1;
    }

    public String getPoints() {
        return Integer.toString(this.points);
    }
}
