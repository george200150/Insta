package com.project.swipeimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;



public class UserFeedActivity extends AppCompatActivity {

    ImageBitmapDirectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        setTitle(username + "'s Photos");
        final ViewPager viewPager = findViewById(R.id.viewpager);

        adapter = ImageBitmapDirectAdapter.setupPageView(username, true, getApplicationContext(), this);
        viewPager.setAdapter(adapter);
    }

}
