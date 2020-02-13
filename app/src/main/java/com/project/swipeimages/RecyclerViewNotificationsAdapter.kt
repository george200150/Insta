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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProfileImageViewHolder(inflateResource(parent, R.layout.item_notification))
    }

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


    private fun inflateResource(parent: ViewGroup, resourceId: Int): View {
        return LayoutInflater.from(parent.context).inflate(resourceId, parent, false)
    }

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
                GlobalObserver.notificationClicked(v!!.tag as String)
            }
        }
    }


    companion object{

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