package io.github.waka.sevenhack.internal.di;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import dagger.Module;
import dagger.Provides;

@Module
public final class FragmentModule {
    private final Fragment fragment;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    Context provideContext() {
        return fragment.getContext();
    }

    @Provides
    FragmentManager provideFragmentManager() {
        return fragment.getFragmentManager();
    }
}
