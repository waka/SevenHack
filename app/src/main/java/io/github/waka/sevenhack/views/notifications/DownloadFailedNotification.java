package io.github.waka.sevenhack.views.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Episode;

public class DownloadFailedNotification {

    public static void notify(Context context, Episode episode) {
        NotificationManager notificationManager = getNotificationManager(context);
        notificationManager.notify(getNotificationId(episode), build(context, episode));
    }

    private static Notification build(Context context, Episode episode) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(context.getString(R.string.notification_downloading))
                .setContentText(episode.title);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        return notification;
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static int getNotificationId(Episode episode) {
        return NotificationIdFactory.get(episode, NotificationIdFactory.Type.DOWNLOAD_FAILED);
    }
}
