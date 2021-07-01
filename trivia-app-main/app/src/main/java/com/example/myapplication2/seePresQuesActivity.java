package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class seePresQuesActivity extends AppCompatActivity {

    private Spinner userspinner;
    private Spinner quesspinner;
    private Button seeButton;
    private ListView listView;

    private ArrayList<String> UserList = new ArrayList<>();
    private ArrayList<String> QuesList = new ArrayList<>();
    private ArrayList<String> qlist = new ArrayList<>();

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_pres_ques);

        userspinner = findViewById(R.id.userspinner);
        quesspinner = findViewById(R.id.quesspinner);
        listView = findViewById(R.id.listView);
        seeButton = findViewById(R.id.seeButton);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.show,qlist);          //what array list will show in  the  listView
        listView.setAdapter(adapter);                                                                //show the questions in the listView

        ArrayAdapter<String> UserAdapter = new ArrayAdapter<>(seePresQuesActivity.this, R.layout.doubleline_spinner, UserList);
        UserAdapter.setDropDownViewResource(R.layout.doubleline_spinner);
        userspinner.setAdapter(UserAdapter);

        ArrayAdapter<String> QuesAdapter = new ArrayAdapter<>(seePresQuesActivity.this, R.layout.doubleline_spinner, QuesList);
        QuesAdapter.setDropDownViewResource(R.layout.doubleline_spinner);
        quesspinner.setAdapter(QuesAdapter);

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


        userspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String c_text = userspinner.getSelectedItem().toString();
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

        seeButton.setOnClickListener(new View.OnClickListener() {            // get the data from the user interface
            @Override
            public void onClick(View v) {

                String User_text = userspinner.getSelectedItem().toString();
                String Ques_text = quesspinner.getSelectedItem().toString();
                if(User_text.isEmpty() ||Ques_text.isEmpty() )
                {
                    Toast.makeText(seePresQuesActivity.this, "Category is empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("PersonalQuestions").child(User_text).child(Ques_text);    //From which category to bring the questions
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