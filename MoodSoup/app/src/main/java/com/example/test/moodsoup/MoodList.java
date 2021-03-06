package com.example.test.moodsoup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * MoodList
 * V1.2
 *
 * This class will handle the arrayAdapter of the mainpage/profile
 * where it sets how listview is displayed
 *
 * @author smayer
 * @author pspiers
 * @author dchen
 */
public class MoodList extends ArrayAdapter<Mood> {
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
        TextView reasonLabel = view.findViewById(R.id.reason_label);
        TextView reason = view.findViewById(R.id.new_mood_reason);
        TextView socialLabel = view.findViewById(R.id.social_label);
        TextView social = view.findViewById(R.id.new_mood_social);
        TextView locationLabel = view.findViewById(R.id.location_label);
        TextView location = view.findViewById(R.id.new_mood_location);
        ImageView image = view.findViewById(R.id.image);

        info.setText(String.format("@%s - %s - %s", mood.getUsername(), mood.getDate(), mood.getTime()));
        emotion.setText(mood.getEmotion());
        if (mood.getEmotion().toUpperCase().equals("HAPPY")) {
            image.setImageResource(R.drawable.moodsoup_happy);
        } else if (mood.getEmotion().toUpperCase().equals("SAD")){
            image.setImageResource(R.drawable.moodsoup_sad);
        } else if (mood.getEmotion().toUpperCase().equals("EXCITED")){
            image.setImageResource(R.drawable.moodsoup_excited);
        } else if (mood.getEmotion().toUpperCase().equals("ANGRY")){
            image.setImageResource(R.drawable.moodsoup_angry);
        } else if (mood.getEmotion().toUpperCase().equals("BORED")){
            image.setImageResource(R.drawable.moodsoup_bored);
        } else if (mood.getEmotion().toUpperCase().equals("TIRED")){
            image.setImageResource(R.drawable.moodsoup_tired);
        } else if (mood.getEmotion().toUpperCase().equals("SCARED")){
            image.setImageResource(R.drawable.moodsoup_scared);
        } else if (mood.getEmotion().toUpperCase().equals("SURPRISED")) {
            image.setImageResource(R.drawable.moodsoup_surprised);
        }

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = firebaseStorage.getReference();
        // Create a reference to "mountains.jpg"
        StorageReference imageRef = storageRef.child(mood.getEmail() + "/" + mood.getDate() + " " + mood.getTime() + ".jpg");
        // Create a reference to 'images/mountains.jpg'
        StorageReference imageReference = storageRef.child("images/" + mood.getEmail() + "/" + mood.getDate() + " " + mood.getTime() + ".jpg");

        // While the file names are the same, the references point to different files
        imageRef.getName().equals(imageReference.getName());    // true
        imageRef.getPath().equals(imageReference.getPath());    // false

        if (mood.isImgIncluded()) {
            view.findViewById(R.id.image_included).setVisibility(View.VISIBLE);
        } else{
            view.findViewById(R.id.image_included).setVisibility(View.GONE);
        }

        if (!mood.getReason().equals("")) {
            reasonLabel.setVisibility(View.VISIBLE);
            reason.setVisibility(View.VISIBLE);
            reason.setText(mood.getReason());
        } else{
            reasonLabel.setVisibility(View.GONE);
            reason.setVisibility(View.GONE);
        }
        if(!mood.getSocial().equals("")) {
            socialLabel.setVisibility(View.VISIBLE);
            social.setVisibility(View.VISIBLE);
            social.setText(mood.getSocial());
        }else{
            socialLabel.setVisibility(View.GONE);
            social.setVisibility(View.GONE);
        }
        if(!mood.getLocation().equals("")) {
            locationLabel.setVisibility(View.VISIBLE);
            location.setVisibility(View.VISIBLE);
            location.setText(mood.getLocation());
        }else{
            locationLabel.setVisibility(View.GONE);
            location.setVisibility(View.GONE);
        }
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
