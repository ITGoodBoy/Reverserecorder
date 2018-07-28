package com.celestialapps.reverserecorder;

import android.app.Application;

import com.celestialapps.reverserecorder.dagger.components.AppComponent;
import com.celestialapps.reverserecorder.dagger.components.DaggerAppComponent;
import com.celestialapps.reverserecorder.dagger.modules.ContextModule;

public class App extends Application {

    private static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = buildAppComponent();
    }

    protected AppComponent buildAppComponent() {
        return DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }


}
