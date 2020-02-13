/*
package com.swipeimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;


public class UserFeedActivity extends AppCompatActivity {//not used anywhere

    ImageBitmapDirectAdapter adapter;
    TextView usernameText;
    Button buttonSubscribe;
    String profileUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
        Intent intent = getIntent();
        profileUsername = intent.getStringExtra("username");
        setTitle(profileUsername + "'s Photos");
        final ViewPager viewPager = findViewById(R.id.viewpager);
        ImageView profileImageView = findViewById(R.id.profileImageView);//TO DO: maybe not
        usernameText = findViewById(R.id.usernameText);
        buttonSubscribe = findViewById(R.id.buttonSubscribe);

        adapter = ImageBitmapDirectAdapter.setupPageView(profileUsername, true, getApplicationContext(), this);
        viewPager.setAdapter(adapter);

        usernameText.setText(profileUsername);

        if(ParseUser.getCurrentUser().getList("isFollowing").contains(profileUsername)){
            buttonSubscribe.setText("UNSUBSCRIBE");
        }
        else{
            buttonSubscribe.setText("SUBSCRIBE");
        }

    }

    public void handleSubscription(View view) {
        if(this.buttonSubscribe.getText().equals("SUBSCRIBE")){
            //subscribe this user
            ParseUser.getCurrentUser().add("isFollowing", profileUsername);//to do: could work better with list of pointers to their profiles (for larger apps, when username changes)
            buttonSubscribe.setText("UNSUBSCRIBE");
        }
        else{
            //unsubscribe this user
            ParseUser.getCurrentUser().getList("isFollowing").remove(profileUsername);//to do: could work better with list of pointers
            List tempUsers = ParseUser.getCurrentUser().getList("isFollowing");
            ParseUser.getCurrentUser().remove("isFollowing");
            ParseUser.getCurrentUser().put("isFollowing",tempUsers);
            buttonSubscribe.setText("SUBSCRIBE");
        }
        ParseUser.getCurrentUser().saveInBackground();
    }
}




*/
/*<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingTop="32dp"
    android:paddingBottom="32dp"
    android:background="@color/white"
    tools:context=".UserFeedActivity">

    <androidx.viewpager.widget.ViewPager
        android:id="@+id/viewpager"
        android:layout_width="378dp"
        android:layout_height="489dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintHorizontal_bias="0.484"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.681" />

    <Button
        android:id="@+id/buttonSubscribe"
        android:layout_width="121dp"
        android:layout_height="37dp"
        android:onClick="handleSubscription"
        android:text="Subscribe/Unsubscribe"
        app:layout_constraintBottom_toTopOf="@+id/viewpager"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/profileImageView"
        app:layout_constraintTop_toBottomOf="@+id/usernameText" />

    <ImageView
        android:id="@+id/profileImageView"
        android:layout_width="98dp"
        android:layout_height="98dp"
        android:layout_marginStart="16dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:srcCompat="@tools:sample/avatars" />

    <TextView
        android:id="@+id/usernameText"
        android:layout_width="149dp"
        android:layout_height="31dp"
        android:text="username"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/profileImageView"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>*/
