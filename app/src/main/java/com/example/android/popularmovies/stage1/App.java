package com.example.android.popularmovies.stage1;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 * Created by User on 22.10.2016..
 */

public class App extends Application {

    private static App sApp;

    public static Context getApplication() {
        return sApp;
    }

    @Override
    public void onCreate() {
        sApp = this;
        Stetho.initializeWithDefaults(this);
    }
}
