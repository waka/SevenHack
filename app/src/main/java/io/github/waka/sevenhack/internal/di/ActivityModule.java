package io.github.waka.sevenhack.internal.di;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import dagger.Module;
import dagger.Provides;

@Module
public final class ActivityModule {
    private final AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    Activity  provideActivity() {
        return activity;
    }

    @Provides
    Context provideContext() {
        return activity;
    }

    @Provides
    LayoutInflater provideLayoutInflater() {
        return activity.getLayoutInflater();
    }
}
