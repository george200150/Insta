package com.project.swipeimages;


public class GlobalObserver {// couldn't manage to send those objects through intent... so... here they are

    private static UserListActivity ula;

    public static void setULA(UserListActivity userListActivity){
        ula = userListActivity;
    }

    public static void updateAllInULA(){
        //we cannot change this code (yet), because, in order to initialize only the current window,
        //we must know which menu page we're onto. Therefore, we have to initialize all of them.
        //...unless I could come up with an unusual bypass that would do...
        ula.setUpDiscover();
        ula.setUpFeed();
        ula.setUpNotifications();
        ula.setUpProfile();
    }
}
