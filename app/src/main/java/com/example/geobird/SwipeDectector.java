package com.example.geobird;

import android.app.Activity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

// copy paste from https://developer.android.com/develop/ui/views/touch-and-input/gestures/detector#java
/**
 * Custom class to detect a downwards or upwards swipe, currently only calls main.land and main.takeoff
 * respectively when a swipe occurs. Can be configured to react to horizontal swipes as well
 * */
class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
    private static final String DEBUG_TAG = "Gestures";
    private final int threshold = 100;
    private final int velocity_threshold = 100;
    private final Runnable onLand;
    private final Runnable onTakeOff;

    /**
     * Takes two lambdas one to run when swipe down occurs (onLand)
     * and one when swipe down occurs (onTakeOff)
     * */
    public SwipeDetector(Runnable onLand, Runnable onTakeOff) {
        super();
        this.onLand = onLand;
        this.onTakeOff = onTakeOff;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
        // https://www.youtube.com/watch?v=vNJyU-XW8_Y
        float xDiff = event2.getX() - event1.getX();
        float yDiff = event2.getY() - event1.getY();
        if (Math.abs(xDiff) > Math.abs(yDiff)) {
            // right or left swipe
            return true;
        } else {
            if (Math.abs(yDiff) > threshold && Math.abs(velocityY) > velocity_threshold) {
                if (yDiff > 0) {
                    // swiped down
                    Log.d(DEBUG_TAG, "Swiped down");
                     onLand.run();
                } else {
                    // swiped up
                    Log.d(DEBUG_TAG, "Swiped up");
                    onTakeOff.run();
                }
                return true;
            }
        }
        return false;
    }
}