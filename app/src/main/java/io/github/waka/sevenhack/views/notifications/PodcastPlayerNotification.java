package io.github.waka.sevenhack.views.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.events.PlayerEventProvider;
import io.github.waka.sevenhack.events.PlayerEvent;
import io.github.waka.sevenhack.media.PodcastPlayer;
import io.github.waka.sevenhack.services.PodcastPlayerService;
import io.github.waka.sevenhack.utils.DateUtil;

public class PodcastPlayerNotification {

    private static final String ACTION_PLAY_OR_PAUSE = "action_play_or_pause";
    private static final String ACTION_STOP = "action_stop";

    public static void notify(Context context, Episode episode) {
        notify(context, episode, 0);
    }

    public static void notify(Context context, Episode episode, int currentPosition) {
        if (!shouldNotify(context.getApplicationContext(), episode)) {
            return;
        }

        NotificationManager notificationManager = getNotificationManager(context.getApplicationContext());
        notificationManager.notify(getNotificationId(), build(context.getApplicationContext(), episode, currentPosition));
    }

    public static void cancel(Context context) {
        if (context.getApplicationContext() == null) {
            return;
        }

        NotificationManager notificationManager = getNotificationManager(context.getApplicationContext());
        notificationManager.cancel(getNotificationId());
    }

    private static Notification build(Context context, Episode episode, int currentPosition) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();

        // アプリアイコンをセット
        builder.setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(episode.title)
                .setContentText(episode.subTitle)
                .setProgress(DateUtil.durationToInt(episode.duration), currentPosition, false)
                .setStyle(style);

        if (PodcastPlayer.getInstance().isPlaying()) {
            builder.addAction(
                    android.R.drawable.ic_media_pause,
                    context.getString(R.string.action_pause),
                    getToggleIntent(context));
        } else {
            builder.addAction(
                    android.R.drawable.ic_media_play,
                    context.getString(R.string.action_play),
                    getToggleIntent(context));
        }
        builder.addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                context.getString(R.string.action_stop),
                getStopIntent(context));

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;

        return notification;
    }

    private static PendingIntent getToggleIntent(Context context) {
        Intent intent = new Intent(context, PodcastPlayerService.class);
        intent.setAction(ACTION_PLAY_OR_PAUSE);
        return PendingIntent.getService(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent getStopIntent(Context context) {
        Intent intent = new Intent(context, PodcastPlayerService.class);
        intent.setAction(ACTION_STOP);
        return PendingIntent.getService(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static int getNotificationId() {
        return NotificationIdFactory.get(NotificationIdFactory.Type.PLAYER);
    }

    private static boolean shouldNotify(Context context, Episode episode) {
        PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
        return (context != null && episode != null && (podcastPlayer.isPlaying() || podcastPlayer.isPaused()));
    }

    public static void handleAction(Context context, String action) {
        if (context == null || TextUtils.isEmpty(action)) {
            return;
        }

        PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();

        if (action.equals(ACTION_PLAY_OR_PAUSE)) {
            if (podcastPlayer.isPlaying()) {
                podcastPlayer.pause();
                PlayerEventProvider.getInstance().send(PlayerEvent.PAUSE);
            } else {
                podcastPlayer.start();
                PlayerEventProvider.getInstance().send(PlayerEvent.PLAY);
            }

            // Update the notification itself
            PodcastPlayerNotification.notify(
                    context,
                    podcastPlayer.getPlayingEpisode(),
                    podcastPlayer.getCurrentPosition());
        } else if (action.equals(ACTION_STOP)) {
            cancel(context);
            podcastPlayer.stop();
            PlayerEventProvider.getInstance().send(PlayerEvent.STOP);
        }
    }
}
