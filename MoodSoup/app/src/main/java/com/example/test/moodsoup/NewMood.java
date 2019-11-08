package com.example.test.moodsoup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

/**
 * Handles creating a new mood
 * @author Sanae Mayer
 * @author Richard Qin
 */

public class NewMood extends AppCompatActivity{
    private TextView date;
    private Spinner emotion;
    private EditText reason;
    private Spinner social;
    private EditText location;
    String TAG = "Sample";
    String email;
    String emotionText,reasonText,socialText,locationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmood);

        date = findViewById(R.id.new_mood_datetime);
        emotion = findViewById(R.id.new_mood_emotion);
        reason = findViewById(R.id.new_mood_reason);
        social = findViewById(R.id.new_mood_social);
        location = findViewById(R.id.new_mood_location);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(NewMood.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.emotion));
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        emotion.setAdapter(adapter);

        ArrayAdapter<String> socialSituationAdapter = new ArrayAdapter<>(NewMood.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.social_situation));
        socialSituationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        social.setAdapter(socialSituationAdapter);

        // Get current date and time
        Calendar calendar = Calendar.getInstance();
        final String currentDate = getDateString(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        final String currentTime = getTimeString(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        final String uploadTime = currentDate+ ' ' + currentTime; // Stored as YYYY-MM-DD HH:MM
        date.setText(uploadTime);

        ImageButton cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton post = findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emotionText = emotion.getSelectedItem().toString();
                reasonText = reason.getText().toString();
                socialText = social.getSelectedItem().toString();
                locationText = location.getText().toString();
                findViewById(R.id.new_mood_error_emotion).setVisibility(View.INVISIBLE);
                if (socialText.equals("Choose a social situation:")) {
                    socialText = "";
                }
                if (emotionText.equals("Choose an emotion:")) {
                    findViewById(R.id.new_mood_error_emotion).setVisibility(View.VISIBLE);
                    Toast.makeText(NewMood.this, "Please Fill out the Required Fields",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //IMPLEMENT USER CLASS
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    email = mAuth.getCurrentUser().getEmail();

                    DocumentReference usernameRef = db.collection("Users").document(email);
                    usernameRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    createNewMood((String) document.getData().get("username"));
                                }
                            }
                        }
                    });
                }
            }
            public void createNewMood(String userName){
                Mood mood = new Mood(email,userName,currentDate, currentTime,emotionText,reasonText,socialText,locationText);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collectionReference = db.collection("Users");
                collectionReference
                            .document(email)
                            .collection("moodHistory")
                            .document(uploadTime)
                            .set(mood)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"Data Addition Successful");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"Data Addition Failed" + e.toString());
                                }
                            });
                    Intent intent = new Intent(NewMood.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
        });

    }

    /**
     * formats a date
     * @param year
     * integer representing year
     * @param month
     * integer representing month
     * @param dayOfMonth
     * integer representing the current day
     * @return
     * Returns a string with a date format of yyyy-mm-dd
     */
    public String getDateString(int year, int month, int dayOfMonth) {
        month += 1;
        String monthTxt = Integer.toString(month);
        String dayTxt = Integer.toString(dayOfMonth);
        if (month<10){
            monthTxt = '0'+ monthTxt;
        }
        if (dayOfMonth<10){
            dayTxt = '0' + dayTxt;
        }
        return year + "-" + monthTxt + "-" + dayTxt;
    }

    /**
     * Formats a time
     * @param hourOfDay
     * integer representing the current hour of day
     * @param minute
     * integer representing the current minute of the hour
     * @return
     * Returns a string with time format hh:mm
     */
    public String getTimeString( int hourOfDay, int minute) {
        String temptime;
        String hour;
        if (hourOfDay<10){
            hour = "0"+hourOfDay;
        }
        else {
            hour = Integer.toString(hourOfDay);
        }
        if (minute<10){
            temptime = hour + ":0" + minute;
        }
        else {
            temptime = hour + ":" + minute;
        }
        return temptime;
    }

    /**
     * Navigates back to previous location on back button press
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}
