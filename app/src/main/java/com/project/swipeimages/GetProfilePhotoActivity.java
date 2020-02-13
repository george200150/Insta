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

    ImageView photoView;
    Bitmap bitmap;


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

    public void handleDiscard(View view) {
        finish();
        Toast.makeText(GetProfilePhotoActivity.this, "You discarded the photo.", Toast.LENGTH_SHORT).show();
    }

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
        } else {
            finish();
        }

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                photoView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_profile_photo);
        photoView = findViewById(R.id.profilePhotoView);
        setTitle("Choose Photo");
        getPhoto();
    }
}
