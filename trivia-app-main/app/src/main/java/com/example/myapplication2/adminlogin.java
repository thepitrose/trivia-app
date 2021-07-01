package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class adminlogin extends AppCompatActivity {

    private Button addq;
    private Button seegeneral;
    private Button seepersonal;
    private Button logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);

        addq = findViewById(R.id.addq);
        seegeneral = findViewById(R.id.seegeneral);
        seepersonal = findViewById(R.id.seepersonal);
        logout = findViewById(R.id.logout);


        addq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(adminlogin.this, addQuestion.class));
            }
        });

        seegeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(adminlogin.this, seeQuestion.class));
            }
        });

        seepersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(adminlogin.this, seePresQuesActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(adminlogin.this , "logout successfull" , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(adminlogin.this, MainActivity.class));
                FirebaseAuth.getInstance().signOut();
            }
        });

    }
}