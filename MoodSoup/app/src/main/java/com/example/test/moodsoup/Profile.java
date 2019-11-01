package com.example.test.moodsoup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import io.opencensus.metrics.export.Summary;

public class Profile extends AppCompatActivity {
    private ListView moodList;
    private ArrayList<String> moods = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final ImageButton toFollowing = findViewById(R.id.imageButton);
        toFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FollowingIntent = new Intent(getApplicationContext(), Following.class);
                startActivity(FollowingIntent);
            }
        });

        db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                    moods.add(snapshot.getString("date"));
                    moods.add(snapshot.getString("emotions"));
                    moods.add(snapshot.getString("location"));
                    moods.add(snapshot.getString("reason"));
                    moods.add(snapshot.getString("social"));
                    moods.add(snapshot.getString("time"));
                }
            }
        });
    }



}
