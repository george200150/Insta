package com.swipeimages

import android.graphics.Bitmap

/**
 * virtual class used to group information about images and deliver it to the specific methods from
 * the adapters that unpack the data and set these attributes to the view and display the item
 */
open class ImageData(open val title: String, open val description: String, open val resource: Bitmap)


/**
 * inherited class from ImageData that also stores uid, which is supposed to save the value of the
 * object's primary key, the class' instance has been initialized with, so that we can later access
 * it with ease, when the item is clicked.
 */
class ImagePlusData(override val title: String, override val description: String, override val resource: Bitmap, val uid: String) : ImageData(title, description, resource)

