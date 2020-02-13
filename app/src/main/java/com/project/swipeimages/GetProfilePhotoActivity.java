package com.swipeimages;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class GetProfilePhotoActivity extends AppCompatActivity {

    ImageView profilePhotoView;
    Bitmap bitmap;


    /**
     * We create a ParseFile from the bitmap we received from the gallery. We get the current user
     * and we set their profile picture to the new image, then we save the user's information in
     * background, notifying the user of the result. After we press the "POST" Button, we finish
     * our Activity and send us back to the user's profile.
     */
    public void handlePost(View view) {// could do better with delegate
        ParseFile file = Utils.bitmapToParseFile(bitmap);
        ParseUser user = ParseUser.getCurrentUser();
        user.put("picture", file);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(GetProfilePhotoActivity.this, "profile picture uploaded!", Toast.LENGTH_SHORT).show();
                    GlobalObserver.invalidateProfile();
                    finish();
                } else {
                    Toast.makeText(GetProfilePhotoActivity.this, "There has been an issue uploading the image :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    /**
     * After we selected the photo, we can discard it, by pressing the "DISCARD" Button, which will
     * finish our Activity and send us back to the user's profile.
     */
    public void handleDiscard(View view) {
        finish();
        Toast.makeText(GetProfilePhotoActivity.this, "You discarded the photo.", Toast.LENGTH_SHORT).show();
    }


    /**
     * Creates new system specialised intent in order to open gallery and get a photo.
     * The method will (indirectly) return a result having the specific requestCode 1.
     * The result represents a selectedImage, that will be processed in the onActivityResult method.
     */
    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }


    /**
     * When the gallery activity finished successfully, the data can be null (no photo selected)
     * or an actual image, that we have to convert to a Bitmap. Then, we set the image visible to
     * the user, in the profilePhotoView.
     * @param requestCode - specific code requested in order to process the result (its value is 1)
     * @param resultCode - the actual returned code by the Activity
     * @param data - returned data (here, it is considered to be an image, if resultCode == 1)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = null;
        if (data != null) {
            selectedImage = data.getData();
        } else {
            finish();
        }

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                profilePhotoView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_profile_photo);
        profilePhotoView = findViewById(R.id.profilePhotoView);
        setTitle("Choose Photo");
        getPhoto();
    }
}
