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


    /**
     * method used to instantiate the view with the data unpacked from the ImageData object, each
     * time the notifyDataSetChanged method from the adapter is called
     * It sets to the view the tag of the image of the objectId from the server (so that we can find
     * the image easier when we later need to), the image itself and its crop style when needed.
     * The CENTER_CROP scale will not resize the image, but it keeps the aspect ratios intact.
     * However, in order to fit the parent's dimensions the image's edges may not be shown.
     * (The FIT_XY scale will resize the image without keeping the aspect ratios so that it fits the
     * parent's dimensions.)
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any =
            ImageView(context).apply {
                setImageBitmap(imageResourceList[position].resource)
                //scaleType = ImageView.ScaleType.FIT_XY
                scaleType = ImageView.ScaleType.CENTER_CROP
                tag = imageResourceList[position].title
            }.also {
                (container as VerticalViewPager).addView(it)
            }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) =
            (container as VerticalViewPager).removeView(obj as ImageView)

    companion object {

        /**
         * static method used to setup the adapter of the VerticalViewPager
         * It creates the array of data, the adapter itself and queries the database, in order to
         * setup the useful information to each View using a ImageData structure to gather it
         * all in a single object that we will unpack in the instantiateItem method.
         * Each time we create a new item and send it to the ViewPager, we must consider
         * notifying the adapter that its data set has changed.
         */
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