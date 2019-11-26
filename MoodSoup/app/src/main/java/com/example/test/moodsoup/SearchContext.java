package com.example.test.moodsoup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchContext extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> emails;

    public SearchContext(@NonNull Context context, ArrayList<String> emails) {
        super(context, 0, emails);
        this.context = context;
        this.emails = emails;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.activity_search_context, parent, false);
        }

        final String email = emails.get(position);
        final TextView emailText = view.findViewById(R.id.email);
        final ImageButton addUser = view.findViewById(R.id.addUser);

        emailText.setText(email);

        final View finalView = view;
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String TAG = "FollowRequest";
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = mAuth.getCurrentUser();
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
                                            requestData.put("request", email);
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

        });
        return view;
    }
}
