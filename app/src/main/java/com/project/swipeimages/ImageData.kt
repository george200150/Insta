package com.swipeimages

import android.graphics.Bitmap

open class ImageData(open val title: String, open val description: String, open val resource: Bitmap)


class ImagePlusData(override val title: String, override val description: String, override val resource: Bitmap, val uid: String) : ImageData(title, description, resource)

