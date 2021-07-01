package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PersonalActivity extends AppCompatActivity {

    private Button uploudQues;
    private Button findQues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        uploudQues = findViewById(R.id.uploudQues);
        findQues = findViewById(R.id.findQues);

        findQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalActivity.this , GetPersonalActivity.class);
                intent.putExtra("isSingleGame", true);
                startActivity(intent);
            }
        });

        uploudQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalActivity.this , CreatePersonalCategory.class);
                intent.putExtra("isSingleGame", true);
                startActivity(intent);
            }
        });

    }
}