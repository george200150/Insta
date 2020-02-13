package com.swipeimages;

/**
 * This class is responsible to notifying UserVerticalFeedActivity of the new incoming likes sent
 * from the VerticalViewPager that detects the Double Tap Gesture.
 */
public class GlobalUVFAObserver {

    private static UserVerticalFeedActivity uvfa;

    public static void setUvfa(UserVerticalFeedActivity userVerticalFeedActivity) {
        uvfa = userVerticalFeedActivity;
    }

    public static void sendLike(){
        uvfa.receivelikedNotification();
    }
}
