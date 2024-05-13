package com.example.geobird;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BoostInfoActivity extends AppCompatActivity {
    private Booster booster;
    private CustomVibrator vibe;
    private final Scheduler scheduler = new Scheduler(100);
    private boolean canBoost = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_boost_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        vibe = new CustomVibrator<>(getSystemService(Vibrator.class));
        try {
            booster = new Booster();
        } catch (Exception e) {
            Log.d("ERROR", e.toString());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (canBoost == false) {
                return super.onTouchEvent(event);
            }
            scheduler.updateTask(() -> {});
            findViewById(R.id.bird).animate().translationY(0);
            booster.charge();
            scheduler.updateTask(() -> booster.getSpeed(vibe::vibrateDoubleClick));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (canBoost == false) {
                return super.onTouchEvent(event);
            }
            booster.release(vibe::vibrateMedium);
            ViewPropertyAnimator ani = findViewById(R.id.bird).animate();
            if (booster.getSpeed(() -> {}) > 0.03) {
                ani.translationY(-1500);
            }
            canBoost = false;
            scheduler.updateTask(() -> boostRelease(ani));
        }
        return super.onTouchEvent(event);
    }

    private void boostRelease(ViewPropertyAnimator ani) {
        if (booster.getSpeed(() -> {}) > 0.03) {
            return;
        }
        scheduler.updateTask(() -> {});
        ani.translationY(0);
        canBoost = true;
    }

    public void backFunc(View v) {
        booster.stop();
        finish();
    }

    public void nextPage(View v) {
        // todo
        // booster is not stopped so pretty sure it is leaking resources now but
        // I have no idea how to fix it since constructor is not called for this activity
        // again if finnish is called so whatever :)
        Intent intent = new Intent(BoostInfoActivity.this, InfoPageActivity.class);
        BoostInfoActivity.this.startActivity(intent);
    }
}