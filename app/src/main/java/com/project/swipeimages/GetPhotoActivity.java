package com.swipeimages;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.IOException;


public class GetPhotoActivity extends AppCompatActivity {

    EditText descriptionText;
    ImageView photoView;
    Bitmap bitmap;


    /**
     * (i tried to pass through the Intent more information than possible, so I had to change my
     * implementation of sending the Activity through Intent (as Serializable) object to using an
     * observer that would manage all the work for us.
     * This could be better when data has to support more complex analysis, rather than sending it
     * directly, without verification. (e.g. spam filters, smart notifications))
     *
     * We create a ParseObject that is an Image. We set all the required information to this object,
     * then we save it in background, notifying the user of the result. After we press the "POST"
     * Button, we finish our Activity and send us back to the main menu.
     */
    public void handlePost(View view){// could do better with delegate
        String description = descriptionText.getText().toString();

        /*Intent data = new Intent(); data.putExtra("description", description);
        data.putExtra("bitmap", bitmap); setResult(RESULT_OK,data);
        finish();*/// E/JavaBinder: !!! FAILED BINDER TRANSACTION !!!  (parcel size = 3686712)
        // DATASET TOO LARGE !!! (we will manage it here directly)
        ParseFile file = Utils.bitmapToParseFile(bitmap);

        ParseObject object = new ParseObject("Image");
        object.put("image", file);
        object.put("username", ParseUser.getCurrentUser().getUsername());
        object.put("description", description);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(GetPhotoActivity.this, "Image has been shared!", Toast.LENGTH_SHORT).show();
                    GlobalObserver.updateULA();
                    finish();
                } else {
                    Toast.makeText(GetPhotoActivity.this, "There has been an issue uploading the image :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    /**
     * After we selected the photo, we can discard it, by pressing the "DISCARD" Button, which will
     * finish our Activity and send us back to the main menu.
     */
    public void handleDiscard(View view){
        finish();
        Toast.makeText(GetPhotoActivity.this, "You discarded the photo.", Toast.LENGTH_SHORT).show();
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
        }
        else{
            finish();
        }

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
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
