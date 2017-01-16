package io.github.waka.sevenhack.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

public final class SnackbarUtil {

    public static void show(View view, int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show();
    }
}
