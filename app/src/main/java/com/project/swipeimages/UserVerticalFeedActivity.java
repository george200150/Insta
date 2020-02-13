package com.swipeimages;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Field;
import java.util.List;


public class UserVerticalFeedActivity extends AppCompatActivity {

    ImagePagerAdapter adapter;
    VerticalViewPager verticalViewPager;

    TextView usernameText;
    Button buttonSubscribe;
    String profileUsername;
    ImageView profileImageView;
    ConstraintLayout rootview;
    boolean liked = true;


    /**
     * This method has three main parts: identifying the view, sending the like, sending the notif.
     *
     * 1. we get all the items of the ViewPager and look for the current one (which is for sure the
     * one we clicked on, because it is simply impossible to be otherwise as our view is always
     * focused on a single image), then when we find it, we get the useful data from it (which
     * represents its tag which is equal to the objectId of the server object, therefore making it
     * easy for us to find further information in the database) and save it into the objectId.
     *
     * 2. We query the list of Images from the database to find all (at most one, because objectId
     * is a PrimaryKey) the objects that have the specified objectId. Then, we get its list of
     * people (usernames) who liked it. If the current user's name is contained by the list, it
     * means that our Double Tap Gesture is meant to Unlike the photo. Otherwise, we Like the Image.
     * We insert/extract the current username into/from the list and save it in background.
     *
     * 3. While saving the list of people who liked the selected photo, we create a Notification
     * FROM the current user TO the user who the Image belongs to. Also, we include the liked photo,
     * in order to display aside the notification. (pointer to the image would have been better)
     * When finished, we notify the current user of his action. (like / unlike)
     */
    public void receivelikedNotification() {
        ParseUser user = ParseUser.getCurrentUser();
        final String username = user.getUsername();

        String objectId = "null";

        try {
            final int currentItem = verticalViewPager.getCurrentItem();
            for (int i = 0; i < verticalViewPager.getChildCount(); i++) {
                final View child = verticalViewPager.getChildAt(i);
                final ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) child.getLayoutParams();

                Field f = layoutParams.getClass().getDeclaredField("position"); //NoSuchFieldException

                f.setAccessible(true);
                int position = (Integer) f.get(layoutParams); //IllegalAccessException

                if (!layoutParams.isDecor && currentItem == position) {
                    //return child;
                    objectId = (String) child.getTag();
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseObject> getImageDataQuery = new ParseQuery<>("Image");
        getImageDataQuery.whereEqualTo("objectId", objectId);
        getImageDataQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0){
                    ParseObject data = objects.get(0);
                    List<String> likedBy = data.getList("likedBy");
                    if(likedBy.contains(username)) {
                        likedBy.remove(username);//unlike
                        liked = false;
                    }
                    else {
                        likedBy.add(username);
                        liked = true;
                    }
                    data.put("likedBy", likedBy);//like
                    final ParseFile image = (ParseFile) data.get("image");
                    data.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if(liked) {
                                    Toast.makeText(UserVerticalFeedActivity.this, "Photo Liked!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(UserVerticalFeedActivity.this, "Photo Unliked!", Toast.LENGTH_SHORT).show();
                                }
                                ParseObject notification = new ParseObject("Notification");
                                notification.put("receiverUsername",profileUsername);
                                notification.put("serderUsername",username);
                                notification.put("photo",image);
                                notification.saveInBackground();
                                GlobalObserver.updateULA();
                            } else {
                                Toast.makeText(UserVerticalFeedActivity.this, "There has been an issue appreciating the image :(", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                else{
                    Log.i("LIKE", "couldn't like photo :(");
                }
            }
        });
    }


    /**
     * This initialization method is called in order to gather all the items and resources, and also
     * to set additional info, such as Intent's title, specific text to the "SUBSCRIBE/UNSUBSCRIBE"
     * Button, and show the photos from the user's Feed (done using a static method from the View),
     * as well as the profile picture.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vertical_feed);
        profileImageView = findViewById(R.id.profileImageView);
        rootview = findViewById(R.id.rootview);
        verticalViewPager = new VerticalViewPager(this);
        rootview.addView(verticalViewPager);
        GlobalUVFAObserver.setUvfa(this);

        Intent intent = getIntent();
        profileUsername = intent.getStringExtra("username");
        setTitle(profileUsername + "'s Photos");

        usernameText = findViewById(R.id.usernameText);
        buttonSubscribe = findViewById(R.id.buttonSubscribe);
        usernameText.setText(profileUsername);
        if (ParseUser.getCurrentUser().getList("isFollowing").contains(profileUsername)) {
            buttonSubscribe.setText("UNSUBSCRIBE");
        } else {
            buttonSubscribe.setText("SUBSCRIBE");
        }

        adapter = ImagePagerAdapter.Companion.setupPageView(profileUsername, true, getApplicationContext(), this);
        verticalViewPager.setAdapter(adapter);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", profileUsername);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null && users.size() > 0) {
                    ParseUser user = users.get(0);
                    ParseFile file = (ParseFile) user.get("picture");
                    if (file != null) {
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    profileImageView.setImageBitmap(bitmap);
                                }
                            }
                        });
                    } else {
                        profileImageView.setImageResource(R.drawable.ic_profile);
                    }
                }
            }
        });
    }


    /**
     * This method handles the action of subscribing / unsubscribing. It sets the text of the Button
     * to its complement when clicked and updates the current user's list of people they follow.
     * It saves the user's info in the background and notifies the GlobalObserver about the change.
     * (in order to show the user in feed (and, for larger apps, it would be useful when trimming
     * notifications of spamming users or friends))
     */
    public void handleSubscription(View view) {
        if(this.buttonSubscribe.getText().equals("SUBSCRIBE")){
            //subscribe this user
            ParseUser.getCurrentUser().add("isFollowing", profileUsername);//could work better with list of pointers to their profiles (for larger apps, when username changes)
            buttonSubscribe.setText("UNSUBSCRIBE");
        }
        else{
            //unsubscribe this user
            ParseUser.getCurrentUser().getList("isFollowing").remove(profileUsername);//could work better with list of pointers
            List tempUsers = ParseUser.getCurrentUser().getList("isFollowing");
            ParseUser.getCurrentUser().remove("isFollowing");
            ParseUser.getCurrentUser().put("isFollowing",tempUsers);
            buttonSubscribe.setText("SUBSCRIBE");
        }
        ParseUser.getCurrentUser().saveInBackground();
        GlobalObserver.updateULA();
    }

}
