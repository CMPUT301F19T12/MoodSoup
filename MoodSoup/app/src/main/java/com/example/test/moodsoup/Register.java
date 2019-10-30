package com.example.test.moodsoup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Register extends AppCompatActivity {

    // Initialize Variables
    TextView emailTV;
    TextView usernameTV;
    TextView passwordTV;
    Button registerBTN;
    String TAG = "Sample";
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String uniqueID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Assign Variables
        emailTV = findViewById(R.id.email_new_user_tv);
        passwordTV = findViewById(R.id.password_new_user_tv);
        registerBTN = findViewById(R.id.register_new_user_btn);
        usernameTV = findViewById(R.id.username_new_user_tv);
        final CollectionReference collectionReference = db.collection("Users");

        // Upon pressing the register button
        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get strings from TextView
                findViewById(R.id.register_invalid_username).setVisibility(View.INVISIBLE);
                findViewById(R.id.register_invalid_email).setVisibility(View.INVISIBLE);

                final String email = emailTV.getText().toString();
                final String password = passwordTV.getText().toString();

                /*DatabaseReference a = FirebaseDatabase.getInstance().getReference("Unique").child("ID");
                a.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        uniqueID = dataSnapshot.child("Current").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                int ID = Integer.valueOf(uniqueID)+1;
                uniqueID = Integer.toString(ID);
                while (uniqueID.length()<4){
                    uniqueID = "0"+uniqueID;
                }*/
                final String username = usernameTV.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If email is valid and not in use
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    HashMap<String, String> data = new HashMap<>();
                                    data.put("email",email);
                                    collectionReference
                                            .document(username)
                                            .set(data)
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
                                                    findViewById(R.id.register_invalid_username).setVisibility(View.VISIBLE);
                                                }
                                            });
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    findViewById(R.id.register_invalid_email).setVisibility(View.VISIBLE);
                                }

                                // ...
                            }
                        });
            }
        });
    }
}
