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
import com.parse.*
import java.util.ArrayList

//import java.io.Serializable

class RecyclerViewNotificationsAdapter(private val context: Context, private val imageDataList: List<ImagePlusData>) : /*Serializable,*/ RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var curImageDataIndex = 0
    // context could be later used

    /**
     * initialize the viewHolder with our own
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProfileImageViewHolder(inflateResource(parent, R.layout.item_notification))
    }


    /**
     * method used for binding the holder to a certain position in the list and show it to the user
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageData = imageDataList[curImageDataIndex]
        (holder as ProfileImageViewHolder).notifImg.setImageBitmap(imageData.resource)
        holder.notifText.text = imageData.title
        holder.notifImg.tag = imageData.uid
        holder.itmView.tag = imageData.uid

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
     * intended to. It also implements the onClick method so that each notification click will be
     * sent to the GlobalObserver and delivered to the UserListActivity where it will be processed.
     */
    internal inner class ProfileImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var notifImg: ImageView
        var notifText: TextView
        var itmView: View

        init {
            notifImg = itemView.findViewById(R.id.notifImg)
            notifText = itemView.findViewById(R.id.notifText)
            itmView = itemView
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(v != null) {
                GlobalObserver.notificationClicked(v.tag as String)
            }
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
        fun setupNotifPageView(username: String, isFilteredByUser: Boolean, appContext: Context, thisContext: AppCompatActivity): RecyclerViewNotificationsAdapter {
            val adapter: RecyclerViewNotificationsAdapter
            val images = ArrayList<ImagePlusData>()
            adapter = RecyclerViewNotificationsAdapter(thisContext, images)

            val notifQuery = ParseQuery<ParseObject>("Notification")
            notifQuery.whereEqualTo("receiverUsername", ParseUser.getCurrentUser().username)
            notifQuery.orderByDescending("createdAt")

            notifQuery.findInBackground { notifs, e ->
                if (e == null && notifs.size > 0) {
                    for (notif in notifs) {
                        val fromUser = notif.get("serderUsername") as String
                        val formatted = "$fromUser has liked your photo!"
                        val yourLikedPhotoFile = notif.get("photo") as ParseFile
                        val uniqId = notif.objectId.toString()

                        yourLikedPhotoFile.getDataInBackground { data, ee ->
                            if (ee == null && data != null) {
                                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                                val impd = ImagePlusData(formatted, "", bitmap, uniqId)
                                images.add(impd)
                                notificationByPass(adapter)
                            }
                        }
                    }
                }
            }
            return adapter
        }


        fun notificationByPass(adapter: RecyclerViewNotificationsAdapter) {
            adapter.notifyDataSetChanged()
        }
    }
}