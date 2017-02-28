package com.richdroid.memorygame.app;

import android.app.Application;
import android.util.Log;

import com.richdroid.memorygame.rest.ApiManager;


/**
 * Created by richa.khanna on 3/20/16.
 */

public class AppController extends Application {

    private static final String TAG = AppController.class
            .getSimpleName();
    // Different Managers
    private ApiManager mApiMan;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "App started");
        initApp();
    }

    private void initApp() {
        mApiMan = ApiManager.getInstance(AppController.this);
        mApiMan.init();
    }

    /**
     * Get the data manager instance
     */
    public synchronized ApiManager getApiManager() {
        return mApiMan;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
