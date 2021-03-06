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
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
/**
 * Followers
 * V1.1
 * 2019-11-07
 *
 * This page contains a list of all pending follow requests, a list of all users the current user
 * is follower and a button which leads to follow_search to find users to add.
 *
 * @author smayer
 * @author pspiers
 */
public class Followers extends Fragment implements RequestContext.RequestSheetListener {
    private ArrayList<String> requestList;
    private ArrayList<String> followerList;
    private ListView request;
    private ListView follower;
    private ArrayAdapter<String> listAdapter;
    private ArrayAdapter followerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_followers, container, false);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideFloatingActionButton(); // hide the FAB
        }

        requestList = new ArrayList<>();
        followerList = new ArrayList<>();
        request = root.findViewById(R.id.requests);
        follower = root.findViewById(R.id.followers);

        final FirebaseFirestore db;
        final String TAG = "Sample";
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final Context context = getContext();
        final RequestContext.RequestSheetListener listener = this;
        CollectionReference pendingColRef = db.collection("Users").document(user.getEmail()).collection("request");
        pendingColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        requestList.add(document.getId());
                    }
                    listAdapter = new RequestContext(context , requestList, listener);
                    request.setAdapter(listAdapter);

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        //get current followed users and add them to the list
        CollectionReference followerColRef = db.collection("Users").document(user.getEmail()).collection("follower");
        followerColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        followerList.add(document.getId());
                    }
                    followerAdapter = new FollowerContext(context , followerList);
                    follower.setAdapter(followerAdapter);
                    registerForContextMenu(follower);
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return root;
    }

    /**
     * The function will dispaly a context menu if the listView is long-clicked.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        MenuInflater inflater = getActivity().getMenuInflater();
        if (v.getId() == R.id.followers) {
            inflater.inflate(R.menu.follower_menu,menu);
        }
    }

    /**
     * The function will handle events when a item in context menu is clicked.
     * It will handles removing the follower from the list and firebase.
     * */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.remove:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                String email = followerList.get(info.position);
                final String TAG = "Remove Following";
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    //Remove user from my follower
                    db.collection("Users").document(user.getEmail()).collection("follower").document(email)
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
            default:
                return super.onContextItemSelected(item);
        }
    }


    /**
    A interface that will pass on state with its position
    if "remove" then delete the ith position in array
    */
    @Override
    public void onButtonClicked(String state, int position) {
        request.setAdapter(listAdapter);
        follower.setAdapter(followerAdapter);
        if (state.equals("delete"))
        {
            requestList.remove(position);
        }
        else
        {
            followerList.add(state);
        }
    }
}
