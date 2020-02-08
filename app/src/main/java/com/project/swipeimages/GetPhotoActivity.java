package com.project.swipeimages;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class GetPhotoActivity extends AppCompatActivity {

    EditText descriptionText;
    ImageView photoView;
    Bitmap bitmap;


    public void handlePost(View view){
        String description = descriptionText.getText().toString();

        /*Intent data = new Intent(); data.putExtra("description", description);
        data.putExtra("bitmap", bitmap); setResult(RESULT_OK,data);
        finish();*/// E/JavaBinder: !!! FAILED BINDER TRANSACTION !!!  (parcel size = 3686712)
        // DATASET TOO LARGE !!! (we will manage it here directly)
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();

        ParseFile file = new ParseFile("image.png", byteArray);

        ParseObject object = new ParseObject("Image");

        object.put("image", file);

        object.put("username", ParseUser.getCurrentUser().getUsername());


        object.put("description", description);

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(GetPhotoActivity.this, "Image has been shared!", Toast.LENGTH_SHORT).show();
                    GlobalObserver.updateAllInULA();
                    finish();
                } else {
                    Toast.makeText(GetPhotoActivity.this, "There has been an issue uploading the image :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void handleDiscard(View view){
        finish();
        Toast.makeText(GetPhotoActivity.this, "You discarded the photo.", Toast.LENGTH_SHORT).show();
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
        }
        else{
            finish();
        }

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {


            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                Log.i("Image Selected", "Good work");

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
        setContentView(R.layout.activity_get_photo);
        descriptionText = findViewById(R.id.descriptionText);
        photoView = findViewById(R.id.photoView);
        setTitle("Choose Photo");
        getPhoto();
    }


}
