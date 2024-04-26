package com.example.geobird;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class GameController {

    private final Scheduler scheduler;
    private final GamePlay game;
    private final Bird bird;
    private final TextView pView;
    private final TextView gView;
    private final TextView timer;

    private final Activity activity;

    public GameController(Activity activity, Scheduler scheduler, GamePlay game, Bird bird, TextView gView, TextView pView, TextView timer) {
        this.scheduler = scheduler;
        this.game = game;
        this.bird = bird;
        this.pView = pView;
        this.gView = gView;
        this.timer = timer;
        this.activity = activity;
        timer();

    }

    public void onLanding() {
        scheduler.pause();
        if (game.isCorrectLocation(bird.getBirdLat(), bird.getBirdLong())) {
            game.increasePoints();
            pView.setText(game.getPoints());
            gView.setText(game.randomCity());
        }

    }

    public void onLiftOff() {
        scheduler.resume();
    }

    public void gameOver() {
        Intent intent = new Intent(activity, GameOverActivity.class);
        intent.putExtra("points", game.getPoints());
        activity.startActivity(intent);

    }

   public void timer() {
       new CountDownTimer(30000,1000) {
           @Override
           public void onTick(long millisUntilFinished) {
                timer.setText(Long.toString(millisUntilFinished / 1000));
           }

           @Override
           public void onFinish() {
                scheduler.pause();
                gView.setText("Game over");
                gameOver();
           }
       }.start();
   }


}
