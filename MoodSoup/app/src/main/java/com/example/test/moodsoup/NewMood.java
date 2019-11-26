package com.example.test.moodsoup;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    private String addressLocation;
    private TextView locationTextView;

    String TAG = "Sample";
    String email;
    String emotionText,reasonText,socialText,locationText;
    private GeoPoint geoPoint;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_CHECK_SETTINGS = 9004;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmood);

        date = findViewById(R.id.new_mood_datetime);
        emotion = findViewById(R.id.new_mood_emotion);
        reason = findViewById(R.id.new_mood_reason);
        social = findViewById(R.id.new_mood_social);
        locationTextView = findViewById(R.id.get_location);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
        final String currentTime = getTimeString(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        final String uploadTime = currentDate+ ' ' + currentTime; // Stored as YYYY-MM-DD HH:MM
        date.setText(uploadTime);

        ImageButton cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createLocationRequest();
                mFusedLocationClient.getLastLocation().addOnSuccessListener(NewMood.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
                            Geocoder geoCoder = new Geocoder(NewMood.this, Locale.getDefault()); //it is Geocoder
                            String errorMessage = "";
                            List<Address> addresses = null;

                            try {
                                addresses = geoCoder.getFromLocation(
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        // In this sample, get just a single address.
                                        1);
                            } catch (IOException ioException) {
                                // Catch network or other I/O problems.
                                errorMessage = getString(R.string.service_not_available);
                                Log.e(TAG, errorMessage, ioException);
                            } catch (IllegalArgumentException illegalArgumentException) {
                                // Catch invalid latitude or longitude values.
                                errorMessage = getString(R.string.invalid_lat_long_used);
                                Log.e(TAG, errorMessage + ". " +
                                        "Latitude = " + location.getLatitude() +
                                        ", Longitude = " +
                                        location.getLongitude(), illegalArgumentException);
                            }

                            // Handle case where no address was found.
                            if (addresses == null || addresses.size()  == 0) {
                                if (errorMessage.isEmpty()) {
                                    errorMessage = getString(R.string.no_address_found);
                                    Log.e(TAG, errorMessage);
                                }
                            } else {
                                Address address = addresses.get(0);
                                ArrayList<String> addressFragments = new ArrayList<String>();

                                // Fetch the address lines using getAddressLine,
                                // join them, and send them to the thread.
                                for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                    addressFragments.add(address.getAddressLine(i));
                                }
                                Log.i(TAG, getString(R.string.address_found));
                                addressLocation = addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea();//+", "+addresses.get(0).getCountryName();
                                //addressLocation = TextUtils.join(System.getProperty("line.separator"),addressFragments);
                                locationTextView.setText(addressLocation);
                            }

                        }
                    }
                });




            }
        });

        ImageButton post = findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emotionText = emotion.getSelectedItem().toString();
                reasonText = reason.getText().toString();
                socialText = social.getSelectedItem().toString();
                if (addressLocation==null){
                    addressLocation = "";
                }
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
                Mood mood = new Mood(email,userName,currentDate, currentTime,emotionText,reasonText,socialText,addressLocation,geoPoint);
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
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
    public String getTimeString( int hourOfDay, int minute, int second) {
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
        if (second<10){
            temptime = temptime + ":0" + second;
        }
        else{
            temptime = temptime + ":" + second;
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

    // GOOGLE MAPS STUFF
    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(NewMood.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }
}

