package com.example.test.moodsoup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

/**
 * HomeFragment
 * V1.0
 *
 * Handles displaying the most recent Moods of users being followed
 * Also Home Screen when logged in
 *
 * @author smayer
 */
public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private ListView moodList;
    private ArrayList<String> followerArray;
    private ArrayList<Mood> moodArray;
    private ArrayList<Mood> filteredMood;
    private ArrayAdapter<Mood> moodAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        //Set adapter for moodList
        db = FirebaseFirestore.getInstance();
        moodList = root.findViewById(R.id.home_mood_list);
        moodArray = new ArrayList<>();
        filteredMood = new ArrayList<>();
        followerArray = new ArrayList<>();

        final String TAG = "GetFollower";
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            CollectionReference followerColRef = db.collection("Users").document(user.getEmail()).collection("following");
            followerColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            followerArray.add(document.getId());
                        }
                        getFollowerMood();
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        /*
         * Whenever filter is selected
         * - position 0 means no filter
         * - Rest will have its emotion attached to it
        */
        Spinner filter = root.findViewById(R.id.filter);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    moodAdapter = new MoodList(getActivity(), moodArray);
                    moodList.setAdapter(moodAdapter);
                } else {
                    filteredMood.clear();
                    setFilter(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showFloatingActionButton(); // show the FAB
        }

        moodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Navigation.findNavController(root).navigate(HomeFragmentDirections.actionNavHomeToNavMoodViewFragment(moodArray.get(i).getEmail(), moodArray.get(i).getDate(),moodArray.get(i).getTime()));
            }
        });

        return root;
    }

    /**
     * This Function will set a filter by setting a new array "filteredMood"
     * which will be used to display in screen
     * - Much faster method because we do not have to access firebase.
     * @param position
     * position indicates the position of the Mood in the filter Spinner
     *
     */
    private void setFilter(final int position) {
        final String[] emotion = getResources().getStringArray(R.array.emotionFilter);
        for (int i = 0; i < moodArray.size(); ++i) {
            if (moodArray.get(i).getEmotion().equals(emotion[position])) {
                filteredMood.add(moodArray.get(i));
            }
        }
        moodAdapter = new MoodList(getActivity(), filteredMood);
        moodList.setAdapter(moodAdapter);

    }

    /**
    * when view is initialized, a "followerArray" will be created to track all the users I'm following.
    * After, with the follower's now we will add the moods created by followers into "moodArray"
    * which will be shown in listView
    */
    private void getFollowerMood() {
        for (int i = 0; i < followerArray.size(); ++i) {
            CollectionReference followerMoodColRef = db.collection("Users").document(followerArray.get(i)).collection("moodHistory");
            followerMoodColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            System.out.println(document.get("date").toString());
                            Mood mood = new Mood(document.get("email").toString(),
                                    document.get("username").toString(), document.get("date").toString(),
                                    document.get("time").toString(), document.get("emotion").toString(),
                                    document.get("reason").toString(), document.get("social").toString(),
                                    document.get("location").toString(), (GeoPoint)document.get("coords"),(boolean) document.get("imgIncluded"));
                            moodArray.add(mood);
                            Collections.sort(moodArray, new StringDateComparator());
                            moodAdapter = new MoodList(getActivity(), moodArray);
                            moodList.setAdapter(moodAdapter);
                        }
                    }
                }
            });
        }
    }
}