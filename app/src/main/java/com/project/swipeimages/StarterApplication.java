package com.project.swipeimages;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;


public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

    /*
    ******************************************************************************
    The default username and password is 'user' and 'RnbH7mTYtjCX'.
    ******************************************************************************
    */

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("020bf509ccc736b9d9dac006339d371dea4be1f6")
                .clientKey("8228d40555e7fd26906ae0218c8220d1b4cf4ba2")
                .server("http://18.191.50.192:80/parse/")
                .build()
        );



        //ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}

