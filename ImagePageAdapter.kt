package com.project.swipeimages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.parse.*
import java.io.ByteArrayOutputStream
import java.util.ArrayList

class ImagePagerAdapter(
        val context: Context,
        private val imageResourceList: List<Uri>
) : PagerAdapter() {

    override fun isViewFromObject(view: View, obj: Any) = view == obj as ImageView

    override fun getCount(): Int = imageResourceList.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any =
            ImageView(context).apply {
                setImageURI(imageResourceList[position])
                scaleType = ImageView.ScaleType.CENTER_CROP
            }.also {
                (container as VerticalViewPager).addView(it)
            }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) =
            (container as VerticalViewPager).removeView(obj as ImageView)

    companion object {

        fun getImageUriFromBitmap(inContext: Context, inImage: Bitmap): Uri {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
            return Uri.parse(path)
        }

        fun setupPageView(username: String, isFilteredByUser: Boolean, appContext: Context, thisContext: AppCompatActivity): ImagePagerAdapter {

            val adapter: ImagePagerAdapter

            val query = ParseQuery<ParseObject>("Image")
            if (isFilteredByUser) {
                query.whereEqualTo("username", username)
            }
            query.orderByDescending("createdAt")

            val images = ArrayList<Uri>()
            adapter = ImagePagerAdapter(thisContext, images)// <- this is initialized before the query is completed!

            query.findInBackground { objects, e ->
                if (e == null && objects.size > 0) {
                    for (`object` in objects) {
                        val file = `object`.get("image") as ParseFile

                        file.getDataInBackground { data, e ->
                            if (e == null && data != null) {
                                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                                images.add(getImageUriFromBitmap(appContext, bitmap))

                                notificationByPass(adapter)// because async
                            }
                        }
                    }
                }
            }
            return adapter
        }

        fun notificationByPass(adapter: ImagePagerAdapter) {
            adapter.notifyDataSetChanged()
        }
    }

}