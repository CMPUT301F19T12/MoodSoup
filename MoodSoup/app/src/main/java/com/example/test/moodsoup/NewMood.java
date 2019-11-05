package com.example.test.moodsoup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class NewMood extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private TextView date;
    private TextView time;
    private Spinner emotion;
    private EditText reason;
    private EditText social;
    private EditText location;
    String TAG = "Sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmood);

        date = findViewById(R.id.new_mood_date);
        time = findViewById(R.id.new_mood_time);
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

        ImageButton cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewMood.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });
        ImageButton post = findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateText = date.getText().toString();
                String timeText = time.getText().toString();
                String emotionText = emotion.getSelectedItem().toString();
                String reasonText = reason.getText().toString();
                String socialText = social.getText().toString();
                String locationText = location.getText().toString();

                if (dateText.isEmpty() || timeText.isEmpty() || emotionText.isEmpty()){
                    System.out.println(dateText + " " + timeText + " " + emotionText);
                    Toast.makeText(NewMood.this, "Please Fill out the Required Fields",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    //IMPLEMENT USER CLASS
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference collectionReference = db.collection("Users");
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String email = mAuth.getCurrentUser().getEmail();
                    Mood mood = new Mood(email,dateText,timeText,emotionText,reasonText,socialText,locationText);
                    collectionReference
                            .document(email)
                            .collection("moodHistory")
                            .document(dateText)
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
            }
        });

    }
    // Initializes Date Picker and sets default values to be current date
    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Modifies textView to match user input into date picker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month += 1;
        String monthTxt = Integer.toString(month);
        String dayTxt = Integer.toString(dayOfMonth);
        if (month<10){
            monthTxt = '0'+ monthTxt;
        }
        if (dayOfMonth<10){
            dayTxt = '0' + dayTxt;
        }
        String tempdate = year + "-" + monthTxt + "-" + dayTxt;
        date.setText(tempdate);
    }

    // Initializes time picker and sets default values to be current time
    private void showTimePickerDialog(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                Boolean.TRUE
        );
        timePickerDialog.show();
    }

    // Modifies TextView to match user input into time picker
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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
        time.setText(temptime);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NewMood.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
