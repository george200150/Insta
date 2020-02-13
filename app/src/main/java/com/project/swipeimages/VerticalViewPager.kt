package com.swipeimages

import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs
import kotlin.math.max
import android.text.method.Touch.onTouchEvent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.GestureDetector

/**
 * constant macros used to set the values of the opacity and zoom when swiping over the images
 */
private const val MIN_SCALE = 0.8f
private const val MIN_ALPHA = 0.7f

class VerticalViewPager
(c: Context) :
        ViewPager(c) {

    /**
     * gesture detector object used to detect the Double Tap
     */
    private val gestureDetector = GestureDetector(c, GestureListener())

    init {
        setPageTransformer(true, VerticalPageTransformer())
        overScrollMode = View.OVER_SCROLL_NEVER
    }


    /**
     * disable horizontal scroll, so that the images can only be swiped up and down
     */
    override fun canScrollHorizontally(direction: Int) = false


    /**
     * Method that swaps the XY axis in order to allow the image to be swiped the way we want.
     */
    private fun swapXY(ev: MotionEvent) =
            ev.apply {
                setLocation(
                        (ev.y / height) * width,
                        (ev.x / width) * height
                )
            }


    /**
     * Method that intercepts the swipe and swaps the XY axis of the screen.
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val intercepted = super.onInterceptTouchEvent(swapXY(ev ?: return false))
        swapXY(ev)
        return intercepted
    }

    /**
     * Method that manages the touch event, whether it is a swipe or a tap.
     */
    override fun onTouchEvent(ev: MotionEvent?) : Boolean {
        return super.onTouchEvent(swapXY(ev!!)) or gestureDetector.onTouchEvent(ev)

    }


    /**
     * Inner Class responsible of listening to complex user gestures, such as Double Tap. (like)
     */
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        // event when double tap occurs
        override fun onDoubleTap(e: MotionEvent): Boolean {
            val x = e.x
            val y = e.y
            Log.d("Double Tap", "Tapped at: ($x,$y)")
            GlobalUVFAObserver.sendLike()
            return true
        }
    }


    /**
     * Inner Class responsible of the movement of the image when swiped.
     */
    inner class VerticalPageTransformer : PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.apply {
                when {
                    position < -1 -> {
                        alpha = 0f
                    }
                    position <= 1 -> {
                        val scaleFactor = max(MIN_SCALE, 1 - abs(position))

                        translationX = width * position * (-1)
                        translationY = height * position

                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> {
                        alpha = 0f
                    }
                }
            }
        }
    }
}