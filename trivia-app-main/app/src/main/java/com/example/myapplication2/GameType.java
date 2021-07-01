package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class GameType extends AppCompatActivity {

    private Button singleb;
    private Button multigame;
    private Button personalq;
    private Button topply;
    private MultiGameTriviaService multiGameTriviaService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_type);
        multiGameTriviaService = MultiGameTriviaService.getSingletonInstance();

        singleb = findViewById(R.id.Single);
        multigame = findViewById(R.id.vs);
        personalq = findViewById(R.id.Personal);
        topply = findViewById(R.id.Topplayers);

        multiGameTriviaService.listenToGameInvitations(new InvitationToGameListener() {
            @Override
            public void handleMultiGameInvitation(String gameKey, String category, String invitingUserName) {
                handleBattleInvitation(gameKey, category, invitingUserName);
            }
        });


        singleb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameType.this , Questionsmenu.class);
                intent.putExtra("isSingleGame", true);
                startActivity(intent);
            }
        });

        multigame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameType.this , Questionsmenu.class);
                intent.putExtra("isSingleGame", false);
                startActivity(intent);            }
        });


        topply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameType.this , showplayersdata.class));
            }
        });

        personalq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameType.this , PersonalActivity.class));
            }
        });

    }

    public void handleBattleInvitation(String gameId, String category, String otherPlayerName) {
        AlertDialog.Builder alert = new AlertDialog.Builder(GameType.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_title, null);
        alert.setCustomTitle(view);

        alert.setMessage("Do you want to compete against " + otherPlayerName + " in " + category + " trivia?");
        Activity self = this;
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                multiGameTriviaService.acceptMultiGameRequest(gameId);
                MultiGameAcceptGameListener acceptGameListener = new MultiGameAcceptGameListener(self);
                multiGameTriviaService.listenToGameUpdates(gameId, acceptGameListener);
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                multiGameTriviaService.cancelMultiGameRequest(gameId);
            }
        });

        alert.show();
    }
}