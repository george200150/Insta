package com.swipeimages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private Context context;
    private ArrayList<View> imageViewResourceList;


    public ImageBitmapDirectAdapter(Context context, ArrayList<View> imageViewResourceList) {
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
        View iv;
        iv = this.imageViewResourceList.get(position);
        container.addView(iv);
        return iv;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public static void notificationByPass(ImageBitmapDirectAdapter adapter){
        adapter.notifyDataSetChanged();
    }

    public static ImageBitmapDirectAdapter setupPageView(String username, boolean isFilteredByUser, final Context appContext, final AppCompatActivity thisContext){

        final ImageBitmapDirectAdapter adapter;

        ParseQuery<ParseObject> query = new ParseQuery<>("Image");
        if(isFilteredByUser){
            query.whereEqualTo("username", username);
        }
        query.orderByDescending("createdAt");

        final ArrayList<View> images = new ArrayList<>();
        adapter = new ImageBitmapDirectAdapter(thisContext, images);// <- this is initialized before the query is completed!

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {
                        ParseFile file = (ParseFile) object.get("image");
                        final String objectId = object.getObjectId();
                        List<String> likes = object.getList("likedBy");
                        final int likeCount = likes.size();

                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,data.length);
                                    ImageView imageView = new ImageView(appContext);
                                    imageView.setImageBitmap(bitmap);
                                    imageView.setTag(objectId);
                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                                    ViewGroup view = new LinearLayout(thisContext);
                                    LinearLayout picLL = new LinearLayout(thisContext);
                                    picLL.setOrientation(LinearLayout.VERTICAL);
                                    view.addView(picLL);

                                    picLL.addView(imageView);

                                    TextView likeCountTextView = new TextView(thisContext);
                                    likeCountTextView.setText(likeCount + " Likes"); // optional singular/plural cases...
                                    likeCountTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                    picLL.addView(likeCountTextView);

                                    images.add(view);
                                    ImageBitmapDirectAdapter.notificationByPass(adapter);
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