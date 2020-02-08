package com.project.swipeimages

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

class RecyclerViewDeletableAdapter(private val context: Context, private val imageDataList: List<ImageDeletableData>) : /*Serializable,*/ RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var curImageDataIndex = 0
    // context could be later used

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DeletableImageViewHolder(inflateResource(parent, R.layout.item_photo_deletable))
    }

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


    private fun inflateResource(parent: ViewGroup, resourceId: Int): View {
        return LayoutInflater.from(parent.context).inflate(resourceId, parent, false)
    }

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
        fun setupDeletablePageView(username: String, isFilteredByUser: Boolean, appContext: Context, thisContext: AppCompatActivity): RecyclerViewDeletableAdapter {

            val adapter: RecyclerViewDeletableAdapter

            val query = ParseQuery<ParseObject>("Image")
            if (isFilteredByUser) {
                query.whereEqualTo("username", username)
            }
            query.orderByDescending("createdAt")

            val images = ArrayList<ImageDeletableData>()
            adapter = RecyclerViewDeletableAdapter(thisContext, images)// <- this is initialized before the query is completed!

            query.findInBackground { objects, e ->
                if (e == null && objects.size > 0) {
                    for (`object` in objects) {
                        val file = `object`.get("image") as ParseFile
                        val user = `object`.get("username").toString() // finally managed to add more information to a photo (DeletableImageViewHolder and XML files are responsible for that)
                        val description = `object`.get("description").toString()
                        val uniqId = `object`.objectId.toString()

                        file.getDataInBackground { data, e ->
                            if (e == null && data != null) {
                                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                                val imd = ImageDeletableData(user, description, bitmap, uniqId)
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