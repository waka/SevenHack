package io.github.waka.sevenhack.data.models;

import android.content.Context;
import android.support.v7.graphics.Palette;

import java.io.Serializable;

import io.github.waka.sevenhack.MainApplication;
import io.github.waka.sevenhack.R;

public class PaletteColor implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int backGroundColor;
    private final int titleTextColor;
    private final int bodyTextColor;

    public static PaletteColor buildWithPalette(Palette palette) {
        return new PaletteColor(palette);
    }

    private PaletteColor(Palette palette) {
        Palette.Swatch swatch = palette.getVibrantSwatch();
        Context context = MainApplication.getInstance().getApplicationContext();

        if (swatch != null) {
            backGroundColor = swatch.getRgb();
        } else {
            backGroundColor = context.getResources().getColor(R.color.color_primary);
        }

        titleTextColor = context.getResources().getColor(R.color.theme_text_white);
        bodyTextColor  = context.getResources().getColor(R.color.theme_text_white);
    }

    public int getBackGroundColor() {
        return backGroundColor;
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public int getBodyTextColor() {
        return bodyTextColor;
    }
}
