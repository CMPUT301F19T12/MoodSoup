package com.example.test.moodsoup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * RequestContext
 * V1.1
 * 2019-11-07
 *
 * This is meant to allow the user to accept or deny a follow request. An accepted request will add
 * the other user to the current user's follows and the current user to the other user's following.
 * In both the cases of accepting or rejecting, the requesting user will be removed from the
 * current user's requests and the current user will be removed from the other user's pending.
 * The other user will not be notified.
 *
 * @author pspiers
 * @author smayer
 */

public class RequestContext extends ArrayAdapter<String> {
    private ArrayList<String> emails;
    private Context context;
    private RequestSheetListener mListener;

    RequestContext(@NonNull Context context, ArrayList<String> email,RequestSheetListener listener) {
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
            view = LayoutInflater.from(context).inflate(R.layout.activity_request_context, parent, false);
        }

        final String email = emails.get(position);
        final TextView emailView = view.findViewById(R.id.email);
        final ImageButton accept = view.findViewById(R.id.accept);
        final ImageButton reject = view.findViewById(R.id.reject);
        final String TAG = "Delete";
        emailView.setText(email);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    //Add user to my follower
                    HashMap<String,String> follower = new HashMap<>();
                    follower.put("follower",email);
                    db.collection("Users").document(user.getEmail()).collection("follower").document(email)
                            .set(follower)
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

                    //Add me to user's follower
                    HashMap<String,String> following = new HashMap<>();
                    following.put("following",user.getEmail());
                    db.collection("Users").document(email).collection("following").document(user.getEmail())
                            .set(following)
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

                    mListener.onButtonClicked(email, -1);
                }

                //Remove user from my request list
                db.collection("Users").document(user.getEmail()).collection("request").document(email)
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

                //Remove me from user's pending list
                db.collection("Users").document(email).collection("pending").document(user.getEmail())
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
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    //Remove user from my pending list
                    db.collection("Users").document(user.getEmail()).collection("request").document(email)
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
                    db.collection("Users").document(email).collection("pending").document(user.getEmail())
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

    /**
     * Interface that will be used in MainActivity
     */
    public interface RequestSheetListener {
        void onButtonClicked(String state, final int position);
    }
}
