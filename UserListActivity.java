package com.project.swipeimages;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class UserListActivity extends AppCompatActivity {

    ImageBitmapDirectAdapter adapter;
    ImageBitmapDirectAdapter allAdapter;

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = null;
        if (data != null) {
            selectedImage = data.getData();
        }

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                Log.i("Image Selected", "Good work");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile("image.png", byteArray);

                ParseObject object = new ParseObject("Image");

                object.put("image", file);

                object.put("username", ParseUser.getCurrentUser().getUsername());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(UserListActivity.this, "Image has been shared!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserListActivity.this, "There has been an issue uploading the image :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        final ViewPager allViewPager = findViewById(R.id.alliewpager);

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
                    allViewPager.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    notificationsText.setVisibility(View.INVISIBLE);
                    return true;
                } else if (item.getItemId() == R.id.item_discover) {//TODO: could do late init..
                    viewPager.setVisibility(View.INVISIBLE);
                    allViewPager.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    notificationsText.setVisibility(View.INVISIBLE);
                    bnv.removeBadge(R.id.item_discover);
                    return true;
                } else if (item.getItemId() == R.id.item_notifications) {
                    bnv.removeBadge(R.id.item_notifications);
                    viewPager.setVisibility(View.INVISIBLE);
                    allViewPager.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    notificationsText.setVisibility(View.VISIBLE);
                    return true;
                } else if (item.getItemId() == R.id.item_profile) {

                    bnv.removeBadge(R.id.item_profile);//TODO: could do late init..
                    viewPager.setVisibility(View.VISIBLE);
                    allViewPager.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    notificationsText.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return true;
                }
            }
        });

        //<Discover>
        allAdapter = ImageBitmapDirectAdapter.setupPageView(null, false, getApplicationContext(), this);
        allViewPager.setAdapter(allAdapter);
        //</Discover>

        //<Profile>
        String username = ParseUser.getCurrentUser().getUsername();
        adapter = ImageBitmapDirectAdapter.setupPageView(username, true, getApplicationContext(), this);
        viewPager.setAdapter(adapter);
        //</Profile>

        //<Feed>
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
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
