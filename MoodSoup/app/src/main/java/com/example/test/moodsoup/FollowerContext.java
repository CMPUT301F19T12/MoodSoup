package com.example.test.moodsoup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
/**
 * FollowerContext
 * V1.1
 * 2019-11-07
 *
 * This is a context meant to be used by the follower list. It contains a button to delete that
 * follower.
 *
 *@author smayer
 *@author pspiers
 */
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
        //Enable long click so that it can be removed

        final String email = emails.get(position);
        final TextView emailView = view.findViewById(R.id.email);
        emailView.setText(email);

        return view;
    }
}