package com.swipeimages;


import org.jetbrains.annotations.NotNull;

public class GlobalObserver {// couldn't manage to send those objects through intent... so... here they are

    private static UserListActivity ula;

    public static void setULA(UserListActivity userListActivity){
        ula = userListActivity;
    }

    public static void updateULA(){
        ula.invalidate();
    }

    public static void invalidateProfile(){
        ula.setUpProfile();
    }

    public static void notificationClicked(@NotNull String tag) {
        ula.deleteNotif(tag);
    }
}
