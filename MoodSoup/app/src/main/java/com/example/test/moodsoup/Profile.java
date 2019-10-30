package com.example.test.moodsoup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        final Button toFollowing = findViewById(R.id.button);
        toFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FollowingIntent = new Intent(getApplicationContext(), Following.class);
                startActivity(FollowingIntent);
            }
        });
    }
}
