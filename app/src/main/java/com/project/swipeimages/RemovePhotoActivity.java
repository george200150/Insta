package com.swipeimages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class RemovePhotoActivity extends AppCompatActivity {

    RecyclerView listPhotosOfUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_photo);
        setTitle("Delete Photos");

        listPhotosOfUser = findViewById(R.id.listPhotosOfUser);
        String username = ParseUser.getCurrentUser().getUsername();
        RecyclerViewDeletableAdapter RVadapter = RecyclerViewDeletableAdapter.Companion.setupDeletablePageView(username, true, getApplicationContext(), this);
        listPhotosOfUser.setLayoutManager(new LinearLayoutManager(this));
        listPhotosOfUser.setAdapter(RVadapter);

        /**
         * Listener that when the item is clicked, triggers the collection of data from the item
         * (gets the objectId of the Image) and creates a confirmation window asking the user
         * whether to delete the photo, or not. If the positive button is clicked, then the
         * deletePhotoFromTheDatabase function is called to permanently delete the photo. Else, the
         * window is closed and the user will be able to navigate through the list of photos again.
         */
        listPhotosOfUser.addOnItemTouchListener(new DeletableRecyclerViewClickListener(getApplicationContext(), listPhotosOfUser, new DeletableRecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, int position) {
                ConstraintLayout rez = (ConstraintLayout) listPhotosOfUser.getLayoutManager().findViewByPosition(position);
                if (rez != null) {
                    //we have saved the (Image) Object ID from the server in the image's tag.
                    ImageView img = (ImageView) rez.getViewById(R.id.ivImage);
                    final String objectId = img.getTag().toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(RemovePhotoActivity.this);
                    builder.setTitle("DELETE");
                    builder.setMessage("Delete this photo?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deletePhotoFromTheDatabase(objectId);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Toast.makeText(RemovePhotoActivity.this, "We couldn't delete the photo :(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
                //pass
            }
        }));
    }


    /**
     * Method that creates a query and looks for the image to be deleted. When found, we delete it
     * from the server and notify the user. Also, we invalidate the currently loaded data from the
     * menus and we reset the RecyclerView responsible for showing the list of photos.
     * @param objectId - primary key of the Image from the server
     */
    private void deletePhotoFromTheDatabase(String objectId){
        ParseQuery<ParseObject> query = new ParseQuery<>("Image");
        query.whereEqualTo("objectId", objectId);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    //delete it for good
                    objects.get(0).deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(RemovePhotoActivity.this, "Your photo was successfully deleted!", Toast.LENGTH_LONG).show();
                                GlobalObserver.updateULA();
                                String username = ParseUser.getCurrentUser().getUsername();
                                RecyclerViewDeletableAdapter RVadapter = RecyclerViewDeletableAdapter.Companion.setupDeletablePageView(username, true, getApplicationContext(), RemovePhotoActivity.this);
                                listPhotosOfUser.setLayoutManager(new LinearLayoutManager(RemovePhotoActivity.this));
                                listPhotosOfUser.setAdapter(RVadapter);
                            } else {
                                Toast.makeText(RemovePhotoActivity.this, "We could not delete it :(", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    //failed to delete
                    Toast.makeText(RemovePhotoActivity.this, "Uh, Oh! We could not delete it :(", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}