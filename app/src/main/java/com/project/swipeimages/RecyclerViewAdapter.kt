package com.swipeimages

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.ArrayList

//import java.io.Serializable

class RecyclerViewAdapter(private val context: Context, private val imageDataList: List<ImageData>) : /*Serializable,*/ RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var curImageDataIndex = 0
    // context could be later used

    /**
     * initialize the viewHolder with our own
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            in 0..2 -> ImageViewHolder(inflateResource(parent, R.layout.item_image_small))
            3 -> ImageViewHolder(inflateResource(parent, R.layout.item_image_large))
            else -> AdViewHolder(inflateResource(parent, R.layout.item_ad))
        }
    }


    /**
     * method used for binding the holder to a certain position in the list and show it to the user
     * It distinguishes three View Types: the small ones, the big one and the add view.
     * The first three view will be small, followed by a bigger picture, than an add.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType in 0..3) {// equivalent of 0 <= i && i <= 3
            val imageData = imageDataList[curImageDataIndex]
            (holder as ImageViewHolder).ivImage.setImageBitmap(imageData.resource)
            holder.tvTitle.text = imageData.title
            holder.tvDescription.text = imageData.description
        }
        if (curImageDataIndex < imageDataList.size - 1) {
            curImageDataIndex++
        } else {
            curImageDataIndex = 0
        }
    }

    override fun getItemCount(): Int {
        return imageDataList.size
    }

    /**
     * sets the rule of distinguishing the view types
     */
    override fun getItemViewType(position: Int): Int {
        return position % 5
    }


    /**
     * method used for initializing the holders' data and listeners
     */
    private fun inflateResource(parent: ViewGroup, resourceId: Int): View {
        return LayoutInflater.from(parent.context).inflate(resourceId, parent, false)
    }


    /**
     * custom inner class that inherits from the ViewHolder class
     * This class will receive all the attributes we need to make a complete item in the list, as we
     * intended to. It will be used for both small and big pictures, as they both share the same
     * properties, only the layout will differ.
     */
    internal inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivImage: ImageView
        var tvTitle: TextView
        var tvDescription: TextView

        init {
            ivImage = itemView.findViewById(R.id.ivImage)
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvDescription = itemView.findViewById(R.id.tvDescription)
        }
    }


    /**
     * custom inner class that inherits from the ViewHolder class
     * This class will receive only a single attribute, which is an ImageView, as its only purpose
     * is to display an add. The resource of the add is static, as we created and put it in the
     * drawable folder as a png image. (not that sophisticated)
     */
    internal inner class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivAd: ImageView

        init {
            ivAd = itemView.findViewById(R.id.ivAd)
        }
    }

    companion object{

        /**
         * static method used to setup the adapter of the RecyclerView
         * It creates the array of data, the adapter itself and queries the database, in order to
         * setup the useful information to each View using a ImageData structure to gather it
         * all in a single object that we will unpack in the onBindViewHolder method.
         * Each time we create a new item and send it to the RecyclerView, we must consider
         * notifying the adapter that its data set has changed.
         */
        fun setupPageView(username: String, isFilteredByUser: Boolean, isInvertedSearch: Boolean, appContext: Context, thisContext: AppCompatActivity): RecyclerViewAdapter {

            var position = 0

            val adapter: RecyclerViewAdapter

            val query = ParseQuery<ParseObject>("Image")
            if (isFilteredByUser and !isInvertedSearch) {//look only for the given user
                query.whereEqualTo("username", username)
            }
            else if(isFilteredByUser and isInvertedSearch){//search for every user but the one given
                query.whereNotEqualTo("username", username)
            }

            query.orderByDescending("createdAt")

            val images = ArrayList<ImageData>()
            adapter = RecyclerViewAdapter(thisContext, images)// <- this is initialized before the query is completed!

            query.findInBackground { objects, e ->
                if (e == null && objects.size > 0) {
                    for (`object` in objects) {
                        val file = `object`.get("image") as ParseFile
                        val user = `object`.get("username").toString() // finally managed to add more information to a photo (ProfileImageViewHolder and XML files are responsible for that)
                        val description = `object`.get("description").toString()

                        file.getDataInBackground { data, e ->
                            if (e == null && data != null) {
                                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                                val imd = ImageData(user, description, bitmap)
                                if(position % 5 == 4){
                                    images.add(ImageData("empty: $user", "empty $description", bitmap))
                                }
                                images.add(imd)
                                position++
                                notificationByPass(adapter)// because async
                            }
                        }
                    }
                }
            }
            return adapter
        }

        fun notificationByPass(adapter: RecyclerViewAdapter) {
            adapter.notifyDataSetChanged()
        }
    }
}