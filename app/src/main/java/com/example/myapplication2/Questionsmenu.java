package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Questionsmenu extends AppCompatActivity {

    private Button bAnimals;
    private Button bGeography;
    private Button bMusic;
    private Button bhistory;
    private Button bCelebrity;
    private boolean isSingleGame = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionsmenu);
        Bundle bundle = getIntent().getExtras();
        this.isSingleGame = bundle.getBoolean("isSingleGame", true);
        getSupportActionBar().setTitle("Choose your category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bAnimals = findViewById(R.id.Animals);
        bGeography = findViewById(R.id.Geography);
        bMusic = findViewById(R.id.Music);
        bhistory = findViewById(R.id.history);
        bCelebrity = findViewById(R.id.Celebrity);


        bAnimals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick("animals");
            }
        });

        bGeography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick("geography");
            }
        });

        bMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick("Music");
            }
        });

        bhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick("History");
            }
        });

        bCelebrity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  onCategoryClick("Celebrity");
            }
        });

    }
    protected void onCategoryClick(String category) {
        if (isSingleGame) {
            Intent intent = new Intent(Questionsmenu.this, BaseQuesActivity.class);
            intent.putExtra("key", category);
            intent.putExtra("type", "Questions");
            startActivity(intent);
        } else {
            Intent intent = new Intent(Questionsmenu.this, BattleLobbyActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("type", "Questions");
            intent.putExtra("Ques", "Filling");
            startActivity(intent);
        }
    }
}
