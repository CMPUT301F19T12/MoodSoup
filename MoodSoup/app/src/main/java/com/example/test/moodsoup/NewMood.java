package com.example.test.moodsoup;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * Handles creating a new mood
 * @author Sanae Mayer
 * @author Richard Qin
 */

public class NewMood extends AppCompatActivity{
    // Variable Declaration
    private TextView dateTV,locationTV,title;
    private Spinner emotion, social;
    private EditText reason;
    private ImageButton addPhoto;
    private boolean errors = false;
    private String email,emotionText,reasonText,socialText, addressLocation;
    private GeoPoint geoPoint;
    private FusedLocationProviderClient mFusedLocationClient;

    // Constants
    int reqCode = -1;
    private static final int REQUEST_CHECK_SETTINGS = 9004;
    String TAG = "Sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmood);

        // Variable Initialization
        title = findViewById(R.id.addnew_tv);
        dateTV = findViewById(R.id.new_mood_datetime);
        emotion = findViewById(R.id.new_mood_emotion);
        reason = findViewById(R.id.new_mood_reason);
        social = findViewById(R.id.new_mood_social);
        locationTV = findViewById(R.id.get_location);
        addPhoto = findViewById(R.id.add_photo);


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
        String currentDate = getDateString(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        String currentTime = getTimeString(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        String uploadTime = currentDate + ' ' + currentTime; // Stored as YYYY-MM-DD HH:MM

        // Can be passed information in order to edit a mood
        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            // Not editing a mood
            if (extras == null)
            {
                dateTV.setText(uploadTime);
            }
            // Information given to edit a mood
            else
            {
                // Change title of Activity
                title.setText(getString(R.string.edit_mood));
                // Pre-fill fields with given information
                uploadTime = extras.getString("date") + ' ' + extras.getString("time");
                currentDate = extras.getString("date");
                currentTime = extras.getString("time");
                String currentEmotion = extras.getString("emotion");
                String currentReason = extras.getString("reason");
                String currentSocial = extras.getString("social");
                String currentLocation = extras.getString("location");
                dateTV.setText(uploadTime);

                ArrayAdapter emotionAdapter = (ArrayAdapter) emotion.getAdapter();
                int emotionPosition = emotionAdapter.getPosition(currentEmotion);
                emotion.setSelection(emotionPosition);
                if (!currentReason.equals("")) {
                    reason.setText(currentReason);
                }

                ArrayAdapter socialAdapter = (ArrayAdapter) social.getAdapter();
                int socialPosition = socialAdapter.getPosition(currentSocial);
                social.setSelection(socialPosition);

                if (!currentLocation.equals("")) {
                    locationTV.setText(currentLocation);
                    addressLocation = currentLocation;
                }
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                // Create a storage reference from our app
                StorageReference storageRef = firebaseStorage.getReference();
                // Create a reference to "mountains.jpg"
                StorageReference imageRef = storageRef.child(extras.getString("email") + "/" + uploadTime + ".jpg");
                // Create a reference to 'images/mountains.jpg'
                StorageReference imageReference = storageRef.child("images/" + extras.getString("email") + "/" + uploadTime + ".jpg");

                // While the file names are the same, the references point to different files
                imageRef.getName().equals(imageReference.getName());    // true
                imageRef.getPath().equals(imageReference.getPath());    // false

                final long ONE_MEGABYTE = 1024 * 1024;
                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        addPhoto.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(),
                                bmp.getHeight(), false));
                    }
                });
                if (extras.containsKey("latitude") && extras.containsKey("longitude")) {
                    geoPoint = new GeoPoint(extras.getDouble("latitude"), extras.getDouble("longitude"));
                }
            }
        }

        // Create final variables for date/time
        final String finalCurrentDate = currentDate;
        final String finalCurrentTime = currentTime;
        final String finalUploadTime = uploadTime;

        // Cancel button
        ImageButton cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Clicking the add location text view automatically gets your location
        locationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gets permission to access location if not granted
                if (ContextCompat.checkSelfPermission(NewMood.this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(NewMood.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                } else {
                    createLocationRequest();
                    // Gets GPS Coordinates
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(NewMood.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
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
                                if (addresses == null || addresses.size() == 0) {
                                    if (errorMessage.isEmpty()) {
                                        errorMessage = getString(R.string.no_address_found);
                                        Log.e(TAG, errorMessage);
                                    }
                                } else {
                                    Log.i(TAG, getString(R.string.address_found));
                                    addressLocation = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea();//+", "+addresses.get(0).getCountryName();
                                    //addressLocation = TextUtils.join(System.getProperty("line.separator"),addressFragments);
                                    locationTV.setText(addressLocation);
                                }

                            }

                        }
                    });
                }
            }
        });

        // Post button
        ImageButton post = findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errors = false;
                emotionText = emotion.getSelectedItem().toString();
                reasonText = reason.getText().toString();
                socialText = social.getSelectedItem().toString();
                // If no location given
                if (addressLocation==null){
                    addressLocation = "";
                }
                // If no social situation given
                findViewById(R.id.new_mood_error_emotion).setVisibility(View.INVISIBLE);
                if (socialText.equals("Choose a social situation:")) {
                    socialText = "";
                }
                // Error Handling
                // Emotional state is required, create error message and set errors to true
                if (emotionText.equals("Choose an emotion:")) {
                    findViewById(R.id.new_mood_error_emotion).setVisibility(View.VISIBLE);
                    Toast.makeText(NewMood.this, "Please Fill out the Required Fields",
                            Toast.LENGTH_SHORT).show();
                    errors = true;
                }
                // Reason must be less than twenty characters, if greater than: create error message and set errors to true
                findViewById(R.id.new_mood_error_reason).setVisibility(View.INVISIBLE);
                if (reasonText.length() > 20) {
                    findViewById(R.id.new_mood_error_reason).setVisibility(View.VISIBLE);
                    errors = true;
                }

                // If no errors
                if (!errors){
                    //IMPLEMENT USER CLASS
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    email = mAuth.getCurrentUser().getEmail();

                    // Add mood to history
                    DocumentReference usernameRef = db.collection("Users").document(email);
                    usernameRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    createNewMood((String) document.getData().get("username"));
                                    createNewImage();
                                }
                            }
                        }
                    });
                }
            }

            /**
             * Uploads mood to user's moodHistory
             * @param userName
             * Create mood object with userName
             */
            public void createNewMood(String userName) {
                Mood mood = new Mood(email, userName, finalCurrentDate, finalCurrentTime, emotionText, reasonText, socialText, addressLocation, geoPoint);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collectionReference = db.collection("Users");
                collectionReference
                        .document(email)
                        .collection("moodHistory")
                        .document(finalUploadTime)
                        .set(mood)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Data Addition Successful");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Data Addition Failed" + e.toString());
                            }
                        });
                Intent intent = new Intent(NewMood.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            /**
             * Uploads images to storage
             */
            public void createNewImage(){
                if (reqCode != -1) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    email = mAuth.getCurrentUser().getEmail();

                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    // Create a storage reference from our app
                    StorageReference storageRef = firebaseStorage.getReference();
                    // Create a reference to "mountains.jpg"
                    StorageReference imageRef = storageRef.child(email + "/" + finalCurrentDate + " " + finalCurrentTime + ".jpg");

                    StorageReference imageReference = storageRef.child("images/" + email + "/" + finalCurrentDate + " " + finalCurrentTime + ".jpg");

                    // While the file names are the same, the references point to different files
                    imageRef.getName().equals(imageReference.getName());    // true
                    imageRef.getPath().equals(imageReference.getPath());    // false

                    Bitmap bm = ((BitmapDrawable) addPhoto.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = imageRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                        }
                    });
                }
            }
        });


        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = NewMood.this;
                Activity activity = NewMood.this;
                int ExtstorePermission = ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                int cameraPermission = ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA);
                List<String> listPermissionsNeeded = new ArrayList<>();
                if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(Manifest.permission.CAMERA);
                }
                if (ExtstorePermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded
                            .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (!listPermissionsNeeded.isEmpty()) {
                    ActivityCompat.requestPermissions(activity, listPermissionsNeeded
                                    .toArray(new String[listPermissionsNeeded.size()]),
                            1);
                }
                else{
                    selectImage(NewMood.this);
                }
            }
        });
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @Nonnull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        int ExtstorePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();

        switch (requestCode){
            case 1:
                if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(Manifest.permission.CAMERA);
                }
                if (ExtstorePermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded
                            .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (listPermissionsNeeded.isEmpty()) {
                    selectImage(NewMood.this);
                }
                return;
        }
    }

    /**
     * Creates a pop up menu
     * @param context
     * context is the menu that is being created
     */
    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    reqCode = 0;
                    startActivityForResult(takePicture, reqCode);
                    dialog.dismiss();

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    reqCode = 1;
                    startActivityForResult(pickPhoto , reqCode);
                    dialog.dismiss();

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * displays preview image
     * @param requestCode
     * Check which request to respond to
     * @param resultCode
     * Check if request was successful
     * @param data
     * Determine which contact was selected
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        ImageView photo = findViewById(R.id.add_photo);
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
                Bitmap photoBit = (Bitmap) data.getExtras().get("data");
                photo.setImageBitmap(photoBit);
            }
        }
        else if (requestCode == 1)
        {
            if (resultCode == RESULT_OK) {
                photo.setImageURI(data.getData());
            }
        }
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

    /**
     * Checks if location services are active
     */
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

