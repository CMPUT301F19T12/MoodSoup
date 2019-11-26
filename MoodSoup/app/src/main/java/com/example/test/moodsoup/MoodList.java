package com.example.test.moodsoup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * @author Sanae Mayer <smayer@ualberta.ca>
 * @author Peter Spiers <pspiers@ualberta.ca>
 * This class will handle the arrayAdapter of the mainpage/profile
 * where it sets how listview is displayed
 */
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
        final ImageView photo = view.findViewById(R.id.photo);

        info.setText(String.format("@%s - %s - %s", mood.getUsername(), mood.getDate(), mood.getTime()));
        emotion.setText(mood.getEmotion());
        if (mood.getEmotion().toUpperCase().equals("HAPPY")) {
            image.setImageResource(R.drawable.moodsoup_happy);
        } else {
            image.setImageResource(R.drawable.moodsoup_sad);
        }

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = firebaseStorage.getReference();
        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child(mood.getEmail() + "/" + mood.getDate() + " " + mood.getTime() + ".jpg");
        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/" + mood.getEmail() + "/" + mood.getDate() + " " + mood.getTime() + ".jpg");

        // While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false

        mountainsRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                System.out.println("got the image");
                photo.setVisibility(View.VISIBLE);

                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                photo.setImageBitmap(Bitmap.createScaledBitmap(bmp, photo.getWidth(),
                        photo.getHeight(), false));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Did not get the image");
                photo.setVisibility(View.GONE);
            }
        });

        reason.setText(mood.getReason());
        social.setText(mood.getSocial());
        location.setText(mood.getLocation());
        name.setText(mood.getEmail());

        // Getting Profile for other users.
        // New ProfileOther Class

        name.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent change = new Intent(view.getContext(), ProfileOther.class);
                change.putExtra("usernameOther",name.toString());

                context.startActivity(change);



            }
        });

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
