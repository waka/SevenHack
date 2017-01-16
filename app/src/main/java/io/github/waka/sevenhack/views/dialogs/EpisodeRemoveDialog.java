package io.github.waka.sevenhack.views.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Podcast;

public class EpisodeRemoveDialog {

    public static AlertDialog remove(Context context, Podcast podcast, DialogCallbacks callbacks) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(podcast.title);
        builder.setMessage(R.string.dialog_remove_podcast_description);
        builder.setPositiveButton(R.string.dialog_remove_podcast, (dialog, which) -> callbacks.onConfirm());
        builder.setNegativeButton(R.string.dialog_no, null);
        return builder.create();
    }
}
