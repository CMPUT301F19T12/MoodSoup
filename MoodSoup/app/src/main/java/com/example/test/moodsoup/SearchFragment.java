package com.example.test.moodsoup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * SearchFragmant
 * V1.1
 * 2019-11-07
 *
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
        final CheckBox username = root.findViewById(R.id.usernameCheck);
        final CheckBox email = root.findViewById(R.id.emailCheck);

        final String TAG = "Sample";
        final FirebaseFirestore db;
        final ListView emailList = root.findViewById(R.id.search_result);
        final ArrayList<String> emailArray = new ArrayList<>();
        final ArrayList<String> usernameArray = new ArrayList<>();
        username.setChecked(true);

        //This only allows one of "username" or "email" to be selected
        username.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    email.setChecked(false);
                else {
                    //cannot uncheck both
                    if (!email.isChecked())
                        username.setChecked(true);
                }
            }
        });

        //This only allows one of "username" or "email" to be selected
        email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    username.setChecked(false);
                else {
                    //cannot uncheck both
                    if (!username.isChecked())
                        email.setChecked(true);
                }
            }
        });


        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideFloatingActionButton(); // hide the FAB
        }

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String toSearch = UserName.getText().toString();
                if (user != null) {
                    //Since we do not allow partial search, a search bar cannot be empty
                    if (toSearch.isEmpty()) {
                        Toast.makeText(getActivity(), "Please Enter a valid username",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        //clear array for new search
                        usernameArray.clear();
                        emailArray.clear();

                        //Search by username
                        if (username.isChecked()) {
                            db.collection("Users").whereEqualTo("username", toSearch).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.exists()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        emailArray.add(document.getId());
                                                        usernameArray.add(document.getData().get("username").toString());
                                                    }
                                                }
                                                ArrayAdapter<String> emailAdapter = new SearchContext(getContext(), emailArray, usernameArray);
                                                emailList.setAdapter(emailAdapter);
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "no data found");
                                }
                            });
                        }
                        //Search by email
                        else if (email.isChecked()) {
                            db.collection("Users").document(toSearch).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    emailArray.add(document.getId());
                                                    usernameArray.add(document.get("username").toString());
                                                }
                                                ArrayAdapter<String> emailAdapter = new SearchContext(getContext(), emailArray, usernameArray);
                                                emailList.setAdapter(emailAdapter);
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "no data found");
                                    Toast.makeText(getActivity(), "Failed to find user",
                                            Toast.LENGTH_SHORT).show();
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