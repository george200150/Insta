package com.project.swipeimages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class ImageBitmapDirectAdapter extends PagerAdapter {

    Context context;
    private ArrayList<ImageView> imageViewResourceList;


    public ImageBitmapDirectAdapter(Context context, ArrayList<ImageView> imageViewResourceList) {
        this.context = context;
        this.imageViewResourceList = imageViewResourceList;
    }

    @Override
    public int getCount() {
        return imageViewResourceList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView iv;
        iv = this.imageViewResourceList.get(position);
        container.addView(iv);
        return iv;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }

    public static void notificationByPass(ImageBitmapDirectAdapter adapter){
        adapter.notifyDataSetChanged();
    }

    public static ImageBitmapDirectAdapter setupPageView(String username, boolean isFilteredByUser, final Context appContext, AppCompatActivity thisContext){

        final ImageBitmapDirectAdapter adapter;

        ParseQuery<ParseObject> query = new ParseQuery<>("Image");
        if(isFilteredByUser){
            query.whereEqualTo("username", username);
        }
        query.orderByDescending("createdAt");

        final ArrayList<ImageView> images = new ArrayList<>();
        adapter = new ImageBitmapDirectAdapter(thisContext, images);// <- this is initialized before the query is completed!

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {
                        ParseFile file = (ParseFile) object.get("image");

                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,data.length);
                                    ImageView imageView = new ImageView(appContext);
                                    imageView.setImageBitmap(bitmap);
                                    images.add(imageView);

                                    ImageBitmapDirectAdapter.notificationByPass(adapter);// <- this will help the viewPager see the updated content (because async bruh..)
                                }
                            }
                        });
                    }
                }
            }
        });
        return adapter;
    }
}