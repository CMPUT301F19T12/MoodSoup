package com.example.test.moodsoup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

public class ProfileFragment extends Fragment implements PendingContext.SheetListener{
    private ArrayList<String> moods = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;
    private AppBarConfiguration mAppBarConfiguration;
    private String TAG = "ERROR HERE!";
    private ListView moodList;
    private TextView profileName;
    private ImageButton toFollowing;
    private ImageButton ProfileImage;
    private static int RESULT_LOAD_IMG = 1;
    private ListView event;
    private ArrayList<Mood> event_list;
    private ArrayAdapter<Mood> event_listAdapter;
    private boolean isFollowing;

    private String test;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.profile,container,false);
        event = root.findViewById(R.id.event_list_self);
        ///Set profile picture
        final ImageButton button = (ImageButton)root.findViewById(R.id.ProfileImage);
        isFollowing = false;
        ProfileFragmentArgs profileFragmentArgs = ProfileFragmentArgs.fromBundle(getArguments());
        String emailFromBundle = profileFragmentArgs.getEmail();
        // Set a user profile image (For self only!) -- > Needs testing

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setSelected(!button.isPressed());

                if (button.isPressed()) {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);


                    button.setImageResource(R.drawable.moodsoup_happy);
                }
                else {
                    button.setImageResource(R.drawable.moodsoup_sad);
                }
            }
        });

        // View ID
        profileName = root.findViewById(R.id.ProfileName);
        moodList = root.findViewById(R.id.event_list_self);
        toFollowing = root.findViewById(R.id.imageButton);

        // User Instance

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        //User info
        toFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FollowingIntent = new Intent(getActivity(), Followers.class);
                startActivity(FollowingIntent);
            }
        });

        if (emailFromBundle.equals("No Email")){
            emailFromBundle = mAuth.getCurrentUser().getEmail();
        }

        if (emailFromBundle.equals(mAuth.getCurrentUser().getEmail())){
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showFloatingActionButton(); // hide the FAB
            }
        } else {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).hideFloatingActionButton(); // hide the FAB
            }
        }

        String uid = user.getUid();
        String name = user.getDisplayName();
        final String email = emailFromBundle;


        // Set user name on profile layout display view
        final TextView display_name =  profileName;
        display_name.setText(email);


        final ListView event = root.findViewById(R.id.event_list_self);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        event_list = new ArrayList<>();
        final Context context = getActivity();
        final PendingContext.SheetListener listener = this;

        // Get the moodHistory for the user in profile --> Not finished yet since we are just pulling the dates, will fix later.

        CollectionReference colRef = db.collection("Users").document(email).collection("moodHistory");
        /*CollectionReference followRef = db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("following");
        followRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (email.equals(document.get("email"))){
                            isFollowing = true;
                        }
                    }
                }
            }
        });*/

        isFollowing = true;
        if (emailFromBundle.equals(mAuth.getCurrentUser().getEmail()) || isFollowing) {
            colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Mood mood = new Mood(document.get("email").toString(), document.get("username").toString(), document.get("date").toString(), document.get("time").toString(), document.get("emotion").toString(), document.get("reason").toString(), document.get("social").toString(), document.get("location").toString(), (GeoPoint) document.get("coords"));
                            event_list.add(mood);
                        }

                    } else {
                        Log.d(TAG, "FAIL", task.getException());
                    }
                    Collections.sort(event_list, new StringDateComparator());
                    event_listAdapter = new MoodList(context, event_list);
                    moodList.setAdapter(event_listAdapter);
                }
            });
        }



        return root;

    }
    @Override
    public void onButtonClicked(String state, int position) {
        event.setAdapter(event_listAdapter);
        if (state.equals("delete"))
        {
            String uploadTime = event_list.get(position).getDate() +" "+event_list.get(position).getTime();
            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("moodHistory").document().delete();
            event_list.remove(position);
            Toast.makeText(getActivity(),"Mood Deleted",
                    Toast.LENGTH_SHORT).show();
        }
    }
}