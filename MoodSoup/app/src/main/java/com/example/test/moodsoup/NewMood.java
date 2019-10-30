package com.example.test.moodsoup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewMood extends AppCompatActivity{
    private EditText date;
    private EditText time;
    private Spinner emotion;
    private EditText reason;
    private EditText social;
    private EditText location;
    String TAG = "Sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmood);

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        emotion = findViewById(R.id.emotion);
        reason = findViewById(R.id.reason);
        social = findViewById(R.id.social);
        location = findViewById(R.id.location);

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
                date.setText("");
                time.setText("");
                reason.setText("");
                social.setText("");
                location.setText("");
                finish();
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
                    Mood mood = new Mood(dateText,timeText,emotionText,reasonText,socialText,locationText);
                    collectionReference
                            .document(email)
                            .collection("moodHistory")
                            .document(dateText + " - " + timeText)
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
                    finish();
                   }
            }
        });

    }
}
