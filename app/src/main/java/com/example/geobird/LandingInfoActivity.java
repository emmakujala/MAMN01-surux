package com.example.geobird;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LandingInfoActivity extends AppCompatActivity {
    private GestureDetectorCompat mDetector;
    private ImageView bird;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landing_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mDetector = new GestureDetectorCompat(this, new SwipeDetector(this::onLand, this::onTakeOff));
        bird = findViewById(R.id.bird);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    private void onLand() {
        bird.setRotation(180);
    }

    private void onTakeOff() {
        bird.setRotation(0);
    }

    public void backFunc(View v) {
        finish();
    }

    public void nextPage(View v) {
        Intent intent = new Intent(LandingInfoActivity.this, BoostInfoActivity.class);
        LandingInfoActivity.this.startActivity(intent);
    }
}