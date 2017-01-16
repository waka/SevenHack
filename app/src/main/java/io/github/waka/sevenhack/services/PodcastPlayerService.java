package io.github.waka.sevenhack.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import io.github.waka.sevenhack.views.notifications.PodcastPlayerNotification;

public class PodcastPlayerService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PodcastPlayerNotification.handleAction(this, intent.getAction());
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
