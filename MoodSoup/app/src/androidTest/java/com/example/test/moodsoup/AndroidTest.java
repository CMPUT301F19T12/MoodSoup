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
        final String email = "omniguardian@gmail.com";
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
