package com.project.swipeimages;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


public class UserVerticalFeedActivity extends AppCompatActivity {

    ImagePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vertical_feed);
        ConstraintLayout rootview = findViewById(R.id.rootview);
        VerticalViewPager verticalViewPager = new VerticalViewPager(this);
        rootview.addView(verticalViewPager);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        setTitle(username + "'s Photos");

        adapter = ImagePagerAdapter.Companion.setupPageView(username, true, getApplicationContext(), this);
        verticalViewPager.setAdapter(adapter);
    }

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FORESLASH = "/";

    public static Uri resIdToUri(Context context, int resId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName()
                + FORESLASH + resId);
    }

}
