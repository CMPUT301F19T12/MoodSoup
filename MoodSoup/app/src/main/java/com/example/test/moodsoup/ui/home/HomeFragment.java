package com.example.test.moodsoup.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.test.moodsoup.Mood;
import com.example.test.moodsoup.MoodList;
import com.example.test.moodsoup.R;
import com.example.test.moodsoup.StringDateComparator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FirebaseFirestore db;
    private ListView moodList;
    private ArrayList<String> followerArray;
    private ArrayList<Mood> moodArray;
    private ArrayAdapter<Mood> moodAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //Set adapter for moodList
        db = FirebaseFirestore.getInstance();
        moodList = root.findViewById(R.id.home_mood_list);
        moodArray = new ArrayList<>();
        followerArray = new ArrayList<>();

        //moodList.setAdapter(moodAdapter);
        final String TAG = "GetFollower";

        /*WHAT SHOULD HAPPEN HERE:
         * get the most recent post from each of the users you are following
         * add the most recent moods to the arrayadapter*/

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            CollectionReference followerColRef = db.collection("Users").document(user.getEmail()).collection("follower");
            followerColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            followerArray.add(document.getId());
                            System.out.println("Size: " + followerArray.size());
                        }
                        setArrayAdapter();
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }
        return root;
    }

    public void setArrayAdapter(){
        System.out.println("size " + followerArray.size());
        for (int i = 0; i < followerArray.size(); ++i) {
            CollectionReference followerMoodColRef = db.collection("Users").document(followerArray.get(i)).collection("moodHistory");
            final int finalI = i;
            followerMoodColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Mood mood = new Mood(followerArray.get(finalI),document.get("date").toString(),document.get("time").toString(),document.get("emotion").toString(),document.get("reason").toString(),document.get("social").toString(),document.get("location").toString());
                            moodArray.add(mood);
                            System.out.println(document.getData());
                        }
                        System.out.println(followerArray.get(finalI));
                        moodAdapter = new MoodList(getActivity(),moodArray);
                        Collections.sort(moodArray,new StringDateComparator());
                        moodList.setAdapter(moodAdapter);
                    }
                }
            });
        }
    }

}