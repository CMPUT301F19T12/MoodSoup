package com.example.test.moodsoup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.follow_search, container, false);
        final EditText UserName = (EditText) root.findViewById(R.id.Search_User);
        final Button search = root.findViewById(R.id.search_button);
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
                                        Toast.makeText(getActivity(), "User Found",
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

                                        Intent FollowingIntent = new Intent(getActivity(), Following.class);
                                        startActivity(FollowingIntent);
                                        getActivity().onBackPressed();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to find user",
                                                Toast.LENGTH_SHORT).show();
                                        Intent FollowingIntent = new Intent(getActivity(), Following.class);
                                        startActivity(FollowingIntent);
                                        getActivity().onBackPressed();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Error: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
        return root;
    }
}