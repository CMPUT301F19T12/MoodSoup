package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    EditText username,password;
    Button login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((username.getText().toString().equals("admin")) && (password.getText().toString().equals("admin")))
                {
                    Toast.makeText(MainActivity.this,"Authenticated! Logging in...", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Invalid username or password",Toast.LENGTH_LONG).show();
                }
            }
        });




    }
}


