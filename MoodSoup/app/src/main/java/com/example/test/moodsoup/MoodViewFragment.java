package com.example.test.moodsoup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Creates a larger view for the moods
 * @author Richard Qin
 */
public class MoodViewFragment extends Fragment {
    // Declare Variables
    private String email;
    private String uploadTime;
    private String emotion;
    private String reason;
    private String situation;
    private String location;
    private FirebaseFirestore db;
    private TextView emailTV;
    private TextView dateTV;
    private TextView emotionTV;
    private TextView reasonTV;
    private TextView situationTV;
    private TextView locationTV;
    private ImageView imageView;
    private TextView situationTV2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize Variables
        final View root = inflater.inflate(R.layout.activity_mood_view_fragment, container, false);
        db = FirebaseFirestore.getInstance();
        email = MoodViewFragmentArgs.fromBundle(getArguments()).getEmail();
        uploadTime = MoodViewFragmentArgs.fromBundle(getArguments()).getUploadTime();

        emailTV = root.findViewById(R.id.view_mood_username);
        dateTV = root.findViewById(R.id.view_mood_upload_time);
        emotionTV = root.findViewById(R.id.view_mood_emotion);
        reasonTV = root.findViewById(R.id.view_mood_reason);
        situationTV = root.findViewById(R.id.view_mood_situation);
        locationTV = root.findViewById(R.id.view_mood_location);
        imageView = root.findViewById(R.id.view_mood_image);
        situationTV2 = root.findViewById(R.id.textView13);

        // Hide the Floating Add Mood Button
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideFloatingActionButton(); // hide the FAB
        }

        // Get details of the mood and set TextViews to display details
        DocumentReference documentReference = db.collection("Users").document(email).collection("moodHistory").document(uploadTime);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        emailTV.setText(email);
                        dateTV.setText(uploadTime);
                        emotion = documentSnapshot.get("emotion").toString();
                        emotionTV.setText(emotion);
                        reason = documentSnapshot.get("reason").toString();
                        if (!reason.equals("")){
                            reason = "\"\"" + reason + "\"\"";
                            reasonTV.setVisibility(View.VISIBLE);
                            reasonTV.setText(reason);
                        }
                        situation = documentSnapshot.get("social").toString();
                        if (!situation.equals("")){
                            situationTV.setVisibility(View.VISIBLE);
                            situationTV2.setVisibility(View.VISIBLE);
                            situationTV.setText(situation);
                        }
                        location = documentSnapshot.get("location").toString();
                        if (!location.equals("")){
                            locationTV.setVisibility(View.VISIBLE);
                            locationTV.setText(location);
                        }


                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        // Create a storage reference from our app
                        StorageReference storageRef = firebaseStorage.getReference();
                        // Create a reference to "mountains.jpg"
                        StorageReference imageRef = storageRef.child(email + "/" + uploadTime + ".jpg");
                        // Create a reference to 'images/mountains.jpg'
                        StorageReference imageReference = storageRef.child("images/" + email + "/" + uploadTime + ".jpg");

                        // While the file names are the same, the references point to different files
                        imageRef.getName().equals(imageReference.getName());    // true
                        imageRef.getPath().equals(imageReference.getPath());    // false

                        final long ONE_MEGABYTE = 1024 * 1024;
                        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                imageView.setVisibility(View.VISIBLE);
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(),
                                        bmp.getHeight(), false));
                            }
                        });

                    }
                }
            }
        });

        emailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(root).navigate(MoodViewFragmentDirections.actionNavMoodViewFragmentToNavProfile().setEmail(email));
            }
        });

        return root;
    }
}
