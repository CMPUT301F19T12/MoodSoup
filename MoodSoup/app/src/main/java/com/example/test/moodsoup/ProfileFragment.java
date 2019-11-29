package com.example.test.moodsoup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

/**
 * @author Belton He <jinzhou@ualberta.ca>
 * @author Atilla Ackbay
 * This page displays moods of the user
 */
public class ProfileFragment extends Fragment implements PendingContext.SheetListener{
    private FirebaseAuth mAuth;
    private String TAG = "ERROR HERE!";
    private ListView moodList;
    private TextView profileName;
    private ListView event;
    private ArrayList<Mood> event_list;
    private ArrayAdapter<Mood> event_listAdapter;
    private String emailFromBundle;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.profile,container,false);
        event = root.findViewById(R.id.event_list_self);
        ///Set profile picture
        ProfileFragmentArgs profileFragmentArgs = ProfileFragmentArgs.fromBundle(getArguments());
        emailFromBundle = profileFragmentArgs.getEmail();

        // View ID
        profileName = root.findViewById(R.id.ProfileName);
        moodList = root.findViewById(R.id.event_list_self);
        registerForContextMenu(moodList);
      
        // User Instance

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //User info

        if (emailFromBundle.equals("No Email")){
            emailFromBundle = mAuth.getCurrentUser().getEmail();
        }

        if (emailFromBundle.equals(mAuth.getCurrentUser().getEmail())){
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showFloatingActionButton(); // hide the FAB
            }
        } else {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).hideFloatingActionButton(); // hide the FAB
            }
        }

        String uid = user.getUid();
        String name = user.getDisplayName();
        final String email = emailFromBundle;


        // Set user name on profile layout display view
        final TextView display_name =  profileName;
        display_name.setText(email);


        final ListView event = root.findViewById(R.id.event_list_self);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        event_list = new ArrayList<>();
        final Context context = getActivity();
        final PendingContext.SheetListener listener = this;

        // Get the moodHistory for the user in profile --> Not finished yet since we are just pulling the dates, will fix later.

        CollectionReference colRef = db.collection("Users").document(email).collection("moodHistory");
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Mood mood = new Mood(document.get("email").toString(), document.get("username").toString(), document.get("date").toString(), document.get("time").toString(), document.get("emotion").toString(), document.get("reason").toString(), document.get("social").toString(), document.get("location").toString(), (GeoPoint) document.get("coords"));
                        event_list.add(mood);
                    }

                } else {
                    Log.d(TAG, "FAIL", task.getException());
                }
                Collections.sort(event_list, new StringDateComparator());
                event_listAdapter = new MoodList(context, event_list);
                moodList.setAdapter(event_listAdapter);
            }
        });


        moodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Navigation.findNavController(root).navigate(ProfileFragmentDirections.actionNavProfileToNavMoodViewFragment(emailFromBundle,event_list.get(i).getDate(),event_list.get(i).getTime()));
            }
        });


        return root;

    }
    @Override
    public void onButtonClicked(String state, int position) {
        moodList.setAdapter(event_listAdapter);
        if (state.equals("delete"))
        {
            String uploadTime = event_list.get(position).getDate() +" "+event_list.get(position).getTime();
            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("moodHistory").document().delete();
            event_list.remove(position);
            Toast.makeText(getActivity(),"Mood Deleted",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * The function will dispaly a context menu if the listView is long-clicked.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu,v,menuInfo);
        System.out.println("Long clicked");
        MenuInflater inflater = getActivity().getMenuInflater();
        if (v.getId() == R.id.event_list_self) {
            inflater.inflate(R.menu.profile_menu,menu);
        }
    }

    /**
     * The function will handle events when a item in context menu is clicked.
     * It will handles removing the following from the list and firebase.
     * */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.delete:
                Mood deleteMood = event_list.get(info.position);
                final String TAG = "Remove Following";
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    //Delete post from moodHistory
                    db.collection("Users").document(user.getEmail()).collection("moodHistory").document(deleteMood.getDate() + " " + deleteMood.getTime())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting document ", e);
                                }
                            });

                    //If there is any image, delete the image as well
                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    // Create a storage reference from our app
                    StorageReference storageRef = firebaseStorage.getReference();
                    // Create a reference to "mountains.jpg"
                    StorageReference imageRef = storageRef.child(deleteMood.getEmail() + "/" + deleteMood.getDate() + ' ' + deleteMood.getTime() + ".jpg");
                    // Create a reference to 'images/mountains.jpg'
                    StorageReference imageReference = storageRef.child("images/" + deleteMood.getEmail() + "/" + deleteMood.getDate() + ' ' + deleteMood.getTime() + ".jpg");

                    // While the file names are the same, the references point to different files
                    imageRef.getName().equals(imageReference.getName());    // true
                    imageRef.getPath().equals(imageReference.getPath());    // false

                    imageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });

                }
                moodList.setAdapter(event_listAdapter);
                event_list.remove(info.position);
                return true;
            case R.id.edit:
                Mood editMood = event_list.get(info.position);
                Intent intent = new Intent(getActivity(), NewMood.class);
                intent.putExtra("date",editMood.getDate());
                intent.putExtra("time",editMood.getTime());
                intent.putExtra("emotion",editMood.getEmotion());
                intent.putExtra("email",editMood.getEmail());
                intent.putExtra("reason",editMood.getReason());
                intent.putExtra("social",editMood.getSocial());
                intent.putExtra("location",editMood.getLocation());
                if (event_list.get(info.position).getCoords() != null) {
                    intent.putExtra("latitude", event_list.get(info.position).getCoords().getLatitude());
                    intent.putExtra("longitude", event_list.get(info.position).getCoords().getLongitude());
                }
                startActivity(intent);
            default:
                return super.onContextItemSelected(item);
        }
    }
}