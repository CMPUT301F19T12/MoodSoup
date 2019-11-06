package com.example.test.moodsoup;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

public class ProfileFragment extends Fragment {
    private ArrayList<String> moods = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;
    private AppBarConfiguration mAppBarConfiguration;

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

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.profile,container,false);
        ///Set profile picture
        final ImageButton button = (ImageButton)root.findViewById(R.id.imageButton2);

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
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        String email = user.getEmail();


        // Set user name on profile layout display view
        final TextView display_name =  profileName;
        display_name.setText("User email: "+email);


        //User moods stuff /////////////
        CollectionReference moodRef = db.collection("Users").document(email).collection("moodHistory");
        Log.d("MOODREF",moodRef.toString());



        ListView moodListview = root.findViewById(R.id.event_list_self);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference moodref = ref.child("moodHistory");


        ///////////

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.child("moodHistory").getValue(String.class);
                    list.add(name);
                    Log.d("TAG", name);
                }


                Log.d("TAG", list.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        moodref.addListenerForSingleValueEvent(eventListener);


        // Error?

        /*public void LoadHistory(ListView moodList)) {
            moodRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>(){
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                String date = documentSnapshot.getString(KEY_DATE);
                                String emotion = documentSnapshot.getString(KEY_EMOTION);
                                String location = documentSnapshot.getString(KEY_LOCATION);
                                String reason = documentSnapshot.getString(KEY_REASON);
                                String social = documentSnapshot.getString(KEY_SOCIAL);
                                String time = documentSnapshot.getString(KEY_TIME);
                            } else {
                                Toast.makeText(MainActivity.this, "Mood Event does not exist");
                            }
                        }
                    });
        }

        /*db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                    moods.add(snapshot.getString("date"));
                    moods.add(snapshot.getString("emotions"));
                    moods.add(snapshot.getString("location"));
                    moods.add(snapshot.getString("reason"));
                    moods.add(snapshot.getString("social"));
                    moods.add(snapshot.getString("time"));
                }
            }
        });
         */
        return root;

    }
}
