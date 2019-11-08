package com.example.test.moodsoup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test.moodsoup.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MoodList extends ArrayAdapter<Mood>{
    private ArrayList<Mood> moods;
    private Context context;

    public MoodList(@NonNull Context context, ArrayList<Mood> moods) {
        super(context, 0, moods);
        this.moods = moods;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.context,parent,false);
        }

        final Mood mood = moods.get(position);

        final TextView name = view.findViewById(R.id.name);
        final TextView info = view.findViewById(R.id.userInfo);
        TextView emotion = view.findViewById(R.id.feeling);
        TextView reason = view.findViewById(R.id.new_mood_reason);
        TextView social = view.findViewById(R.id.new_mood_social);
        TextView location = view.findViewById(R.id.new_mood_location);
        ImageView image = view.findViewById(R.id.image);


        info.setText(String.format("@%s - %s - %s", mood.getUsername(), mood.getDate(), mood.getTime()));
        emotion.setText(mood.getEmotion());
        if (mood.getEmotion().toUpperCase().equals("HAPPY")) {
            image.setImageResource(R.drawable.moodsoup_happy);
        } else {
            image.setImageResource(R.drawable.moodsoup_sad);
        }

        reason.setText(mood.getReason());
        social.setText(mood.getSocial());
        location.setText(mood.getLocation());
        name.setText(mood.getEmail());
        return view;
    }

    public ArrayList<Mood> getMoods() {
        return moods;
    }

    public void setMoods(ArrayList<Mood> moods) {
        this.moods = moods;
    }

    @NonNull
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
