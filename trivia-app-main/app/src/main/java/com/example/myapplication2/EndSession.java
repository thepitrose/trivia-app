package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EndSession extends AppCompatActivity {

    private TextView viewPoint;
    private Button logout;
    private Button newgame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_session);
        viewPoint  = findViewById(R.id.showpint);
        logout = findViewById(R.id.logoutend);
        newgame = findViewById(R.id.newgame);

        getSupportActionBar().setTitle("Round summary");

        // get user score in the round from bundle
        Bundle bundle = getIntent().getExtras();
        Integer score = bundle.getInt("score");
        viewPoint.setText(score.toString());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EndSession.this , "logout successfull" , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EndSession.this, MainActivity.class));
                FirebaseAuth.getInstance().signOut();
            }
        });



        newgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EndSession.this, GameType.class));
            }
        });



    }

    @Override
    public void onBackPressed()
    {

    }

}
