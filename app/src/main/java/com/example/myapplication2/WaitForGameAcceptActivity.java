package com.example.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WaitForGameAcceptActivity extends AppCompatActivity {
   private MultiGameTriviaService multiGameTriviaService = MultiGameTriviaService.getSingletonInstance();
   private MultiGameAcceptGameListener acceptGameListener = new MultiGameAcceptGameListener(this);
   private String currentGameKey = null;
   private Button cancelInvitationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_game_accept);
        cancelInvitationButton = findViewById(R.id.cancelButton);
        Bundle bundle = getIntent().getExtras();
        String category = bundle.getString("category");
        String otherPlayerUserId = bundle.getString("otherPlayerId");
        String currentUserId = UsersService.getUserId();
        this.currentGameKey = multiGameTriviaService.createNewMultiGame(
                currentUserId,
                otherPlayerUserId,
                category,
                (GameEventsListener) this.acceptGameListener);

        cancelInvitationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentGameKey != null) {
                    multiGameTriviaService.cancelMultiGameRequest(currentGameKey);
                }
                else {
                    acceptGameListener.cancelGame();
                }
            }
        });
    }
}