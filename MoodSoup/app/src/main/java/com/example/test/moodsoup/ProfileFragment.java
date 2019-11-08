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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
    private ImageButton imageButton2;
    private static final String KEY_DATE = "Date";
    private static final String KEY_EMOTION = "Emotion";
    private static final String KEY_LOCATION = "Location";
    private static final String KEY_REASON = "Reason";
    private static final String KEY_SOCIAL = "Social";
    private static final String KEY_TIME = "Time";
    private static int RESULT_LOAD_IMG = 1;
    private ListView event;
    private ArrayList<String> event_list;
    private ArrayAdapter<String> event_listAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.profile,container,false);
        event = root.findViewById(R.id.event_list_self);
        ///Set profile picture
        final ImageButton button = (ImageButton)root.findViewById(R.id.imageButton2);


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
                Intent FollowingIntent = new Intent(getActivity(), Following.class);
                startActivity(FollowingIntent);
            }
        });

        String uid = user.getUid();
        String name = user.getDisplayName();
        final String email = user.getEmail();


        // Set user name on profile layout display view
        final TextView display_name =  profileName;
        display_name.setText("User email: "+email);



        final ListView event = root.findViewById(R.id.event_list_self);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        event_list = new ArrayList<>();
        final Context context = getActivity();
        final PendingContext.SheetListener listener = this;


        // Get the moodHistory for the user in profile --> Not finished yet since we are just pulling the dates, will fix later.

        CollectionReference colRef = db.collection("Users").document(email).collection("moodHistory");
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {


                    for (QueryDocumentSnapshot document : task.getResult())
                    {

                        event_list.add(document.getId());


                    }



                    event_listAdapter = new PendingContext(context , event_list, listener);
                    event.setAdapter(event_listAdapter);




                } else {
                    Log.d(TAG, "FAIL", task.getException());
                }
            }
        });




        //event_list.add(colRef.document().getFirestore().toString());



        return root;

    }

    @Override
    public void onButtonClicked(String state, int position) {
        event.setAdapter(event_listAdapter);
        if (state.equals("delete"))
        {
            event_list.remove(position);
            Toast.makeText(getActivity(),"Mood Deleted",
                    Toast.LENGTH_SHORT).show();
        }
    }
}