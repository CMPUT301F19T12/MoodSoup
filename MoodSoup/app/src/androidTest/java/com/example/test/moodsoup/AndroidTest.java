package com.example.test.moodsoup;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

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
     * @author Richard Qin
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
     * @author Richard Qin
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
     * @author Richard Qin
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
     * @author Richard Qin
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
     * @author Belton He <jinzhou@ulaberta.ca>
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
     * Close activity after each test
     * Deletes the accounts created after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
