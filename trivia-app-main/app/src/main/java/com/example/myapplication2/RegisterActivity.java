package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText repassword;
    private EditText username;
    private Button register;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        register = findViewById(R.id.register);
        username = findViewById(R.id.username);

        auth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {              // get the data from the user interface
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_repassword = repassword.getText().toString();
                String txt_username = username.getText().toString();

                // ----------------------check password and mail

                int x=-1;
                int y=0;
                for (int i=0; i<txt_email.length()-1 ; i++)
                {
                    if(txt_email.charAt(i)=='@')                  //to - Check if the email has at least one character before the @
                    {
                        x=i;
                    }
                    if(txt_email.charAt(i)=='.')                  //to - Check if the email has at least two characters after the last dot
                    {
                        y=i+1;
                    }

                }

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password))            //If one of the fields is empty
                {
                    Toast.makeText(RegisterActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                }

                // ----------------------check mail

                if (x==-1 || x==0 ||txt_email.length()-y<2)         //Check if the email has at least one character before the @   OR  if the email has at least two characters after the last dot
                {
                    Toast.makeText(RegisterActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }

                // ----------------------check password
                else if (txt_password.length() < 6)                 //if the password to short
                {
                    Toast.makeText(RegisterActivity.this, "password to short", Toast.LENGTH_SHORT).show();
                }

                else if (!txt_password.equals(txt_repassword))          //if the password and Verify password are not identical
                {
                    Toast.makeText(RegisterActivity.this, "password and Verify password are not identical", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    registerUser(txt_email, txt_password,txt_username);
                }
            }
        });

    }


    private void registerUser(String email, String password, String username)
    {
        UsersService.registerUser(RegisterActivity.this,email, password, username, new OnCompleteListener<AuthResult>() {

            public void test()
            {
                Toast.makeText(RegisterActivity.this, "faile to register ,Invalid email address Or the email is already registered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful())
                {
                    Toast.makeText(RegisterActivity.this, "successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this , MainActivity.class));
                    finish();
                }

            }


        });
    }



}
