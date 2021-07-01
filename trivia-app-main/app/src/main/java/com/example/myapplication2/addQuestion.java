package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class addQuestion extends AppCompatActivity {

    private Spinner spinner;
    private EditText qtxt;
    private EditText an1txt;
    private EditText an2txt;
    private EditText an3txt;
    private EditText an4txt;
    private Button addbutton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        spinner = findViewById(R.id.spinner);
        qtxt = findViewById(R.id.questiontext);
        an1txt = findViewById(R.id.answer1);
        an2txt = findViewById(R.id.answer2);
        an3txt = findViewById(R.id.answer3);
        an4txt = findViewById(R.id.answer4);
        addbutton = findViewById(R.id.addbutton);

        ArrayAdapter<String> myAdapter= new ArrayAdapter<>(addQuestion.this,  R.layout.doubleline_spinner, getResources().getStringArray(R.array.category));
        myAdapter.setDropDownViewResource(R.layout.doubleline_spinner);
        spinner.setAdapter(myAdapter);

        getSupportActionBar().setTitle("addQuestion - admin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();





        addbutton.setOnClickListener(new View.OnClickListener() {            // get the data from the user interface
            @Override
            public void onClick(View v) {
                String q_txt = qtxt.getText().toString();
                String a1_txt = an1txt.getText().toString();
                String a2_txt = an2txt.getText().toString();
                String a3_txt = an3txt.getText().toString();
                String a4_txt = an4txt.getText().toString();
                String c_text = spinner.getSelectedItem().toString();


                if (q_txt.isEmpty() || a1_txt.isEmpty() || a2_txt.isEmpty() || a3_txt.isEmpty() || a4_txt.isEmpty() || c_text.isEmpty())
                {
                    Toast.makeText(addQuestion.this , "One or more of the fields are empty" , Toast.LENGTH_SHORT).show();
                }

                else
                {
                    addAquestion(q_txt, a1_txt,a2_txt,a3_txt,a4_txt,c_text);
                }
            }
        });

    }

    private void addAquestion(String q_txt, String a1_txt, String a2_txt, String a3_txt, String a4_txt, String c_text) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Questions").child("animals");
        reference.addListenerForSingleValueEvent(new ValueEventListener(){
            ArrayList<String> temp = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {                  //get the data
                    temp.add(snapshot.getValue().toString());
                }

                String qNum =String.valueOf(temp.size()+1);

                HashMap<String , Object> map = new HashMap<>();
                if (q_txt.charAt(q_txt.length()-1)=='?')
                {
                    map.put("Q", q_txt);
                }
                else
                {
                    map.put("Q", q_txt+'?');
                }
                map.put("XA" , a1_txt);
                map.put("XB" , a2_txt);
                map.put("XC" , a3_txt);
                map.put("XD" , a4_txt);

                FirebaseDatabase.getInstance().getReference().child("Questions").child(c_text).child(qNum).updateChildren(map);
                Toast.makeText(addQuestion.this , "Add the question successfull" , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(addQuestion.this , adminlogin.class));
                finish();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

