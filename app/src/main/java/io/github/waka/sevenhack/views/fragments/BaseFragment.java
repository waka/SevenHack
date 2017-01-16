package io.github.waka.sevenhack.views.fragments;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import io.github.waka.sevenhack.activities.BaseActivity;
import io.github.waka.sevenhack.internal.di.FragmentComponent;
import io.github.waka.sevenhack.internal.di.FragmentModule;

public class BaseFragment extends Fragment {

    private FragmentComponent fragmentComponent;

    @NonNull
    FragmentComponent getComponent() {
        if (fragmentComponent != null) {
            return fragmentComponent;
        }

        Activity activity = getActivity();
        fragmentComponent = ((BaseActivity) activity).getComponent()
                .plus(new FragmentModule(this));
        return fragmentComponent;
    }
}
