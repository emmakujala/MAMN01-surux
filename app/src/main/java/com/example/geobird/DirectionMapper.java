package com.example.geobird;

import android.util.Log;

public class DirectionMapper {

    public String direction(float x, float y) {
        x *= -1f;
        Log.d("CORDS, X | Y",   x + " | " + y);
        double rad = Math.atan(x / y);
        double angle = absoluteAngle(Math.abs(rad), x, y);
        Log.d("CORDS, A", "Angle: " + angle);

        if (angle < 22.5 || angle > 337.5) {
            return "right";
        } else if (angle > 22.5 && angle < 67.5) {
            return "upperRight";
        } else if (angle > 67.5 && angle < 112.5) {
            return "up";
        } else if (angle > 112.5 && angle < 157.5) {
            return "upperLeft";
        } else if (angle > 157.5 && angle < 202.5) {
            return "left";
        } else if (angle > 202.5 && angle < 247.5) {
            return "downLeft";
        } else if (angle > 247.5 && angle < 292.5) {
            return "down";
        } else if (angle > 292.5 && angle < 337.5) {
            return "downRight";
        }
        Log.d("ERROR", "Angle was: " + angle);
        return "";
    }

    private double absoluteAngle(double rad, float x, float y) {
        double angle = Math.toDegrees(rad);
        if (x > 0 && y > 0) {
            Log.d("Quadrant", "1");
            return angle;
        } else if (x > 0 && y < 0) {
            Log.d("Quadrant", "2");
            return 180 - angle;
        } else if (x < 0 && y < 0) {
            Log.d("Quadrant", "3");
            return 180 + angle;
        } else if (x < 0 && y > 0) {
            Log.d("Quadrant", "4");
            return 360 - angle;
        } else {
            Log.d("Quadrant ERROR", "X: " + x + " | y: " + y);
            return 0;
        }
    }
}
