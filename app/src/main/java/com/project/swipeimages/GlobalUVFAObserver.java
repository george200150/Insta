package com.swipeimages;

public class GlobalUVFAObserver {

    private static UserVerticalFeedActivity uvfa;

    public static void setUvfa(UserVerticalFeedActivity userVerticalFeedActivity) {
        uvfa = userVerticalFeedActivity;
    }

    public static void sendLike(){
        uvfa.receivelikedNotification();
    }
}
