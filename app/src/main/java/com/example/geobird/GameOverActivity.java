package com.example.geobird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        TextView gameOver = findViewById(R.id.gameOver);
        gameOver.setText("You got " + getIntent().getExtras().getString("points") + " points");

        Button reStart = findViewById(R.id.button3);
        reStart.setOnClickListener(v -> {
            Intent intent = new Intent(GameOverActivity.this, MapsActivity.class);
            GameOverActivity.this.startActivity(intent);
        });
    }
}