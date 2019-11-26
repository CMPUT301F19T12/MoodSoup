package com.example.test.moodsoup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * SearchFragmant
 * V1.1
 * 2019-11-07
 * <p>
 * This is meant to allow the user to search the database for another user and send them a follow
 * request. Doing this adds a follow request to the target user's request list and a pending request
 * to the current user's pending list.
 *
 * @author pspiers
 * @author smayer
 */
public class SearchFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.follow_search, container, false);
        final EditText UserName = (EditText) root.findViewById(R.id.Search_User);
        final Button search = root.findViewById(R.id.search_button);

        final String TAG = "Sample";
        final FirebaseFirestore db;

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        /*
         * These will check for the following:
         *   - Is the field empty
         *   - Are you trying to add yourslef
         *   - Are you already following a user
         * */
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String toSearch = UserName.getText().toString();
                if (user != null) {
                    //Cannot be empty
                    if (toSearch.isEmpty()) {
                        Toast.makeText(getActivity(), "Please Enter a valid username",
                                Toast.LENGTH_SHORT).show();
                    }
                    //Cannot add yourself
                    else if (toSearch.equals(user.getEmail())) {
                        Toast.makeText(getActivity(), "Cannot add yourself!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        final FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            final FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference pendingRef = db.collection("Users").document(user.getEmail()).collection("pending").document(toSearch);
                            pendingRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> pendingTask) {
                                    if (pendingTask.isSuccessful()) {
                                        //Already sent a request
                                        DocumentSnapshot pendingDoc = pendingTask.getResult();
                                        if (pendingDoc.exists()) {
                                            Toast.makeText(getActivity(), "User already exists in your pending",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            DocumentReference pendingRef = db.collection("Users")
                                                    .document(user.getEmail()).collection("following")
                                                    .document(toSearch);
                                            pendingRef.get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> followerTask) {
                                                            if (followerTask.isSuccessful()) {
                                                                //Already following
                                                                DocumentSnapshot followerDoc = followerTask.getResult();
                                                                if (followerDoc.exists()) {
                                                                    Toast.makeText(getActivity(), "User already exists in your following",
                                                                            Toast.LENGTH_SHORT).show();
                                                                } else {
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
                                                                                            pendingData.put("pending", toSearch);
                                                                                            db.collection("Users")
                                                                                                    .document(user.getEmail())
                                                                                                    .collection("pending")
                                                                                                    .document(toSearch)
                                                                                                    .set(pendingData)
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            Log.d(TAG, "Data Addition Successful");
                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            Log.d(TAG, "Data Addition Failed" + e.toString());
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
                                                                                                            Log.d(TAG, "Data Addition Successful");
                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            Log.d(TAG, "Data Addition Failed" + e.toString());
                                                                                                        }
                                                                                                    });
                                                                                            Navigation.findNavController(root).navigate(R.id.nav_following);
                                                                                        } else {
                                                                                            Toast.makeText(getActivity(), "Failed to find user",
                                                                                                    Toast.LENGTH_SHORT).show();
                                                                                            Navigation.findNavController(root).navigate(R.id.nav_following);
                                                                                        }
                                                                                    } else {
                                                                                        Toast.makeText(getActivity(), "Error: " + task.getException().getMessage(),
                                                                                                Toast.LENGTH_SHORT).show();
                                                                                        Navigation.findNavController(root).navigate(R.id.nav_following);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        return root;
    }
}