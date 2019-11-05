package com.example.test.moodsoup;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import org.w3c.dom.Text;

import java.util.ArrayList;

import io.opencensus.metrics.export.Summary;

public class Profile extends AppCompatActivity {
    private ArrayList<String> moods = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;
    private AppBarConfiguration mAppBarConfiguration;

    private ListView moodList;
    private TextView profileName;
    private ImageButton toFollowing;

    private static final String KEY_DATE = "Date";
    private static final String KEY_EMOTION = "Emotion";
    private static final String KEY_LOCATION = "Location";
    private static final String KEY_REASON = "Reason";
    private static final String KEY_SOCIAL = "Social";
    private static final String KEY_TIME = "Time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // View ID
        profileName = findViewById(R.id.ProfileName);
        moodList = findViewById(R.id.event_list_self);
        toFollowing = findViewById(R.id.imageButton);

        // User Instance
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        //User info


        String uid = user.getUid();
        String name = user.getDisplayName();
        String email = user.getEmail();


        // Set user name on profile layout display view
        TextView display_name =  profileName;
        display_name.setText("User email: "+email);


        //User moods



        toFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FollowingIntent = new Intent(getApplicationContext(), Following.class);
                startActivity(FollowingIntent);
            }
        });

        CollectionReference moodRef = db.collection("Users").document(email).collection("moodHistory");
        // Error?

        /*public void LoadHistory(ListView moodList)) {
            moodRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>(){
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                String date = documentSnapshot.getString(KEY_DATE);
                                String emotion = documentSnapshot.getString(KEY_EMOTION);
                                String location = documentSnapshot.getString(KEY_LOCATION);
                                String reason = documentSnapshot.getString(KEY_REASON);
                                String social = documentSnapshot.getString(KEY_SOCIAL);
                                String time = documentSnapshot.getString(KEY_TIME);
                            } else {
                                Toast.makeText(MainActivity.this, "Mood Event does not exist");
                            }
                        }
                    });
        }

        /*db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                    moods.add(snapshot.getString("date"));
                    moods.add(snapshot.getString("emotions"));
                    moods.add(snapshot.getString("location"));
                    moods.add(snapshot.getString("reason"));
                    moods.add(snapshot.getString("social"));
                    moods.add(snapshot.getString("time"));
                }
            }
        });
         */
    }



}
