package com.project.swipeimages;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class UserListActivity extends AppCompatActivity {

    ImageBitmapDirectAdapter IBDadapter;
    RecyclerViewAdapter RVadapter;

    public void getPhoto() {
        Intent intent = new Intent(getApplicationContext(), GetPhotoActivity.class);
        //intent.putExtra("IBDAdapter", (Serializable) IBDadapter);// Now implements serializable !!!
        //intent.putExtra("RVAdapter", (Serializable) RVadapter);//I had to make Kotlin class implement serializable !!!

        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.share_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.share) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                getPhoto();
            }
        } else if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("User Feed");

        //<Notifications>
        final TextView notificationsText = findViewById(R.id.notificationsText);
        //</Notifications>

        final ViewPager viewPager = findViewById(R.id.viewpager);
        final RecyclerView rvItems = findViewById(R.id.rvItems);

        final ListView listView = findViewById(R.id.listView);
        final ArrayList<String> usernames = new ArrayList<>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);

        final BottomNavigationView bnv = findViewById(R.id.bottomNavigationView);
        BadgeDrawable messageBadge = bnv.getOrCreateBadge(R.id.item_notifications);
        messageBadge.setBadgeTextColor(getColor(R.color.white));
        //messageBadge.setNumber(1);

        /**
         * Called when an item in the bottom navigation menu is selected.
         *
         * @param item The selected item
         * @return true to display the item as the selected item and false if the item should not be
         *     selected. Consider setting non-selectable items as disabled preemptively to make them
         *     appear non-interactive.
         */
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.item_feed) {
                    viewPager.setVisibility(View.INVISIBLE);
                    rvItems.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    notificationsText.setVisibility(View.INVISIBLE);
                    return true;
                } else if (item.getItemId() == R.id.item_discover) {//TODO: could do late init..
                    viewPager.setVisibility(View.INVISIBLE);
                    rvItems.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    notificationsText.setVisibility(View.INVISIBLE);
                    bnv.removeBadge(R.id.item_discover);
                    return true;
                } else if (item.getItemId() == R.id.item_notifications) {
                    bnv.removeBadge(R.id.item_notifications);
                    viewPager.setVisibility(View.INVISIBLE);
                    rvItems.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    notificationsText.setVisibility(View.VISIBLE);
                    return true;
                } else if (item.getItemId() == R.id.item_profile) {

                    bnv.removeBadge(R.id.item_profile);//TODO: could do late init..
                    viewPager.setVisibility(View.VISIBLE);
                    rvItems.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    notificationsText.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return true;
                }
            }
        });

        //<Discover>
        RVadapter = RecyclerViewAdapter.Companion.setupPageView("null", false, getApplicationContext(), this);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(RVadapter);
        //</Discover>

        //<Profile>
        String username = ParseUser.getCurrentUser().getUsername();
        IBDadapter = ImageBitmapDirectAdapter.setupPageView(username, true, getApplicationContext(), this);
        viewPager.setAdapter(IBDadapter);
        //</Profile>

        //<Feed>
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), UserVerticalFeedActivity.class);
                intent.putExtra("username", usernames.get(i));
                startActivity(intent);

            }
        });
        ParseQuery<ParseUser> queryGetData = ParseUser.getQuery();
        queryGetData.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        queryGetData.addAscendingOrder("username");
        queryGetData.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseUser user : objects) {
                            usernames.add(user.getUsername());
                        }
                        listView.setAdapter(arrayAdapter);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
        //</Feed>
    }
}


/*
<androidx.viewpager.widget.ViewPager
        android:visibility="invisible"
        android:id="@+id/alliewpager"
        android:layout_width="378dp"
        android:layout_height="489dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintHorizontal_bias="0.484"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.363" />*/
