package com.example.test.moodsoup;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.opencensus.metrics.export.Summary;

public class Profile extends AppCompatActivity  implements PendingContext.SheetListener{
    private ArrayList<String> moods = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;
    private AppBarConfiguration mAppBarConfiguration;
    private String TAG = "ERROR HERE!";
    private ListView moodList;
    private TextView profileName;
    private ImageButton toFollowing;
    private ImageButton imageButton2;
    private static final String KEY_DATE = "Date";
    private static final String KEY_EMOTION = "Emotion";
    private static final String KEY_LOCATION = "Location";
    private static final String KEY_REASON = "Reason";
    private static final String KEY_SOCIAL = "Social";
    private static final String KEY_TIME = "Time";
    private static int RESULT_LOAD_IMG = 1;
    private ListView event;

    private ArrayList<String> event_list;
    private ArrayAdapter<String> event_listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        event = findViewById(R.id.event_list_self);
        ///Set profile picture
        final ImageButton button = (ImageButton)findViewById(R.id.imageButton2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setSelected(!button.isPressed());

                if (button.isPressed()) {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);


                    button.setImageResource(R.drawable.moodsoup_happy);
                }
                else {
                    button.setImageResource(R.drawable.moodsoup_sad);
                }
            }
        });

        // View ID
        profileName = findViewById(R.id.ProfileName);
        moodList = findViewById(R.id.event_list_self);
        toFollowing = findViewById(R.id.imageButton);

        // User Instance

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        //User info
        toFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FollowingIntent = new Intent(getApplicationContext(), Following.class);
                startActivity(FollowingIntent);
            }
        });

        String uid = user.getUid();
        String name = user.getDisplayName();
        final String email = user.getEmail();


        // Set user name on profile layout display view
        final TextView display_name =  profileName;
        display_name.setText("User email: "+email);



        final ListView event = findViewById(R.id.event_list_self);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference colRef = db.collection("Users").document(email).collection("moodHistory");
        event_list = new ArrayList<>();
        final Context context = this;
        final PendingContext.SheetListener listener = this;




        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {


                    for (QueryDocumentSnapshot document : task.getResult())
                    {


                        event_list.add(document.getId());

                    }

                    event_listAdapter = new PendingContext(context , event_list, listener);
                    event.setAdapter(event_listAdapter);




                } else {
                    Log.d(TAG, "FAIL", task.getException());
                }
            }
        });



    }

    @Override
    public void onButtonClicked(String state, int position) {
        event.setAdapter(event_listAdapter);
        if (state.equals("delete"))
        {
            event_list.remove(position);
            Toast.makeText(getApplicationContext(),"Mood Deleted",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Profile.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
