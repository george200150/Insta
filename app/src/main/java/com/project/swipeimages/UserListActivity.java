package com.swipeimages;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * Enum Type representing the three dot menu choices available to the user.
 * The types are used when deciding which menu the user is currently viewing.
 */
enum MenuType{
    FEED,
    DISCOVER,
    NOTIFICATIONS,
    PROFILE
}


public class UserListActivity extends AppCompatActivity {

    MenuType menuType = MenuType.FEED;// default startup screen
    BottomNavigationView bnv;
    RecyclerView rvNotifications;

    private boolean isSetFeed;
    private boolean isSetDiscover;
    private boolean isSetNotifications;
    private boolean isSetProfile;
    private boolean firstTime;

    ArrayList<String> users = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    ArrayAdapter autoCompleteArrayAdapter;

    ImageBitmapDirectAdapter IBDadapter;
    RecyclerViewAdapter RVadapter;
    TextView profileText;
    AutoCompleteTextView autoCompleteSearchBarView;
    ImageView profileEditImageView;
    int notifNumber;


    /**
     * Method that initializes the Activity by gathering the resources, setting up the listener for
     * the autoComplete, calling the functions responsible for the menus setup, setting up the
     * GlobalObserver, setting the "isSet_" attributes so that the programme knows only the startup
     * menu was initialized. (this boosts up the start speed but increases the waiting time when
     * switching between the uninitialized menus)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        notifNumber = 0;
        firstTime = true;
        setTitle("User Feed");
        final ViewPager viewProfilePager = findViewById(R.id.viewProfilePager);
        final RecyclerView rvItems = findViewById(R.id.rvItems);
        final ListView feedListView = findViewById(R.id.listView);
        bnv = findViewById(R.id.bottomNavigationView);
        final TextView notificationsText = findViewById(R.id.notificationsText);
        profileEditImageView = findViewById(R.id.profileEditImageView);
        profileText = findViewById(R.id.profileText);
        autoCompleteSearchBarView = findViewById(R.id.autoCompleteSearchBarView);
        autoCompleteSearchBarView.setThreshold(1);
        autoCompleteSearchBarView.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
            @Override
            public void onDismiss() {
                final Intent intent = new Intent(getApplicationContext(), UserVerticalFeedActivity.class);
                intent.putExtra("username", autoCompleteSearchBarView.getText().toString());

                ParseQuery<ParseUser> checkUserQuery = ParseUser.getQuery();
                checkUserQuery.whereEqualTo("username", autoCompleteSearchBarView.getText().toString());
                checkUserQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> users, ParseException e) {
                        if (e == null && users.size() > 0) {
                            startActivity(intent);
                        } else {
                            Toast.makeText(UserListActivity.this, "Could not find user :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        GlobalObserver.setULA(this);
        isSetFeed = true;
        isSetDiscover = false;
        isSetNotifications = false;
        isSetProfile = false;


        /**
         * Called when an item in the bottom navigation menu is selected.
         * This switch-case inside the listener is supposed to make visible to the user only the
         * selected menu's view and set them up if not already set up. (according to the "isSet"-s)
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
                    if(!isSetFeed)
                        setUpFeed();
                    menuType = MenuType.FEED;
                    bnv.removeBadge(R.id.item_feed);
                    profileEditImageView.setVisibility(View.INVISIBLE);
                    profileText.setVisibility(View.INVISIBLE);
                    viewProfilePager.setVisibility(View.INVISIBLE);
                    rvItems.setVisibility(View.INVISIBLE);
                    autoCompleteSearchBarView.setVisibility(View.VISIBLE);
                    feedListView.setVisibility(View.VISIBLE);
                    notificationsText.setVisibility(View.INVISIBLE);
                    rvNotifications.setVisibility(View.INVISIBLE);
                    return true;
                } else if (item.getItemId() == R.id.item_discover) {
                    if(!isSetDiscover)
                        setUpDiscover();
                    bnv.removeBadge(R.id.item_discover);
                    menuType = MenuType.DISCOVER;
                    profileEditImageView.setVisibility(View.INVISIBLE);
                    profileText.setVisibility(View.INVISIBLE);
                    viewProfilePager.setVisibility(View.INVISIBLE);
                    rvItems.setVisibility(View.VISIBLE);
                    autoCompleteSearchBarView.setVisibility(View.INVISIBLE);
                    feedListView.setVisibility(View.INVISIBLE);
                    notificationsText.setVisibility(View.INVISIBLE);
                    rvNotifications.setVisibility(View.INVISIBLE);
                    return true;
                } else if (item.getItemId() == R.id.item_notifications) {
                    if(!isSetNotifications)
                        setUpNotifications();
                    menuType = MenuType.NOTIFICATIONS;
                    bnv.removeBadge(R.id.item_notifications);
                    profileEditImageView.setVisibility(View.INVISIBLE);
                    profileText.setVisibility(View.INVISIBLE);
                    autoCompleteSearchBarView.setVisibility(View.INVISIBLE);
                    viewProfilePager.setVisibility(View.INVISIBLE);
                    rvItems.setVisibility(View.INVISIBLE);
                    feedListView.setVisibility(View.INVISIBLE);
                    notificationsText.setVisibility(View.VISIBLE);
                    if (notifNumber > 0) {
                        rvNotifications.setVisibility(View.VISIBLE);
                        View notificationsText = findViewById(R.id.notificationsText);
                        notificationsText.setVisibility(View.INVISIBLE);
                    }
                    else{
                        rvNotifications.setVisibility(View.INVISIBLE);
                        View notificationsText = findViewById(R.id.notificationsText);
                        notificationsText.setVisibility(View.VISIBLE);
                    }
                    return true;
                } else if (item.getItemId() == R.id.item_profile) {
                    if(!isSetProfile)
                        setUpProfile();
                    menuType = MenuType.PROFILE;
                    bnv.removeBadge(R.id.item_profile);
                    profileEditImageView.setVisibility(View.VISIBLE);
                    profileText.setVisibility(View.VISIBLE);
                    autoCompleteSearchBarView.setVisibility(View.INVISIBLE);
                    viewProfilePager.setVisibility(View.VISIBLE);
                    rvItems.setVisibility(View.INVISIBLE);
                    feedListView.setVisibility(View.INVISIBLE);
                    notificationsText.setVisibility(View.INVISIBLE);
                    rvNotifications.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return true;
                }
            }
        });
        setUpFeed();// home screen. must init.
        setUpNotifications();// CANNOT BE - lazy init
    }


    /**
     * Method that sets up the Feed.
     * It loads the list of followed users and displays it to the user.
     * Also, it adds all the names of the users in the autoCompleteSearchBar, in order to be able to
     * access anyone's profile when ENTER is pressed.
     */
    public void setUpFeed() {
        isSetFeed = true;
        final ListView listView = findViewById(R.id.listView);
        users.clear();
        final ArrayList<String> followedUsers = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), UserVerticalFeedActivity.class);
                intent.putExtra("username", followedUsers.get(i));
                startActivity(intent);

            }
        });

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, followedUsers);
        autoCompleteArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, users);
        listView.setAdapter(arrayAdapter);
        autoCompleteSearchBarView.setAdapter(autoCompleteArrayAdapter);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseUser user : objects) {
                        users.add(user.getUsername());// users are all the users existing in the database
                    }
                    arrayAdapter.notifyDataSetChanged();
                    for (String username : users) {
                        ParseUser user = ParseUser.getCurrentUser();
                        List<String> isFollowing = user.getList("isFollowing");
                        if(isFollowing != null)
                            if (isFollowing.contains(username)) {
                                followedUsers.add(username);// followed users are the users followed by the current user
                                arrayAdapter.notifyDataSetChanged();
                            }
                    }
                }
            }
        });
    }


    /**
     * Method that deletes the notification in the list with the specified tag.
     * It also resets the notification menu, to show the updated list of notifications, if any.
     * @param tag - objectId from the database of the Notification (to find it easier)
     */
    public void deleteNotif(String tag) {
        ParseQuery<ParseObject> notifQuery = new ParseQuery<>("Notification");
        notifQuery.whereEqualTo("objectId", tag);
        notifQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> notifs, ParseException e) {
                if (e == null && notifs.size() > 0) {
                    notifs.get(0).deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                Log.i("DELETED NOTIF", "Success!");
                                //invalidate
                                setUpNotifications();
                            }
                            else{
                                Log.i("DELETED NOTIF", "Error...");
                            }
                        }
                    });
                }
                else{
                    Log.i("DELETED NOTIF", "Could not find notification!");
                }
            }
        });
    }


    /**
     * Method that sets up the Discover.
     * It loads all the pictures of all the users in a RecyclerView that will show some adds, the
     * same as real apps do.
     */
    public void setUpDiscover(){
        isSetDiscover = true;
        final RecyclerView rvItems = findViewById(R.id.rvItems);
        String username = ParseUser.getCurrentUser().getUsername();
        RVadapter = RecyclerViewAdapter.Companion.setupPageView(username, true, true,  getApplicationContext(), this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvItems.setLayoutManager(manager);

        rvItems.setAdapter(RVadapter);
    }


    /**
     * Method that sets up the Notifications.
     * (Cannot be lazy init because when the app is opened, the user must know the notifications.)
     * It loads the list of notifications, creates a Red Badge with the number of Notifications on
     * it, to attract the user's attention, or shows only the "no notifications now" text.
     */
    public void setUpNotifications() { // notification icon cannot be lazy init !!!
        isSetNotifications = true;
        rvNotifications = findViewById(R.id.notifRLV);
        RecyclerViewNotificationsAdapter rvnAdapter = RecyclerViewNotificationsAdapter.Companion.setupNotifPageView(ParseUser.getCurrentUser().getUsername(), true, getApplicationContext(), this);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(rvnAdapter);

        ParseQuery<ParseObject> notifQuery = new ParseQuery<>("Notification");
        notifQuery.whereEqualTo("receiverUsername", ParseUser.getCurrentUser().getUsername());
        notifQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> notifs, ParseException e) {
                if(e == null) {
                    notifNumber = notifs.size();
                    if (notifNumber > 0) {
                        BadgeDrawable messageBadge = bnv.getOrCreateBadge(R.id.item_notifications);
                        messageBadge.setBadgeTextColor(getColor(R.color.white));
                        messageBadge.setNumber(notifNumber);
                    }
                    else{
                        if(! firstTime) {
                            rvNotifications.setVisibility(View.INVISIBLE);
                            View notificationsText = findViewById(R.id.notificationsText);
                            notificationsText.setVisibility(View.VISIBLE);
                        }
                        else{
                            firstTime = false;
                        }
                    }
                }
            }
        });
    }


    /**
     * Method that sets up the Profile.
     * It loads the current user's posted images, the profile picture and their username.
     */
    public void setUpProfile() {
        isSetProfile = true;
        profileText.setText(ParseUser.getCurrentUser().getUsername());
        final ViewPager viewPager = findViewById(R.id.viewProfilePager);
        String username = ParseUser.getCurrentUser().getUsername();
        IBDadapter = ImageBitmapDirectAdapter.setupPageView(username, true, getApplicationContext(), this);
        viewPager.setAdapter(IBDadapter);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null && users.size() > 0) {
                    ParseObject user = users.get(0);
                    ParseFile file = (ParseFile) user.get("picture");
                    if(file != null) {
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    profileEditImageView.setImageBitmap(bitmap);
                                }
                            }
                        });
                    }
                    else{
                        profileEditImageView.setImageResource(R.drawable.ic_profile);
                    }
                } else {
                    profileEditImageView.setImageResource(R.drawable.ic_profile);
                }
            }
        });
    }


    /**
     * Method that is called by the GlobalObserver, in order to notify any important changes that
     * would make the onscreen data out-of-date and would require further reloading from the server.
     * the "isSet___" attributes are checked every time the user changes menus, in order to load
     * only the necessary data when it is needed. (therefore, helps with the lazy initialization)
     */
    public void invalidate(){//used for invalidating loaded data from lists when an update is made
        isSetFeed = false;
        isSetDiscover = false;
        isSetNotifications = false;
        isSetProfile = false;

        if( menuType.equals(MenuType.FEED)){
            setUpFeed();
        }
        else if( menuType.equals(MenuType.DISCOVER)){
            setUpDiscover();
        }
        else if( menuType.equals(MenuType.NOTIFICATIONS)){
            setUpNotifications();
        }
        else if( menuType.equals(MenuType.PROFILE)){
            setUpProfile();
        }

    }


    /**
     * Method that creates a new Intent and opens a window for us to select from gallery a photo.
     * This has effect on the user's profile photo.
     */
    public void handleEditProfilePhoto(View view){ // handleEditProfilePhoto and getPhoto could share same class but the handlePost would be a delegate...
        Intent intent = new Intent(this, GetProfilePhotoActivity.class);
        startActivity(intent);
    }


    /**
     * Method that creates a new Intent and opens a window for us to select from gallery a photo.
     * This has effect on the user's feed photos.
     */
    public void getPhoto() {
        Intent intent = new Intent(getApplicationContext(), GetPhotoActivity.class);
        startActivity(intent);
    }


    /**
     * Method that checks if the user allowed the app to get access to Storage. If confirmed, it
     * will take a photo and further process it.
     * @param requestCode - theoretical requested code
     * @param permissions - actual requested code
     * @param grantResults - permissions granted by the user
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }


    /**
     * Method that sets up the three dots menu in the upper-right corner of the screen.
     * @param menu - the menu we created and put in the resources folder
     * @return the value of the super method
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.share_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    /**
     * This method manages all the selection cases of items from the three dots menu.
     * 1.share = Creates a new window, in order to ask permissions to read the storage if the
     * permission has not already been granted. Else, it opens the gallery straightaway.
     *
     * 2.logout = Logs out the user and redirects them to the login screen.
     *
     * 3.rmPhoto = Creates a new Intent and opens the RemovePhotoActivity, where we can delete pics.
     *
     * @param item - selected menu item
     * @return the value of the super method (we focus on the function calls we make)
     */
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
        else if (item.getItemId() == R.id.rmPhoto) {
            Intent intent = new Intent(getApplicationContext(), RemovePhotoActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
