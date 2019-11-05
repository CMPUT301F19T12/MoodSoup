package com.example.test.moodsoup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Follower extends AppCompatActivity implements RequestContext.RequestSheetListener {

    private ArrayList<String> pendingList;
    private ArrayList<String> followerList;
    private ListView request;
    private ListView follower;
    private ArrayAdapter<String> listAdapter;
    private ArrayAdapter followerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        pendingList = new ArrayList<>();
        followerList = new ArrayList<>();
        request = findViewById(R.id.requests);
        follower = findViewById(R.id.follower);

        final FirebaseFirestore db;
        final String TAG = "Sample";
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final Context context = this;
        final RequestContext.RequestSheetListener listener = this;
        CollectionReference pendingColRef = db.collection("Users").document(user.getEmail()).collection("request");
        pendingColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        pendingList.add(document.getId());
                    }
                    listAdapter = new RequestContext(context , pendingList, listener);
                    request.setAdapter(listAdapter);

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        CollectionReference followerColRef = db.collection("Users").document(user.getEmail()).collection("follower");
        followerColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        followerList.add(document.getId());
                    }
                    followerAdapter = new FollowerContext(context , followerList);
                    follower.setAdapter(followerAdapter);
                    registerForContextMenu(follower);
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        if (v.getId() == R.id.follower) {
            getMenuInflater().inflate(R.menu.follower_menu,menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.remove:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                String email = followerList.get(info.position);
                final String TAG = "Remove Follower";
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    //Remove user from my follower
                    db.collection("Users").document(user.getEmail()).collection("follower").document(email)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting document ", e);
                                }
                            });

                    //Remove me from user's following list
                    db.collection("Users").document(email).collection("following").document(user.getEmail())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting document ", e);
                                }
                            });
                }
                follower.setAdapter(followerAdapter);
                followerList.remove(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onButtonClicked(String state, int position) {
        request.setAdapter(listAdapter);
        follower.setAdapter(followerAdapter);
        if (state.equals("delete"))
        {
            pendingList.remove(position);
        }
        else
        {
            followerList.add(state);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Follower.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
