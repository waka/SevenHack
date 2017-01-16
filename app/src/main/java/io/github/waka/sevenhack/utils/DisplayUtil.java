package io.github.waka.sevenhack.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public final class DisplayUtil {
    public static int dpToPx(Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics()));
    }
}
