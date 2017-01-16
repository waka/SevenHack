package io.github.waka.sevenhack;

import android.app.Application;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;
import io.github.waka.sevenhack.internal.di.AppComponent;
import io.github.waka.sevenhack.internal.di.AppModule;
import io.github.waka.sevenhack.internal.di.DaggerAppComponent;

public class MainApplication extends Application {

    private static MainApplication instance;

    private AppComponent appComponent;

    public MainApplication() {
        instance = this;
    }

    @NonNull
    public AppComponent getComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        // setup libralies
        Fabric.with(this, new Crashlytics());
        StethoWrapper.init(this);
        AndroidThreeTen.init(this);
    }

    public static MainApplication getInstance() {
        return instance;
    }
}
