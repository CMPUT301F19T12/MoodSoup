package com.example.test.moodsoup;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
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
import static junit.framework.TestCase.assertTrue;

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
        Intent intent = new Intent(rule.getActivity().getApplicationContext(),Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        rule.getActivity().startActivity(intent);
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
     * Registers a new user and checks to see if they are logged in
     * Then logs the user out and attempts to log in
     * Then checks to see if the profile logged into is the correct one
     */
    @Test
    public void checkRegistration(){
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
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.sleep(2000);
        assertTrue(solo.waitForText(username,1,2000));
        solo.clickOnMenuItem("Logout");
        solo.assertCurrentActivity("Wrong Activity: Expected Login",Login.class);
        solo.enterText((EditText)solo.getView(R.id.username),email);
        solo.enterText((EditText)solo.getView(R.id.password),password);
        solo.clickOnView(solo.getView(R.id.login));
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.sleep(2000);
        assertTrue(solo.waitForText(username,1,2000));
    }

    @Test
    public void checkFollows(){
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
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.fab));
        solo.assertCurrentActivity("Wrong Activity: Expected NewMood",NewMood.class);
        solo.clickOnView(solo.getView(R.id.new_mood_emotion));
        solo.clickOnMenuItem("Happy");
        final String reason = "This is a test.";
        solo.enterText((EditText)solo.getView(R.id.new_mood_reason),reason);
        solo.clickOnView(solo.getView(R.id.post));
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
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
        solo.clickOnMenuItem("Search");
        //solo.assertCurrentActivity("Wrong Fragment: Expected SearchFragment", SearchFragment.class);
        solo.enterText((EditText)solo.getView(R.id.Search_User),email);
        solo.clickOnView(solo.getView(R.id.search_button));
        //solo.assertCurrentActivity("Wrong Fragment: Expected Following", Following.class);
        solo.waitForText(email);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Logout");
        solo.assertCurrentActivity("Wrong Activity: Expected Login",Login.class);
        solo.enterText((EditText)solo.getView(R.id.username),email);
        solo.enterText((EditText)solo.getView(R.id.password),password);
        solo.clickOnView(solo.getView(R.id.login));
        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Followers");
        solo.clickOnView(solo.getView(R.id.accept));
        solo.waitForText(email2);
        ((DrawerLayout) solo.getView(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
        solo.clickOnMenuItem("Logout");
        solo.assertCurrentActivity("Wrong Activity: Expected Login",Login.class);
        solo.enterText((EditText)solo.getView(R.id.username),email2);
        solo.enterText((EditText)solo.getView(R.id.password),password2);
        solo.clickOnView(solo.getView(R.id.login));
        solo.assertCurrentActivity("Wrong Activity: Expected MainActivity", MainActivity.class);
        solo.waitForText(reason);
    }


    /**
     * Close activity after each test
     * Deletes the accounts created after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).delete();
            FirebaseAuth.getInstance().getCurrentUser().delete();
            FirebaseAuth.getInstance().signOut();
        }
    }
}
