package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    private EditText emaillog;
    private EditText passwordlog;
    private Button loginlog;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emaillog = findViewById(R.id.emaillog);
        passwordlog = findViewById(R.id.passwordlog);
        loginlog = findViewById(R.id.loginlog);

        auth = FirebaseAuth.getInstance();

        loginlog.setOnClickListener(new View.OnClickListener() {            // get the data from the user interface
            @Override
            public void onClick(View v) {
                String txt_email = emaillog.getText().toString();
                String txt_password = passwordlog.getText().toString();
                loginUser(txt_email, txt_password);
            }
        });
    }

    private void loginUser(String email, String password) {

        UsersService.loginUser(LoginActivity.this,email, password, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                handleLoginUI(task.isSuccessful());
            }
        });


    }

    private void handleLoginUI (boolean isSuccessful) {
        if (isSuccessful)
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("admins");    //look up if the user is admin - from the admin database
            reference.addListenerForSingleValueEvent(new ValueEventListener() {

                boolean isAdmin=false;

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userUID = UsersService.getUserId();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {              //Check is the uid of the user is in the admin database
                        if (snapshot.getRef().getKey().equals(userUID))
                        {
                            isAdmin=true;
                            break;
                        }
                    }


                    if (isAdmin)
                    {
                        Toast.makeText(LoginActivity.this , "login successfull" , Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, adminlogin.class));                    //If a connection is established, go to the admin  window
                        finish();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this , "login successfull" , Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, GameType.class));                    //If a connection is established, go to the categories window
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else
        {
            Toast.makeText(LoginActivity.this, "Failed to login, check email Or password", Toast.LENGTH_SHORT).show();
        }
    }

}
