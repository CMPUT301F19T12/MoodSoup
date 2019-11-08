package com.example.test.moodsoup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import androidx.recyclerview.widget.RecyclerView;

/*
* SearchFragmant
* V1.1
* 2019-11-07
*
* This is meant to allow the user to search the database for another user and send them a follow
* request. Doing this adds a follow request to the target user's request list and a pending request
* to the current user's pending list.
*
* @author pspiers
 *@author smayer
 */
public class SearchFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View root = inflater.inflate(R.layout.follow_search, container, false);
        final EditText UserName = (EditText) root.findViewById(R.id.Search_User);
        final Button search = root.findViewById(R.id.search_button);
        final String TAG = "Sample";
        final FirebaseFirestore db;
        //Not currently used. It's here as a reminder of what needs to be done.
        RecyclerView searchList = root.findViewById(R.id.Search_list);


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
                                        /*
                                        * There exists a user with the given name in the database
                                        * So they need to be added to pending and the current user
                                        * added to their request
                                         */
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
                                    } else { //Did not find target, tell user search failed
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
