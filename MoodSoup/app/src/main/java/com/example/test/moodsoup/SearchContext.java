package com.example.test.moodsoup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
/**
 * SearchFragmant
 * V1.1
 * 2019-11-07
 *
 * This is meant to notify the user about the other users that they have searched.
 * You can either send or cancel follow request, or view if you are already following.
 *
 * @author pspiers
 * @author smayer
 */
public class SearchContext extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> emails;
    private ArrayList<String> usernames;

    public SearchContext(@NonNull Context context, ArrayList<String> emails, ArrayList<String> usernames) {
        super(context, 0, emails);
        this.context = context;
        this.emails = emails;
        this.usernames = usernames;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.activity_search_context, parent, false);
        }

        final String email = emails.get(position);
        final String username = usernames.get(position);
        final TextView emailText = view.findViewById(R.id.email);
        final TextView usernameText = view.findViewById(R.id.username);
        final Button addUser = view.findViewById(R.id.addUser);

        emailText.setText(email);
        usernameText.setText(username);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //If you search for yourself
            if (email.equals(user.getEmail())) {
                addUser.setText("Yourself");
                addUser.setEnabled(false);
            }

            DocumentReference pendingRef = db.collection("Users").document(user.getEmail()).collection("pending").document(email);
            pendingRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> pendingTask) {
                    if (pendingTask.isSuccessful()) {
                        //Already sent a request therefore you should be able to cancel it
                        DocumentSnapshot pendingDoc = pendingTask.getResult();
                        if (pendingDoc.exists()) {
                            addUser.setText("Cancel Request");
                            addUser.setEnabled(true);
                        }
                    }
                }
            });

            DocumentReference followingRef = db.collection("Users").document(user.getEmail()).collection("following").document(email);
            followingRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> followingTask) {
                    if (followingTask.isSuccessful()) {
                        //If you are already following a user, button should not be clickable and notify the user that user is already following
                        DocumentSnapshot followingDoc = followingTask.getResult();
                        if (followingDoc.exists()) {
                            addUser.setText("Following");
                            addUser.setEnabled(false);
                        }
                    }
                }
            });

            final View finalView = view;
            addUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //When cancel request is pressed
                    if (addUser.getText() == "Cancel Request") {
                        //Change the text to "send request"
                        addUser.setText("Send Request");
                        addUser.setEnabled(true);
                        if (user != null) {
                            //Remove user from my pending
                            db.collection("Users")
                                    .document(user.getEmail()).collection("pending").document(email)
                                    .delete();
                            //Remove me from user's request
                            db.collection("Users")
                                    .document(email).collection("request").document(user.getEmail())
                                    .delete();
                        }
                    }
                    //When follow is requested
                    else {
                        //Change the text to "cancel request"
                        addUser.setText("Cancel Request");
                        addUser.setEnabled(true);
                        final String TAG = "Search";
                        if (user != null) {
                            db.collection("Users")
                                    .document(email)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult().exists()) {
                                                    Toast.makeText(finalView.getContext(), "User Found",
                                                            Toast.LENGTH_SHORT).show();
                                                    //Add searched user to my pending
                                                    HashMap<String, String> pendingData = new HashMap<>();
                                                    pendingData.put("pending", email);
                                                    db.collection("Users")
                                                            .document(user.getEmail())
                                                            .collection("pending")
                                                            .document(email)
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
                                                            .document(email)
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
                                                } else {
                                                    Toast.makeText(finalView.getContext(), "Failed to find user",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(finalView.getContext(), "Error: " + task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }

            });
        }
        return view;
    }
}
