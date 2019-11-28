package com.example.test.moodsoup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * @author Richard Qin
 * @author Darian Chen
 * @author Sanae Mayer
 * Handles navigating to the different fragments and activities
 * Hosts the user's information and passes it to the different fragments and activities
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private AppBarConfiguration mAppBarConfiguration;
    FirebaseFirestore db;
    FloatingActionButton fab;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

        showFloatingActionButton(); // Show the FAB

        // Floating button in bottom right corner
        // Used to add a new mood



        fab.setOnTouchListener(new View.OnTouchListener() {

            float horizontal;
            float horizontalX;
            float actualX;
            int before;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        horizontal = view.getX() - event.getRawX();
                        horizontalX = event.getRawX();
                        before = MotionEvent.ACTION_DOWN;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        view.setX(event.getRawX() + horizontal);
                        view.setY(event.getRawY() + horizontal);

                        before = MotionEvent.ACTION_MOVE;
                        break;

                    case MotionEvent.ACTION_UP:
                        actualX = event.getRawX()-horizontalX;
                        // If user clicks to add a new mood...
                        if (Math.abs(actualX)< 8){
                            Intent newMood = new Intent(MainActivity.this, NewMood.class);
                            startActivity(newMood);
                        }
                        break;
                    case MotionEvent.ACTION_BUTTON_PRESS:

                    default:
                        return false;
                }
                return true;
            }
        });


        // Initialize Database information
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        // Initialize Sidebar and navigation
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_search,R.id.nav_map, R.id.nav_follower, R.id.nav_following, R.id.nav_profile, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Get logged in user
        FirebaseUser cUser = mAuth.getCurrentUser();
        if (cUser != null) {
            String email = cUser.getEmail();
            DocumentReference docRef = db.collection("Users").document(email);
            final String TAG = "DOCUMENT ";
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Changes username in the sidebar panel
                            TextView username = findViewById(R.id.nav_header_Username);
                            String usern = (String) document.getData().get("username");
                            username.setText(usern);
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                }
            });
        }
    }

    /**
     * Changes between fragments
     * @param item
     * item is the fragment to be changed to
     * @return
     * returns true on success
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        switch(item.getItemId()){
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.mobile_navigation, new ProfileFragment()).commit();
                break;
        }
        return true;
    }

    /**
     * creates a menu list in top right corner
     * (currently not in use)
     * @param menu
     * menu item to be put in top right corner
     * @return
     * returns true on success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    /**
     * Handles back button press
     * Navigates to previous fragment on back button press
     * @return
     * returns true on success
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Checks to see if user is logged in at launch
     */
    @Override
    public void onStart(){
        super.onStart();
        if (mAuth.getCurrentUser() == null){
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
    }

    public void showFloatingActionButton() {
        fab.show();
    };

    public void hideFloatingActionButton() {
        fab.hide();
    };
}
