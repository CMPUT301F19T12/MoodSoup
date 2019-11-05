package com.example.test.moodsoup.old_activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.moodsoup.Following;
import com.example.test.moodsoup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowSearch extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_search);
        final EditText UserName = (EditText) findViewById(R.id.Search_User);
        final Button search = findViewById(R.id.search_button);
        final String TAG = "Sample";
        final FirebaseFirestore db;

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String toSearch = UserName.getText().toString();
                db.collection("Users")
                        .document(toSearch)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        Toast.makeText(getApplicationContext(), "User Found",
                                                Toast.LENGTH_SHORT).show();

                                        //Add searched user to my pending
                                        HashMap<String, String> pendingData = new HashMap<>();
                                        pendingData.put("pending",toSearch);
                                        db.collection("Users")
                                                .document(user.getEmail())
                                                .collection("pending")
                                                .document(toSearch)
                                                .set(pendingData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG,"Data Addition Successful");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG,"Data Addition Failed" + e.toString());
                                                    }
                                                });

                                        //Add me to searched user's request
                                        HashMap<String, String> requestData = new HashMap<>();
                                        requestData.put("request", user.getEmail());
                                        db.collection("Users")
                                                .document(toSearch)
                                                .collection("request")
                                                .document(user.getEmail())
                                                .set(requestData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG,"Data Addition Successful");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG,"Data Addition Failed" + e.toString());
                                                    }
                                                });

                                        Intent FollowingIntent = new Intent(getApplicationContext(), Following.class);
                                        startActivity(FollowingIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to find user",
                                                Toast.LENGTH_SHORT).show();
                                        Intent FollowingIntent = new Intent(getApplicationContext(), Following.class);
                                        startActivity(FollowingIntent);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });


    }
}