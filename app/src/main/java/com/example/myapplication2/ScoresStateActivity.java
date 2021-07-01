package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.TimeUnit;

public class ScoresStateActivity extends AppCompatActivity {

    private static final long DURATION = TimeUnit.SECONDS.toMillis(7);      // the set the TIMER by seconds
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mid_score_state);
        Bundle bundle = getIntent().getExtras();
        String player1Name = bundle.getString("Player1Name");
        String player2Name = bundle.getString("Player2Name");
        long player1RoundScore = bundle.getLong("Player1Score");
        long player2RoundScore = bundle.getLong("Player2Score");
        boolean isEndGame = bundle.getBoolean("isEndGame");

        TextView player1Title = findViewById(R.id.battleLobyTitle);
        TextView player2Title = findViewById(R.id.player2Name);
        TextView player1ScoreDesc = findViewById(R.id.player1Score);
        TextView player2ScoreDesc = findViewById(R.id.player2Score);
        Button mainScreenButton = findViewById(R.id.endgameButton);

        player1Title.setText(player1Name);
        player2Title.setText(player2Name);
        player1ScoreDesc.setText(player1RoundScore + " points!");
        player2ScoreDesc.setText(player2RoundScore + " points!");

        if (isEndGame) {
            mainScreenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ScoresStateActivity.this, GameType.class));
                }
            });
        } else {
            mainScreenButton.setVisibility(View.INVISIBLE);
            // Set time for stop showing the mid score state and move to the next question
            timer = new CountDownTimer(DURATION, 1000) {   //the timer

                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    finish();
                }
            }.start();
        }
    }
}