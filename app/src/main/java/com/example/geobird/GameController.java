package com.example.geobird;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class GameController {

    private final Scheduler scheduler;
    private final GamePlay game;
    private final Bird bird;
    private final TextView pView;
    private final TextView gView;
    private final TextView timerView;
    private long timeLeft;
    private CountDownTimer timer;
    private final Marker marker;

    private final Activity activity;

    public GameController(Activity activity, Scheduler scheduler, GamePlay game, Bird bird, TextView gView, TextView pView, TextView timerView, Marker marker) {
        this.scheduler = scheduler;
        this.game = game;
        this.bird = bird;
        this.pView = pView;
        this.gView = gView;
        this.timerView = timerView;
        this.activity = activity;
        this.marker = marker;
        timerStart(60000);

    }

    public void onLanding() {
        scheduler.pause();
        marker.setPosition(new LatLng(game.goalLat,game.goalLong));
        marker.setVisible(true);
        game.calculatePoints(bird.getBirdLat(), bird.getBirdLong());
        pView.setText(game.getPoints());
        gView.setText(game.randomCity());
        bird.setHasLanded(true);
        /**if (game.isCorrectLocation(bird.getBirdLat(), bird.getBirdLong())) {
            game.increasePoints();
            pView.setText(game.getPoints());
            gView.setText(game.randomCity());
        }*/


    }

    public void onLiftOff() {
        bird.setHasLanded(false);
        marker.setVisible(false);
        scheduler.resume();
    }

    public void gameOver() {
        if (activity instanceof MapsActivity) {
            ((MapsActivity) activity).booster.stop();
        }
        Intent intent = new Intent(activity, GameOverActivity.class);
        intent.putExtra("points", game.getPoints());
        activity.startActivity(intent);
        activity.finish();

    }

   public void timerStart(long millisUntilFinished) {
       timer = new CountDownTimer(millisUntilFinished,1000) {
           @Override
           public void onTick(long millisUntilFinished) {
               timeLeft = millisUntilFinished;
               timerView.setText(Long.toString(millisUntilFinished / 1000));
           }

           @Override
           public void onFinish() {
                scheduler.pause();
                gView.setText("Game over");
                gameOver();
           }
       };
       timer.start();
   }

   public void pauseTimer() {
        timer.cancel();
   }

   public void resumeTimer() {
        timerStart(timeLeft);
   }


}
