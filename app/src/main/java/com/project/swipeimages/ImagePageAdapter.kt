package com.swipeimages

import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.parse.*
import java.util.ArrayList

class ImagePagerAdapter(
        val context: Context,
        private val imageResourceList: List<ImageData>
) : PagerAdapter() {

    override fun isViewFromObject(view: View, obj: Any) = view == obj as ImageView

    override fun getCount(): Int = imageResourceList.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any =
            ImageView(context).apply {
                setImageBitmap(imageResourceList[position].resource)
                scaleType = ImageView.ScaleType.CENTER_CROP
                tag = imageResourceList[position].title
            }.also {
                (container as VerticalViewPager).addView(it)
            }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) =
            (container as VerticalViewPager).removeView(obj as ImageView)

    companion object {

        fun setupPageView(username: String, isFilteredByUser: Boolean, appContext: Context, thisContext: AppCompatActivity): ImagePagerAdapter {
            val adapter: ImagePagerAdapter
            val images = ArrayList<ImageData>()
            adapter = ImagePagerAdapter(thisContext, images)// <- this is initialized before the query is completed!

            val query = ParseQuery<ParseObject>("Image")
            if (isFilteredByUser) {
                query.whereEqualTo("username", username)
            }
            query.orderByDescending("createdAt")
            query.findInBackground { objects, e ->
                if (e == null && objects.size > 0) {
                    for (`object` in objects) {
                        val file = `object`.get("image") as ParseFile
                        val photoId = `object`.objectId as String
                        file.getDataInBackground { data, ee ->
                            if (ee == null && data != null) {
                                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                                val imd = ImageData(photoId, "-", bitmap)
                                images.add(imd)
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