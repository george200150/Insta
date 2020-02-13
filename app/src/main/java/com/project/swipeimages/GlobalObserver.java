package com.swipeimages;


import org.jetbrains.annotations.NotNull;

/**
 * This Observer is meant to create a static link between the main menu and all the other features
 * implemented in other files.
 *
 * The setULA method is used in the onCreate of the UserListActivity class, in order to notify any
 * changes caused by the other Activities.
 *
 * The updateULA method is used to notify a major change of the UserListActivity and, therefore,
 * reset the entire menu, by telling the Activity to reload all the data from the server because it
 * may miss important information otherwise.
 *
 * The invalidateProfile method is used to notify a minor change in the profile menu.(photo changed)
 *
 * The notificationClicked method is used to delete the selected notification. This method is called
 * by the RecyclerViewNotificationsAdapter itself, as its items are listeners to themselves and
 * built only to serve as notifications. Their onClick method could have been overridden only in the
 * RecyclerView, so it sends the signals through the GlobalObserver to the UserListActivity.
 */
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
