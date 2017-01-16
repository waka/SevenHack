package io.github.waka.sevenhack.views.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Episode;

public class EnclosureDownloadDialog {

    public static AlertDialog download(Context context, Episode episode, DialogCallbacks callbacks) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(episode.title);
        builder.setMessage(episode.subTitle + "(" + episode.duration + ")");
        builder.setPositiveButton(R.string.dialog_download, (dialog, which) -> callbacks.onConfirm());
        builder.setNegativeButton(R.string.dialog_no, null);
        return builder.create();
    }

    public static AlertDialog cancelDownload(Context context, Episode episode, DialogCallbacks callbacks) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(episode.title);
        builder.setMessage(episode.subTitle + "(" + episode.duration + ")");
        builder.setPositiveButton(R.string.dialog_download_cancel, (dialog, which) -> callbacks.onConfirm());
        builder.setNegativeButton(R.string.dialog_no, null);
        return builder.create();
    }

    public static AlertDialog clearDownload(Context context, Episode episode, DialogCallbacks callbacks) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(episode.title);
        builder.setMessage(episode.subTitle);
        builder.setPositiveButton(R.string.dialog_download_clear, (dialog, which) -> callbacks.onConfirm());
        builder.setNegativeButton(R.string.dialog_no, null);
        return builder.create();
    }
}
