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

class RecyclerViewAdapter(private val context: Context, private val imageDataList: List<ImageData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var curImageDataIndex = 0
    // context could be later used

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            in 0..2 -> ImageViewHolder(inflateResource(parent, R.layout.item_image_small))
            3 -> ImageViewHolder(inflateResource(parent, R.layout.item_image_large))
            else -> AdViewHolder(inflateResource(parent, R.layout.item_ad))
        }
    }

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

    override fun getItemViewType(position: Int): Int {
        return position % 5
    }

    private fun inflateResource(parent: ViewGroup, resourceId: Int): View {
        return LayoutInflater.from(parent.context).inflate(resourceId, parent, false)
    }

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

    internal inner class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivAd: ImageView

        init {
            ivAd = itemView.findViewById(R.id.ivAd)
        }
    }

    companion object{
        fun setupPageView(username: String, isFilteredByUser: Boolean, appContext: Context, thisContext: AppCompatActivity): RecyclerViewAdapter {

            //VAL is a constant ; VAR is a variable !!!
            var position = 0

            val adapter: RecyclerViewAdapter

            val query = ParseQuery<ParseObject>("Image")
            if (isFilteredByUser) {
                query.whereEqualTo("username", username)
            }
            query.orderByDescending("createdAt")

            val images = ArrayList<ImageData>()
            adapter = RecyclerViewAdapter(thisContext, images)// <- this is initialized before the query is completed!

            query.findInBackground { objects, e ->
                if (e == null && objects.size > 0) {
                    for (`object` in objects) {
                        val file = `object`.get("image") as ParseFile
                        val user = `object`.get("username").toString() // finally managed to add more information to a photo (ImageViewHolder and XML files are responsible for that)
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