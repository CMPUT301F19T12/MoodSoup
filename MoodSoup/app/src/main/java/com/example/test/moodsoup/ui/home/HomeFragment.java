package com.example.test.moodsoup.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.test.moodsoup.Mood;
import com.example.test.moodsoup.MoodList;
import com.example.test.moodsoup.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    FirebaseFirestore db;
    private ListView moodList;
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
        moodAdapter = new MoodList(getActivity(),moodArray);
        moodList.setAdapter(moodAdapter);

        /*WHAT SHOULD HAPPEN HERE:
        * get the most recent post from each of the users you are following
        * add the most recent moods to the arrayadapter*/
        Mood happyMood = new Mood("2019-01-01","12:00","Happy","Happy Test","","");
        moodAdapter.add(happyMood);
        Mood sadMood = new Mood("2019-01-02","13:30","Sad","Sad Test","","");
        moodAdapter.add(sadMood);
        return root;
    }
}