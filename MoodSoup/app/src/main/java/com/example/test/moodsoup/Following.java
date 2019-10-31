package com.example.test.moodsoup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Following extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.following);
        final Button search = findViewById(R.id.Search);

        final ArrayList<String> list = new ArrayList<>();
        final ListView pending = findViewById(R.id.pending);

        final FirebaseFirestore db;
        final String TAG = "Sample";
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final Context context = this;
        CollectionReference colRef = db.collection("Users").document(user.getEmail()).collection("pending");
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        list.add(document.getId());
                    }
                    ArrayAdapter<String> listAdapter = new pendingContext( context , list);
                    pending.setAdapter(listAdapter);

                    //QueryDocumentSnapshot document = task.getResult();
                    //if (document.exists()) {
                    //   Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    //    textViewToChange.setText(document.get("pending").toString());
                    //} else {
                    //    Log.d(TAG, "No such document");
                    //}
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        }) ;




        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SearchIntent = new Intent(getApplicationContext(), FollowSearch.class);
                startActivity(SearchIntent);
                finish();
            }
        });








    }



}
