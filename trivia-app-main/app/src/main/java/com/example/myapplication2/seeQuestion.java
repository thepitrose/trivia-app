package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class seeQuestion extends AppCompatActivity {

    private Spinner spinner;
    private Button seeButton;
    private ListView listView;

    private ArrayList<String> qlist = new ArrayList<>();

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_question);

        spinner = findViewById(R.id.spinner);
        listView = findViewById(R.id.listView);
        seeButton = findViewById(R.id.seeButton);

        ArrayAdapter<String> myAdapter= new ArrayAdapter<>(seeQuestion.this, R.layout.doubleline_spinner, getResources().getStringArray(R.array.category));
        myAdapter.setDropDownViewResource(R.layout.doubleline_spinner);
        spinner.setAdapter(myAdapter);

        getSupportActionBar().setTitle("seeQuestion - admin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.show,qlist);          //what array list will show in  the  listView
        listView.setAdapter(adapter);                                                                //show the questions in the listView

        seeButton.setOnClickListener(new View.OnClickListener() {            // get the data from the user interface
            @Override
            public void onClick(View v) {

                String c_text = spinner.getSelectedItem().toString();
                if(c_text.isEmpty())
                {
                    Toast.makeText(seeQuestion.this, "Category is empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Questions").child(c_text);    //From which category to bring the questions
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {                                             // "Listener" that communicate with database
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {                     //When data is received from the database

                            qlist.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {                  //get the data
                                qlist.add(snapshot.getValue().toString());
                                Collections.sort(qlist);                                               //Make sure the data theat received will be correct
                            }
                            listView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });
    }
}