package com.example.test.moodsoup;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import static androidx.navigation.Navigation.findNavController;
/**
 * Following
 * V1.1
 * 2019-11-07
 *
 * This page contains a list of all the people who are following you and a list of all the requests
 * that you have active. You may delete a follower and you may accept or reject a request.
 *
 *@author smayer <smayer@ualberta.ca>
 * @author Peter Spiers <pspiers@ualberta.ca>
 */

public class Following extends Fragment implements PendingContext.SheetListener {
    private ArrayList<String> pendingList;
    private ArrayList<String> followerList;
    private ListView pending;
    private ListView follower;
    private ArrayAdapter<String> pendingListAdapter;
    private ArrayAdapter followerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_following, container, false);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideFloatingActionButton(); // hide the FAB
        }

        final Button search = root.findViewById(R.id.Search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNavController(root).navigate(R.id.nav_search );
            }
        });

        pendingList = new ArrayList<>();
        followerList = new ArrayList<>();
        pending = root.findViewById(R.id.pending);
        follower = root.findViewById(R.id.follower);

        //registerForContextMenu(following);
        final FirebaseFirestore db;
        final String TAG = "Sample";
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final Context context = getContext();
        final PendingContext.SheetListener listener = this;
        CollectionReference colRef = db.collection("Users").document(user.getEmail()).collection("pending");
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        pendingList.add(document.getId());
                    }
                    pendingListAdapter = new PendingContext(context, pendingList, listener);
                    pending.setAdapter(pendingListAdapter);
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        //Add a follower
        CollectionReference followerColRef = db.collection("Users").document(user.getEmail()).collection("following");
        followerColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        followerList.add(document.getId());
                    }
                    followerAdapter = new FollowerContext(context, followerList);
                    follower.setAdapter(followerAdapter);
                    registerForContextMenu(follower);
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        follower.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Navigation.findNavController(root).navigate(FollowingDirections.actionNavFollowingToNavProfile().setEmail(followerList.get(i)));
            }
        });

        return root;
    }

    /**
     * The function will dispaly a context menu if the listView is long-clicked.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        if (v.getId() == R.id.follower) {
            inflater.inflate(R.menu.follower_menu, menu);
        }
    }

    /**
     * The function will handle events when a item in context menu is clicked.
     * It will handles removing the follower from the list and firebase.
     * */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                String email = followerList.get(info.position);
                final String TAG = "Remove Following";
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    //Remove user from my follower
                    db.collection("Users").document(user.getEmail()).collection("following").document(email)
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

                    //Remove me from user's follower list
                    db.collection("Users").document(email).collection("follower").document(user.getEmail())
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
                }
                follower.setAdapter(followerAdapter);
                followerList.remove(info.position);
                return true;
            case R.id.move_to_profile:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                email = followerList.get(info.position);
                Navigation.findNavController(getView()).navigate(FollowingDirections.actionNavFollowingToNavProfile().setEmail(email));
            default:
                return super.onContextItemSelected(item);
        }

    }

    /**
     A interface that will pass on state with its position
     if "delete' then delete the ith position in array
     */
    @Override
    public void onButtonClicked(String state, int position) {
        pending.setAdapter(pendingListAdapter);
        if (state.equals("delete")) {
            pendingList.remove(position);
            Toast.makeText(getContext(), "User Deleted",
                    Toast.LENGTH_SHORT).show();
        }
    }
}