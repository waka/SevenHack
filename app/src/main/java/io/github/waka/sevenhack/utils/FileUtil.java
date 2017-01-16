package io.github.waka.sevenhack.utils;

import android.content.Context;

import java.io.File;

import io.github.waka.sevenhack.data.entities.EnclosureCache;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.entities.Podcast;

public final class FileUtil {

    public static File getEnclosureCacheDir(Context context) {
        return context.getExternalFilesDir(null);
    }

    public static File getEnclosureCacheFile(Context context, Episode episode) {
        if (episode.enclosureCache == null) {
            return null;
        }
        return new File(getEnclosureCacheDir(context), episode.enclosureCache.path);
    }

    public static File getEnclosureCacheFile(Context context, EnclosureCache cache) {
        return new File(getEnclosureCacheDir(context), cache.path);
    }

    public static void deleteEnclosureCacheFiles(Context context, Podcast podcast) {
        File file = new File(
                getEnclosureCacheDir(context),
                String.valueOf(podcast.id));
        recursiveDeleteFile(file);
    }

    private static void recursiveDeleteFile(final File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                recursiveDeleteFile(child);
            }
        }
        file.delete();
    }
}
