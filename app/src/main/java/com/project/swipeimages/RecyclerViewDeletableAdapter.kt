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

class RecyclerViewDeletableAdapter(private val context: Context, private val imageDataList: List<ImagePlusData>) : /*Serializable,*/ RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var curImageDataIndex = 0
    // context could be later used

    /**
     * initialize the viewHolder with our own
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DeletableImageViewHolder(inflateResource(parent, R.layout.item_photo_deletable))
    }


    /**
     * method used for binding the holder to a certain position in the list and show it to the user
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageData = imageDataList[curImageDataIndex]
        (holder as DeletableImageViewHolder).ivImage.setImageBitmap(imageData.resource)
        holder.tvTitle.text = imageData.title
        holder.tvDescription.text = imageData.description

        holder.ivImage.tag = imageData.uid

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
     * method used for initializing the holders' data and listeners
     */
    private fun inflateResource(parent: ViewGroup, resourceId: Int): View {
        return LayoutInflater.from(parent.context).inflate(resourceId, parent, false)
    }


    /**
     * custom inner class that inherits from the ViewHolder class
     * This class will receive all the attributes we need to make a complete item in the list, as we
     * intended to.
     */
    internal inner class DeletableImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivImage: ImageView
        var tvTitle: TextView
        var tvDescription: TextView

        init {
            ivImage = itemView.findViewById(R.id.ivImage)
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvDescription = itemView.findViewById(R.id.tvDescription)
        }
    }


    companion object{

        /**
         * static method used to setup the adapter of the RecyclerView
         * It creates the array of data, the adapter itself and queries the database, in order to
         * setup the useful information to each View using a ImagePlusData structure to gather it
         * all in a single object that we will unpack in the onBindViewHolder method.
         * Each time we create a new item and send it to the RecyclerView, we must consider
         * notifying the adapter that its data set has changed.
         */
        fun setupDeletablePageView(username: String, isFilteredByUser: Boolean, appContext: Context, thisContext: AppCompatActivity): RecyclerViewDeletableAdapter {

            val adapter: RecyclerViewDeletableAdapter

            val query = ParseQuery<ParseObject>("Image")
            if (isFilteredByUser) {
                query.whereEqualTo("username", username)
            }
            query.orderByDescending("createdAt")

            val images = ArrayList<ImagePlusData>()
            adapter = RecyclerViewDeletableAdapter(thisContext, images)// <- this is initialized before the query is completed!

            query.findInBackground { objects, e ->
                if (e == null && objects.size > 0) {
                    for (`object` in objects) {
                        val file = `object`.get("image") as ParseFile
                        val user = `object`.get("username").toString() // finally managed to add more information to a photo (ProfileImageViewHolder and XML files are responsible for that)
                        val description = `object`.get("description").toString()
                        val uniqId = `object`.objectId.toString()

                        file.getDataInBackground { data, e ->
                            if (e == null && data != null) {
                                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                                val imd = ImagePlusData(user, description, bitmap, uniqId)
                                images.add(imd)
                                notificationByPass(adapter)// because async
                            }
                        }
                    }
                }
            }
            return adapter
        }

        fun notificationByPass(adapter: RecyclerViewDeletableAdapter) {
            adapter.notifyDataSetChanged()
        }
    }
}