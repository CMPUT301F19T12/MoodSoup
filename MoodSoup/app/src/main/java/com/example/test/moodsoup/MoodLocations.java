package com.example.test.moodsoup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * MoodLocations
 * V1.2
 *
 * Handles loading up the Google MapView
 * Handles placing markers on the MapView
 *
 * @author rqin1
 */
public class MoodLocations extends Fragment implements OnMapReadyCallback {

    // Variable Declaration
    private MapView mMapView;
    private Spinner options;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FirebaseFirestore db;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.activity_mood_locations, container, false);

        // Hide the floating add button on this fragment
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideFloatingActionButton(); // hide the FAB
        }

        // Variable initialization
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mMapView = root.findViewById(R.id.mapView);
        initGoogleMap(savedInstanceState);

        // Zooms map to location of user, if gps is on
        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng curPos = new LatLng(location.getLatitude(),location.getLongitude());
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(curPos).zoom(17).bearing(0).tilt(0).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                });

        // Initialize drop down menu
        options = root.findViewById(R.id.map_options_spinner);
        ArrayAdapter<String> mapOptions = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.map_options));
        mapOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        options.setAdapter(mapOptions);

        //
        options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // If first option is selected: Own Moods
                if (i == 0){
                    // Cleans off all markers from map
                    mMap.clear();
                    if (user != null){
                        // Gets all moods events in own mood history
                        CollectionReference moodHistoryRef = db.collection("Users").document(user.getEmail()).collection("moodHistory");
                        moodHistoryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Mood mood = new Mood(document.get("email").toString(),
                                                document.get("username").toString(),document.get("date").toString(),
                                                document.get("time").toString(),document.get("emotion").toString(),
                                                document.get("reason").toString(),document.get("social").toString(),
                                                document.get("location").toString(),(GeoPoint)document.get("coords"), (boolean) document.get("imgIncluded"));
                                        // Creates a marker for moods with a gps coordinate
                                        if (mood.getCoords() != null) {
                                            LatLng coordinates = new LatLng(mood.getCoords().getLatitude(), mood.getCoords().getLongitude());
                                            String title = mood.getDate()+" "+mood.getTime()+ ": "+mood.getUsername()+ " was " +mood.getEmotion();
                                            mMap.addMarker(new MarkerOptions().position(coordinates).title(title));
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });

                    }
                }
                // If second option is selected: Other's Moods
                else if (i == 1){
                    // Cleans up all markers from map view
                    mMap.clear();
                    if (user != null){
                        // Gets all users that the user is following
                        CollectionReference followerColRef = db.collection("Users").document(user.getEmail()).collection("following");
                        followerColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Gets all moods of users that are being followed
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        CollectionReference followerMoodColRef = db.collection("Users").document(document.getId()).collection("moodHistory");
                                        followerMoodColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        // Create Mood Object
                                                        Mood mood = new Mood(document.get("email").toString(),
                                                                document.get("username").toString(), document.get("date").toString(),
                                                                document.get("time").toString(), document.get("emotion").toString(),
                                                                document.get("reason").toString(), document.get("social").toString(),
                                                                document.get("location").toString(), (GeoPoint)document.get("coords"), (boolean) document.get("imgIncluded"));
                                                        // Creates a marker for moods with a gps coordinate
                                                        if (mood.getCoords() != null) {
                                                            LatLng coordinates = new LatLng(mood.getCoords().getLatitude(), mood.getCoords().getLongitude());
                                                            String title = mood.getDate()+" "+mood.getTime()+ ": "+mood.getUsername()+ " was " +mood.getEmotion();
                                                            mMap.addMarker(new MarkerOptions().position(coordinates).title(title));
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return root;
    }

    /**
     * Initializes MapView
     * @param savedInstanceState
     */
    private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    /**
     * Checks permissions and finds User Location
     * @param map
     * map is Google Map Object
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
