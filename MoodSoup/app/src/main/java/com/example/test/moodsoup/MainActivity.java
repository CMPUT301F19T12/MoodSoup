package com.example.test.moodsoup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ImageButton profile;
    ImageButton home;
    FloatingActionButton addMood;
    ListView moodList;
    ArrayList<Mood> moodArray;
    ArrayAdapter<Mood> moodAdapter;
    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize views
        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        moodList = findViewById(R.id.moodList);
        addMood = findViewById(R.id.addMood);
        logout = findViewById(R.id.logout);
        
        //Set adapter for moodList
        moodArray = new ArrayList<>();
        moodAdapter = new MoodList(this,moodArray);
        moodList.setAdapter(moodAdapter);

        //Logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this,Login.class);
                startActivity(intent);
            }
        });

        //Move to user's profile screen on profile button pressed
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move screen to profile screen
                Intent ProfileIntent = new Intent(getApplicationContext(), Profile.class);
                startActivity(ProfileIntent);
            }
        });

        addMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NewMood.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            Intent intent = new Intent(MainActivity.this,Login.class);
            startActivity(intent);
        }
    }
}
