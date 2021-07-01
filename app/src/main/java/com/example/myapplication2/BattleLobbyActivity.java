package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class BattleLobbyActivity extends AppCompatActivity {
    private Spinner availableUsersSpinner;
    private Button playButton;
    private MultiGameTriviaService multiGameTriviaService;
    private ArrayList<String> spinnerMatchIds;
    private String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        multiGameTriviaService = MultiGameTriviaService.getSingletonInstance();

        setContentView(R.layout.battle_loby);
        Bundle bundle = getIntent().getExtras();
        this.category = bundle.getString("category");

        availableUsersSpinner = findViewById(R.id.playersDropdown);
        playButton = findViewById(R.id.playButton);
        spinnerMatchIds = new ArrayList<>();

        UsersService.getConnectedUsers(new getDataListener() {
            @Override
            public void getData(HashMap<String, Object> connectedUsers) {
                setConnectedUsersInLobby(connectedUsers);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otherPlayerUserId = spinnerMatchIds.get(availableUsersSpinner.getSelectedItemPosition());
                Intent intent = new Intent(BattleLobbyActivity.this, WaitForGameAcceptActivity.class);
                intent.putExtra("category", category);
                intent.putExtra("otherPlayerId", otherPlayerUserId);
                startActivity(intent);
            }
        });
    }



    public void setConnectedUsersInLobby(HashMap<String, Object> users) {
        String currentUserId = UsersService.getUserId();
        ArrayList<String> userNames = new ArrayList<>();

        // get usernames of all connected users
        for (Object user: users.values()) {
            HashMap<String, String> userData = (HashMap<String, String>)user;
            String userId = userData.get("id");
            if (!userId.equals(currentUserId)) {
                String name = userData.get("username");
                userNames.add(name);
                spinnerMatchIds.add(userId);
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.doubleline_spinner, userNames);
        adapter.setDropDownViewResource(R.layout.doubleline_spinner);
        availableUsersSpinner.setAdapter(adapter); // this will set list of values to spinner
    }

}