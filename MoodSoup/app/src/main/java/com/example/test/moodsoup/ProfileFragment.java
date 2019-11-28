package com.example.test.moodsoup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

public class ProfileFragment extends Fragment implements PendingContext.SheetListener{
    private FirebaseAuth mAuth;
    private String TAG = "ERROR HERE!";
    private ListView moodList;
    private TextView profileName;
    private ListView event;
    private ArrayList<Mood> event_list;
    private ArrayAdapter<Mood> event_listAdapter;
    private String emailFromBundle;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.profile,container,false);
        event = root.findViewById(R.id.event_list_self);
        ///Set profile picture
        ProfileFragmentArgs profileFragmentArgs = ProfileFragmentArgs.fromBundle(getArguments());
        emailFromBundle = profileFragmentArgs.getEmail();

        // View ID
        profileName = root.findViewById(R.id.ProfileName);
        moodList = root.findViewById(R.id.event_list_self);

        // User Instance

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //User info

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


        moodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Navigation.findNavController(root).navigate(ProfileFragmentDirections.actionNavProfileToNavMoodViewFragment(emailFromBundle,event_list.get(i).getDate()+' '+event_list.get(i).getTime()));
            }
        });


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