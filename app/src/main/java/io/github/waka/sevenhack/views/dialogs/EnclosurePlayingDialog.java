package io.github.waka.sevenhack.views.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Episode;

public class EnclosurePlayingDialog {

    public static AlertDialog play(Context context, Episode episode, DialogCallbacks callbacks) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
        builder.setTitle(episode.title);
        builder.setMessage(episode.subTitle);
        builder.setPositiveButton(R.string.dialog_play, (dialog, which) -> callbacks.onConfirm());
        builder.setNegativeButton(R.string.dialog_no, null);
        return builder.create();
    }

    public static AlertDialog streaming(Context context, Episode episode, DialogCallbacks callbacks) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
        builder.setTitle(episode.title);
        builder.setMessage(
                episode.subTitle + "\n\n" + context.getString(R.string.dialog_streaming_play_description)
        );
        builder.setPositiveButton(R.string.dialog_streaming_play, (dialog, which) -> callbacks.onConfirm());
        builder.setNegativeButton(R.string.dialog_no, null);
        return builder.create();
    }
}
