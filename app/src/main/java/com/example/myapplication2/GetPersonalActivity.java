package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GetPersonalActivity extends AppCompatActivity {

    private Spinner spinnerUser;
    private Spinner spinnerQues;
    private Button bget;

    private ArrayList<String> UserList = new ArrayList<>();
    private ArrayList<String> QuesList = new ArrayList<>();

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_personal);

        spinnerUser = findViewById(R.id.spinnerUser);
        spinnerQues = findViewById(R.id.spinnerQues);
        bget = findViewById(R.id.bget);

        ArrayAdapter<String> UserAdapter = new ArrayAdapter<>(GetPersonalActivity.this, R.layout.doubleline_spinner, UserList);
        UserAdapter.setDropDownViewResource(R.layout.doubleline_spinner);
        spinnerUser.setAdapter(UserAdapter);

        ArrayAdapter<String> QuesAdapter = new ArrayAdapter<>(GetPersonalActivity.this, R.layout.doubleline_spinner, QuesList);
        QuesAdapter.setDropDownViewResource(R.layout.doubleline_spinner);
        spinnerQues.setAdapter(QuesAdapter);

        UserList.add("");
        QuesList.add("");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("PersonalQuestions");       // Connects to the database - in the userData
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {                  //get the data
                    UserList.add(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        UserAdapter.notifyDataSetChanged();


        spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String c_text = spinnerUser.getSelectedItem().toString();
                QuesList.clear();
                QuesList.add("");
                QuesAdapter.notifyDataSetChanged();
                if(c_text!="")
                {
                DatabaseReference QuesReference = FirebaseDatabase.getInstance().getReference().child("PersonalQuestions").child(c_text);       // Connects to the database - in the userData
                QuesReference.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot QuesdataSnapshot) {

                        for (DataSnapshot Quessnapshot : QuesdataSnapshot.getChildren())
                        {                  //get the data
                            QuesList.add(Quessnapshot.getKey());
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        bget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String User_text = spinnerUser.getSelectedItem().toString();
                String Ques_text = spinnerQues.getSelectedItem().toString();


                Intent intent = new Intent(GetPersonalActivity.this, BaseQuesActivity.class);
                intent.putExtra("key", User_text);
                intent.putExtra("Ques", Ques_text);
                intent.putExtra("type", "PersonalQuestions");
                startActivity(intent);
            }
        });


    }

}