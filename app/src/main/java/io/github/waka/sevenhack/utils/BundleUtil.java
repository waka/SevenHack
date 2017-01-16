package io.github.waka.sevenhack.utils;

import android.os.Bundle;

import org.parceler.Parcels;

import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.entities.Podcast;
import io.github.waka.sevenhack.data.models.PaletteColor;

public class BundleUtil {
    private static final String PODCAST = "podcast";
    private static final String EPISODE = "episode";
    private static final String PALETTE = "palette";

    public static Bundle createWithPodcast(Podcast podcast) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PODCAST, Parcels.wrap(podcast));
        return bundle;
    }

    public static Bundle createWithEpisode(Episode episode) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EPISODE, Parcels.wrap(episode));
        return bundle;
    }

    public static Bundle createWithEpisode(Episode episode, PaletteColor paletteColor) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EPISODE, Parcels.wrap(episode));
        bundle.putSerializable(PALETTE, paletteColor);
        return bundle;
    }

    public static Podcast getPodcast(Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(PODCAST));
    }

    public static Episode getEpisode(Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(EPISODE));
    }

    public static PaletteColor getPaletteColor(Bundle bundle) {
        return (PaletteColor) bundle.getSerializable(PALETTE);
    }
}
