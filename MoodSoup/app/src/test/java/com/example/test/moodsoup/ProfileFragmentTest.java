package com.example.test.moodsoup;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.junit.*;
import static org.junit.Assert.*;

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

import java.util.HashMap;

import androidx.annotation.NonNull;

/**
 * @author Darian Chen
 * @author Richard Qin
 */

class ProfileFragmentTest {
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String email = "test@test.com";
    String password = "test";

    // Initialize Firebase
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");
        mAuth.createUserWithEmailAndPassword(email, password);
        FirebaseUser user = mAuth.getCurrentUser();

    }
}