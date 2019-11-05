package com.example.test.moodsoup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FollowerContext extends ArrayAdapter<String> {
    private ArrayList<String> emails;
    private Context context;

    FollowerContext(@NonNull Context context, ArrayList<String> email) {
        super(context, 0, email);
        this.emails = email;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.activity_follower_context, parent, false);
        }
        View itemLayout = view;
        itemLayout.setLongClickable(true);

        final String email = emails.get(position);
        final TextView emailView = view.findViewById(R.id.email);
        emailView.setText(email);

        return view;
    }
}