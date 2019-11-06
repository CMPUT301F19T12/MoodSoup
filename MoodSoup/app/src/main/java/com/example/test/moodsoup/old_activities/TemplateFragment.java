package com.example.test.moodsoup.old_activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.test.moodsoup.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class TemplateFragment extends Fragment {

    private String test;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_main, container, false);
        //Code here
        //Button search = root.findViewById(R.id.search_button);
        //this = getActivity()
        return root;
    }
}
