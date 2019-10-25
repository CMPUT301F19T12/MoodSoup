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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    // Initialize Variables
    TextView emailTV;
    TextView usernameTV;
    TextView passwordTV;
    TextView firstNameTV;
    TextView lastNameTV;
    Button registerBTN;
    String TAG = "Sample";

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Assign Variables
        emailTV = findViewById(R.id.email_new_user_tv);
        passwordTV = findViewById(R.id.password_new_user_tv);
        registerBTN = findViewById(R.id.register_new_user_btn);
        usernameTV = findViewById(R.id.username_new_user_tv);
        firstNameTV = findViewById(R.id.firstname_new_user_tv);
        lastNameTV = findViewById(R.id.lastname_new_user_tv);
        final CollectionReference collectionReference = db.collection("Users");

        // Upon pressing the register button
        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get strings from TextView
                final String email = emailTV.getText().toString();
                final String password = passwordTV.getText().toString();
                final String username = usernameTV.getText().toString();
                final String firstname = firstNameTV.getText().toString();
                final String lastname = lastNameTV.getText().toString();
                HashMap<String, String> data = new HashMap<>();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If email is valid and not in use
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
                // Add new users to user database
                data.put("username",username);
                data.put("firstName",firstname);
                data.put("lastName",lastname);
                collectionReference
                        .document(email)
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
                            }
                        });
            }
        });
    }
}