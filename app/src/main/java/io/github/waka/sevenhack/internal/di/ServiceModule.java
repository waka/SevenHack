package io.github.waka.sevenhack.internal.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public final class ServiceModule {

    private final Context context;

    public ServiceModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext() {
        return context;
    }
}
