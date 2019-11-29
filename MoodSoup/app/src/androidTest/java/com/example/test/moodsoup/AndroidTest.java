package com.example.test.moodsoup;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * Test start on the Login Page and deletes the user that is last logged in
 */
@RunWith(AndroidJUnit4.class)
public class AndroidTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance
     * @throws Exception
     */
    @Before
    public void SetUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * @author smayer
     * Attempts to follow test2 with test account using email
     */
    @Test
    public void checkEmailSearchSuccess() {
        final String email = "test@gmail.com";
        final String password = "test123";

        solo.enterText((EditText) solo.getView(R.id.username), email);
        solo.enterText((EditText) solo.getView(R.id.password), password);
        solo.clickOnView(solo.getView(R.id.login));

        solo.sleep(3000);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Search");
        solo.sleep(5000);


        solo.enterText((EditText) solo.getView(R.id.Search_User), "test2@gmail.com");
        solo.clickOnCheckBox(1);
        solo.clickOnButton(2);

        solo.sleep(3000);

        solo.clickOnButton("Send Request");
        solo.sleep(3000);

        final String myEmail = "test@gmail.com";
        final String requestEmail = "test2@gmail.com";

        FirebaseFirestore.getInstance().collection("Users")
                .document(myEmail).collection("pending").document(requestEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String myPending = documentSnapshot.getData().get("pending").toString();
                assertEquals(myPending, requestEmail);
                FirebaseFirestore.getInstance().collection("Users").document(myEmail).collection("pending").document(requestEmail).delete();
            }
        });

        FirebaseFirestore.getInstance().collection("Users")
                .document(requestEmail).collection("request").document(myEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String myRequest = documentSnapshot.getData().get("request").toString();
                assertEquals(myRequest, myEmail);
                FirebaseFirestore.getInstance().collection("Users").document(requestEmail).collection("request").document(myEmail).delete();

            }
        });
    }

    /**
     * @author smayer
     * Attempts to follow test2 with test account using username
     */
    @Test
    public void checkUsernameSearchSuccess() {
        final String email = "test@gmail.com";
        final String password = "test123";

        solo.enterText((EditText) solo.getView(R.id.username), email);
        solo.enterText((EditText) solo.getView(R.id.password), password);
        solo.clickOnView(solo.getView(R.id.login));

        solo.sleep(3000);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Search");
        solo.sleep(5000);


        solo.enterText((EditText) solo.getView(R.id.Search_User), "test2");
        solo.clickOnCheckBox(0);
        solo.clickOnButton(2);

        solo.sleep(3000);

        solo.clickOnButton("Send Request");
        solo.sleep(3000);

        final String myEmail = "test@gmail.com";
        final String requestEmail = "test2@gmail.com";

        FirebaseFirestore.getInstance().collection("Users")
                .document(myEmail).collection("pending").document(requestEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String myPending = documentSnapshot.getData().get("pending").toString();
                assertEquals(myPending, requestEmail);
                FirebaseFirestore.getInstance().collection("Users").document(myEmail).collection("pending").document(requestEmail).delete();
            }
        });

        FirebaseFirestore.getInstance().collection("Users")
                .document(requestEmail).collection("request").document(myEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String myRequest = documentSnapshot.getData().get("request").toString();
                assertEquals(myRequest, myEmail);
                FirebaseFirestore.getInstance().collection("Users").document(requestEmail).collection("request").document(myEmail).delete();

            }
        });
    }

    /**
     * @author smayer
     * Attempts to search for user(test3@gmail.com) that does not exist using email
     */
    @Test
    public void checkEmailSearchFail() {
        final String email = "test@gmail.com";
        final String password = "test123";

        solo.enterText((EditText) solo.getView(R.id.username), email);
        solo.enterText((EditText) solo.getView(R.id.password), password);
        solo.clickOnView(solo.getView(R.id.login));

        solo.sleep(3000);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Search");
        solo.sleep(3000);


        solo.enterText((EditText) solo.getView(R.id.Search_User), "test3@gmail.com");
        solo.clickOnCheckBox(1);
        solo.clickOnButton(2);

        solo.sleep(3000);

        ListView search = (ListView) solo.getView(R.id.search_result);
        assertEquals(search.getAdapter().getCount(),0);
    }

    /**
     * @author smayer
     * Attempts to search for user(test3) that does not exist using username
     */
    @Test
    public void checkUsernameSearchFail() {
        final String email = "test@gmail.com";
        final String password = "test123";

        solo.enterText((EditText) solo.getView(R.id.username), email);
        solo.enterText((EditText) solo.getView(R.id.password), password);
        solo.clickOnView(solo.getView(R.id.login));

        solo.sleep(3000);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Search");
        solo.sleep(5000);


        solo.enterText((EditText) solo.getView(R.id.Search_User), "test3");
        solo.clickOnCheckBox(0);
        solo.clickOnButton(2);

        solo.sleep(3000);

        ListView search = (ListView) solo.getView(R.id.search_result);
        assertEquals(search.getAdapter().getCount(),0);
    }

    /**
     * @author smayer
     * Attempts to Accept a follow request that is manually added
     */
    @Test
    public void checkAcceptFollowRequest() {
        final String email = "test@gmail.com";
        final String requestEmail = "test2@gmail.com";
        final String password = "test123";

        HashMap<String,String> request = new HashMap<>();
        request.put("request",requestEmail);
        FirebaseFirestore.getInstance().collection("Users")
                .document(email).collection("request").document(requestEmail).set(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FAKE", "Request Addition Successful");
                    }
                });

        HashMap<String,String> pending = new HashMap<>();
        pending.put("pending",email);
        FirebaseFirestore.getInstance().collection("Users")
                .document(requestEmail).collection("pending").document(email).set(pending)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FAKE", "Pending Addition Successful");
                    }
                });

        solo.enterText((EditText) solo.getView(R.id.username), email);
        solo.enterText((EditText) solo.getView(R.id.password), password);
        solo.clickOnView(solo.getView(R.id.login));

        solo.sleep(3000);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Follower");
        solo.sleep(5000);

        solo.clickOnImageButton(0);
        solo.sleep(3000);


        FirebaseFirestore.getInstance().collection("Users")
                .document(email).collection("follower").document(requestEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String follower = documentSnapshot.getData().get("follower").toString();
                assertEquals(follower, requestEmail);
                FirebaseFirestore.getInstance().collection("Users").document(email).collection("follower").document(requestEmail).delete();
            }
        });

        FirebaseFirestore.getInstance().collection("Users")
                .document(requestEmail).collection("following").document(email)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String following = documentSnapshot.getData().get("following").toString();
                assertEquals(following, email);
                FirebaseFirestore.getInstance().collection("Users").document(requestEmail).collection("following").document(email).delete();
            }
        });
    }

    /**
     * @author smayer
     * Attempts to declines a follow request that is manually added
     */
    @Test
    public void checkDeclineFollowRequest() {
        final String email = "test@gmail.com";
        final String requestEmail = "test2@gmail.com";
        final String password = "test123";

        HashMap<String,String> request = new HashMap<>();
        request.put("request",requestEmail);
        FirebaseFirestore.getInstance().collection("Users")
                .document(email).collection("request").document(requestEmail).set(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FAKE", "Request Addition Successful");
                    }
                });

        HashMap<String,String> pending = new HashMap<>();
        pending.put("pending",email);
        FirebaseFirestore.getInstance().collection("Users")
                .document(requestEmail).collection("pending").document(email).set(pending)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FAKE", "Pending Addition Successful");
                    }
                });

        solo.enterText((EditText) solo.getView(R.id.username), email);
        solo.enterText((EditText) solo.getView(R.id.password), password);
        solo.clickOnView(solo.getView(R.id.login));

        solo.sleep(3000);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Follower");
        solo.sleep(5000);

        solo.clickOnImageButton(1);
        solo.sleep(3000);

        ListView requestView = (ListView) solo.getView(R.id.requests);
        assertEquals(requestView.getAdapter().getCount(),0);

        FirebaseFirestore.getInstance().collection("Users")
                .document(email).collection("request").document(requestEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                assertTrue(!documentSnapshot.exists());
            }
        });

        FirebaseFirestore.getInstance().collection("Users")
                .document(requestEmail).collection("pending").document(email)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                assertTrue(!documentSnapshot.exists());
            }
        });
    }

    /**
     * @author smayer
     * Attempts to remove a follower that is manually added
     */
    @Test
    public void checkRemoveFollower() {
        final String email = "test@gmail.com";
        final String requestEmail = "test2@gmail.com";
        final String password = "test123";

        HashMap<String,String> follower = new HashMap<>();
        follower.put("follower",requestEmail);
        FirebaseFirestore.getInstance().collection("Users")
                .document(email).collection("follower").document(requestEmail).set(follower)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FAKE", "follower Addition Successful");
                    }
                });

        HashMap<String,String> following = new HashMap<>();
        following.put("following",email);
        FirebaseFirestore.getInstance().collection("Users")
                .document(requestEmail).collection("following").document(email).set(following)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FAKE", "following Addition Successful");
                    }
                });

        solo.enterText((EditText) solo.getView(R.id.username), email);
        solo.enterText((EditText) solo.getView(R.id.password), password);
        solo.clickOnView(solo.getView(R.id.login));

        solo.sleep(3000);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Follower");
        solo.sleep(5000);

        solo.clickLongInList(0);

        solo.clickOnText("Remove Follower");

        FirebaseFirestore.getInstance().collection("Users")
                .document(email).collection("follower").document(requestEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                assertTrue(!documentSnapshot.exists());
            }
        });

        FirebaseFirestore.getInstance().collection("Users")
                .document(requestEmail).collection("following").document(email)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                assertTrue(!documentSnapshot.exists());
            }
        });
    }

    /**
     * @author smayer
     * Attempts to remove a following that is manually added
     */
    @Test
    public void checkRemoveFollowing() {
        final String email = "test@gmail.com";
        final String requestEmail = "test2@gmail.com";
        final String password = "test123";

        HashMap<String,String> following = new HashMap<>();
        following.put("following",requestEmail);
        FirebaseFirestore.getInstance().collection("Users")
                .document(email).collection("following").document(requestEmail).set(following)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FAKE", "following Addition Successful");
                    }
                });

        HashMap<String,String> follower = new HashMap<>();
        follower.put("follower",email);
        FirebaseFirestore.getInstance().collection("Users")
                .document(requestEmail).collection("follower").document(email).set(follower)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FAKE", "follower Addition Successful");
                    }
                });

        solo.enterText((EditText) solo.getView(R.id.username), email);
        solo.enterText((EditText) solo.getView(R.id.password), password);
        solo.clickOnView(solo.getView(R.id.login));

        solo.sleep(3000);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Following");
        solo.sleep(5000);

        solo.clickLongInList(0);

        solo.clickOnText("Stop Following");

        FirebaseFirestore.getInstance().collection("Users")
                .document(email).collection("following").document(requestEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                assertTrue(!documentSnapshot.exists());
            }
        });

        FirebaseFirestore.getInstance().collection("Users")
                .document(requestEmail).collection("follower").document(email)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                assertTrue(!documentSnapshot.exists());
            }
        });
    }

    /**
     * @author smayer
     * Attempts to remove a following that is manually added
     */
    @Test
    public void checkCancelFollowRequest() {
        final String email = "test@gmail.com";
        final String requestEmail = "test2@gmail.com";
        final String password = "test123";

        HashMap<String,String> pending = new HashMap<>();
        pending.put("pending",requestEmail);
        FirebaseFirestore.getInstance().collection("Users")
                .document(email).collection("pending").document(requestEmail).set(pending)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FAKE", "pending Addition Successful");
                    }
                });

        HashMap<String,String> request = new HashMap<>();
        request.put("request",email);
        FirebaseFirestore.getInstance().collection("Users")
                .document(requestEmail).collection("request").document(email).set(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FAKE", "request Addition Successful");
                    }
                });

        solo.enterText((EditText) solo.getView(R.id.username), email);
        solo.enterText((EditText) solo.getView(R.id.password), password);
        solo.clickOnView(solo.getView(R.id.login));

        solo.sleep(3000);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Following");
        solo.sleep(5000);

        solo.clickOnImageButton(0);

        FirebaseFirestore.getInstance().collection("Users")
                .document(email).collection("pending").document(requestEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                assertTrue(!documentSnapshot.exists());
            }
        });

        FirebaseFirestore.getInstance().collection("Users")
                .document(requestEmail).collection("request").document(email)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                assertTrue(!documentSnapshot.exists());
            }
        });
    }

    /**
     * @author rqin1
     * Attempts to register a new user
     * Checks to see if they are logged in with newly created account
     */
    @Test
    public void checkRegistrationSuccess(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(rule.getActivity().getApplicationContext(),Register.class);
        rule.getActivity().startActivity(intent);
        solo.assertCurrentActivity("Wrong Activity: Expected Register",Register.class);
        final String email = "test5@gmail.com";
        solo.enterText((EditText)solo.getView(R.id.email_new_user_tv),email);
        final String username = "test";
        solo.enterText((EditText)solo.getView(R.id.username_new_user_tv),username);
        final String password = "test123";
        solo.enterText((EditText)solo.getView(R.id.password_new_user_tv),password);
        solo.clickOnButton("Register");
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        assertEquals(email, FirebaseAuth.getInstance().getCurrentUser().getEmail());
        FirebaseFirestore.getInstance().collection("Users").document(email).delete();
        FirebaseAuth.getInstance().getCurrentUser().delete();
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * @author rqin1
     * Attempts to create account with precreated existing email: test@gmail.com
     * Checks to see that error text appears
     * Checks to see that activity is still Register Activity
     * Checks to see that no user is logged in
     */
    @Test
    public void checkRegistrationFail(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(rule.getActivity().getApplicationContext(),Register.class);
        rule.getActivity().startActivity(intent);
        solo.assertCurrentActivity("Wrong Activity: Expected Register",Register.class);
        final String email = "test@gmail.com";
        solo.enterText((EditText)solo.getView(R.id.email_new_user_tv),email);
        final String username = "test";
        solo.enterText((EditText)solo.getView(R.id.username_new_user_tv),username);
        final String password = "test1234";
        solo.enterText((EditText)solo.getView(R.id.password_new_user_tv),password);
        solo.clickOnButton("Register");
        solo.waitForText(rule.getActivity().getString(R.string.invalid_email));
        solo.assertCurrentActivity("Wrong Activity: Expected Register",Register.class);
        assertNull(FirebaseAuth.getInstance().getCurrentUser());
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * @author rqin1
     * Checks login with precreated account test@gmail.com
     * Checks to see if moved to MainActivity on Login Button
     * Checks to see if current user is same as email
     */

    @Test
    public void checkLoginSuccess(){
        Intent intent = new Intent(rule.getActivity().getApplicationContext(),Login.class);
        rule.getActivity().startActivity(intent);
        final String email = "test@gmail.com";
        final String password = "test123";
        solo.enterText((EditText)solo.getView(R.id.username),email);
        solo.enterText((EditText)solo.getView(R.id.password),password);
        solo.clickOnView(solo.getView(R.id.login));
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        assertEquals(email, FirebaseAuth.getInstance().getCurrentUser().getEmail());
        FirebaseAuth.getInstance().signOut();
    }
    /**
     * @author rqin1
     * Attempts to login to precreated account test@gmail.com with wrong password
     * Checks to see if still on Login Activity
     * Checks to see if a user is logged in
     */
    @Test
    public void checkLoginFail(){
        Intent intent = new Intent(rule.getActivity().getApplicationContext(),Login.class);
        rule.getActivity().startActivity(intent);
        final String email = "test@gmail.com";
        final String password = "test1234567890";
        solo.enterText((EditText)solo.getView(R.id.username),email);
        solo.enterText((EditText)solo.getView(R.id.password),password);
        solo.clickOnView(solo.getView(R.id.login));
        solo.assertCurrentActivity("Wrong Activity: Expected Login", Login.class);
        assertNull(FirebaseAuth.getInstance().getCurrentUser());
        FirebaseAuth.getInstance().signOut();
    }


    /*
    WILL KEEP THIS HERE FOR REFERENCE
    BUT PROBABLY GONNA DELETE THIS AROUND MIDNIGHT THURSDAY
    @Test
    public void checkFollows(){
        // Create first account
        solo.assertCurrentActivity("Wrong Activity: Expected Login",Login.class);
        solo.clickOnView(solo.getView(R.id.register_btn));
        solo.assertCurrentActivity("Wrong Activity: Expected Register",Register.class);
        final String email = "test@gmail.com";
        solo.enterText((EditText)solo.getView(R.id.email_new_user_tv),email);
        final String username = "test";
        solo.enterText((EditText)solo.getView(R.id.username_new_user_tv),username);
        final String password = "test123";
        solo.enterText((EditText)solo.getView(R.id.password_new_user_tv),password);
        solo.clickOnButton("Register");
        // Create post
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.fab));
        solo.assertCurrentActivity("Wrong Activity: Expected NewMood",NewMood.class);
        solo.clickOnView(solo.getView(R.id.new_mood_emotion));
        solo.clickOnMenuItem("Happy");
        final String reason = "This is a test.";
        solo.enterText((EditText)solo.getView(R.id.new_mood_reason),reason);
        solo.clickOnView(solo.getView(R.id.post));
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        // Logs out
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Logout");
        // Create Second User
        solo.assertCurrentActivity("Wrong Activity: Expected Login",Login.class);
        solo.clickOnView(solo.getView(R.id.register_btn));
        solo.assertCurrentActivity("Wrong Activity: Expected Register",Register.class);
        final String email2 = "test2@gmail.com";
        solo.enterText((EditText)solo.getView(R.id.email_new_user_tv),email2);
        final String username2 = "test2";
        solo.enterText((EditText)solo.getView(R.id.username_new_user_tv),username2);
        final String password2 = "test123";
        solo.enterText((EditText)solo.getView(R.id.password_new_user_tv),password2);
        solo.clickOnButton("Register");
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        // Searches for first user and makes a follow request
        solo.clickOnMenuItem("Search");
        solo.enterText((EditText)solo.getView(R.id.Search_User),email);
        solo.clickOnView(solo.getView(R.id.search_button));
        solo.waitForText(email);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Logout");
        // Logs into first account
        solo.assertCurrentActivity("Wrong Activity: Expected Login",Login.class);
        solo.enterText((EditText)solo.getView(R.id.username),email);
        solo.enterText((EditText)solo.getView(R.id.password),password);
        solo.clickOnView(solo.getView(R.id.login));
        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        // Accepts follow request
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Followers");
        solo.clickOnView(solo.getView(R.id.accept));
        solo.waitForText(email2);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Logout");
        // Logs into second account
        solo.assertCurrentActivity("Wrong Activity: Expected Login",Login.class);
        solo.enterText((EditText)solo.getView(R.id.username),email2);
        solo.enterText((EditText)solo.getView(R.id.password),password2);
        solo.clickOnView(solo.getView(R.id.login));
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        // Checks to see if post made by first account appears
        solo.waitForText(reason);
        // Clean up: deletes both accounts
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).delete();
        FirebaseAuth.getInstance().getCurrentUser().delete();
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Logout");
        solo.assertCurrentActivity("Wrong Activity: Expected Login",Login.class);
        solo.enterText((EditText)solo.getView(R.id.username),email);
        solo.enterText((EditText)solo.getView(R.id.password),password);
        solo.clickOnView(solo.getView(R.id.login));
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).delete();
        FirebaseAuth.getInstance().getCurrentUser().delete();
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Logout");
    }*/

    /**
     * @author jinzhou
     * checks if Profile Navigation works
     * checks if it's user's profile
     */
    @Test
    public void checkProfiles(){
        Intent intent = new Intent(rule.getActivity().getApplicationContext(),Login.class);
        rule.getActivity().startActivity(intent);
        final String email = "test@gmail.com";
        solo.enterText((EditText)solo.getView(R.id.username),email);
        final String password = "test123";
        solo.enterText((EditText)solo.getView(R.id.password),password);
        solo.clickOnView(solo.getView(R.id.login));
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Profile");
        assertEquals(email,FirebaseAuth.getInstance().getCurrentUser().getEmail());
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * @author jinzhou
     * checks whether it system allows to expand a post
     */
    @Test
    public void AccessEditMood() {
        Intent intent = new Intent(rule.getActivity().getApplicationContext(), Login.class);
        rule.getActivity().startActivity(intent);
        final String email = "test@gmail.com";
        solo.enterText((EditText) solo.getView(R.id.username), email);
        final String password = "test123";
        solo.enterText((EditText) solo.getView(R.id.password), password);
        solo.clickOnView(solo.getView(R.id.login));
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Profile");
        solo.clickInList(1);
        solo.assertCurrentActivity("Wrong Activity: Expected MoodViewFragment", MoodViewFragment.class);
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * @author pspiers
     * checks if Maps Navigation works
     */
    @Test
    public void checkMapExists(){
        Intent intent = new Intent(rule.getActivity().getApplicationContext(),Login.class);
        rule.getActivity().startActivity(intent);
        final String email = "test@gmail.com";
        solo.enterText((EditText)solo.getView(R.id.username),email);
        final String password = "test123";
        solo.enterText((EditText)solo.getView(R.id.password),password);
        solo.clickOnView(solo.getView(R.id.login));
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Maps");
        solo.assertCurrentActivity("Wrong Activity: Expected Maps", MoodLocations.class);
        FirebaseAuth.getInstance().signOut();
    }
    /**
     * @author pspiers
     * checks if we go to home when home is pressed form menu
     */
    @Test
    public void checkHome(){
        Intent intent = new Intent(rule.getActivity().getApplicationContext(),Login.class);
        rule.getActivity().startActivity(intent);
        final String email = "test@gmail.com";
        solo.enterText((EditText)solo.getView(R.id.username),email);
        final String password = "test123";
        solo.enterText((EditText)solo.getView(R.id.password),password);
        solo.clickOnView(solo.getView(R.id.login));
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Search");
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Home");
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        FirebaseAuth.getInstance().signOut();
    }
    /**
     * @author pspiers
     * checks if we go to home when home is pressed form menu
     */
    @Test
    public void checkGetsToProfile(){
        Intent intent = new Intent(rule.getActivity().getApplicationContext(),Login.class);
        rule.getActivity().startActivity(intent);
        final String email = "test@gmail.com";
        solo.enterText((EditText)solo.getView(R.id.username),email);
        final String password = "test123";
        solo.enterText((EditText)solo.getView(R.id.password),password);
        solo.clickOnView(solo.getView(R.id.login));
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Profile");
        solo.assertCurrentActivity("Wrong Activity: Expected Profile", ProfileFragment.class);
        FirebaseAuth.getInstance().signOut();
    }
    /**
     * @author pspiers
     * checks if the sidebar can be opened/closed multiple times
     */
    @Test
    public void checkSidebar(){
        Intent intent = new Intent(rule.getActivity().getApplicationContext(),Login.class);
        rule.getActivity().startActivity(intent);
        final String email = "test@gmail.com";
        solo.enterText((EditText)solo.getView(R.id.username),email);
        final String password = "test123";
        solo.enterText((EditText)solo.getView(R.id.password),password);
        solo.clickOnView(solo.getView(R.id.login));
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Search");
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Home");
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Search");
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Home");
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Search");
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Home");
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Close activity after each test
     * Deletes the accounts created after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
