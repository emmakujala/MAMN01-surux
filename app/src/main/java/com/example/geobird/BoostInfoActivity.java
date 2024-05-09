package com.example.geobird;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BoostInfoActivity extends AppCompatActivity {
    private Booster booster;
    private CustomVibrator vibe;

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
            booster.charge();
            booster.getSpeed(vibe::testVibrate);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            booster.release();
        }
        return super.onTouchEvent(event);
    }

    public void backFunc(View v) {
        finish();
    }

    public void nextPage(View v) {
        Intent intent = new Intent(BoostInfoActivity.this, InfoPageActivity.class);
        BoostInfoActivity.this.startActivity(intent);
    }
}