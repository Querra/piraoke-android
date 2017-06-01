package de.querra.mobile.piraoke;

import android.app.Application;
import android.content.Context;

public class PiraokeApplication extends Application {

    public static Context CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
    }
}
