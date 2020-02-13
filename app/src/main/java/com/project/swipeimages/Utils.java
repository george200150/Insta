package com.swipeimages;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;

public class Utils {
    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FORESLASH = "/";

    /**
     * Method that converts a Resource (integer value representing the unique id of the resource) to
     * a Uniform Resource Identifier. (however, a rather pretentious data type for this app...)
     * @param context - application context
     * @param resId - static resource from "R."
     * @return Uri of the resource
     */
    public static Uri resIdToUri(Context context, int resId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName()
                + FORESLASH + resId);
    }

    /**
     * Method that converts a Bitmap to a ParseFile so that it could be saved on the server.
     * @param bitmap - Bitmap image
     * @return ParseFile that encapsulates the Bitmap's information
     */
    public static ParseFile bitmapToParseFile(Bitmap bitmap){// could also input the name of the file...
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return new ParseFile("image.png", byteArray);
    }

    /*private fun getImageUriFromBitmap(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }*/
}
