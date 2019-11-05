package com.example.test.moodsoup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class PendingContext extends ArrayAdapter<String> {
    private ArrayList<String> emails;
    private Context context;
    private SheetListener mListener;

    PendingContext(@NonNull Context context, ArrayList<String> email, SheetListener listener) {
        super(context, 0, email);
        this.emails = email;
        this.context = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.activity_pending_context, parent, false);
        }

        final String email = emails.get(position);
        final TextView emailView = view.findViewById(R.id.email);
        final ImageButton delete = view.findViewById(R.id.delete);
        final String TAG = "Delete";
        emailView.setText(email);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                final FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    //Remove user from my pending list
                    db.collection("Users").document(user.getEmail()).collection("pending").document(email)
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

                    //Remove me from user's request list
                    db.collection("Users").document(email).collection("request").document(user.getEmail())
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

                    mListener.onButtonClicked("delete", position);
                }
            }
        });
        return view;
    }

    //Interface that will be used in MainActivity
    public interface SheetListener {
        void onButtonClicked(String state, final int position);
    }
}
