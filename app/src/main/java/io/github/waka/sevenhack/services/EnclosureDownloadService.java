package io.github.waka.sevenhack.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.waka.sevenhack.MainApplication;
import io.github.waka.sevenhack.data.entities.EnclosureCache;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.events.DownloadedEventProvider;
import io.github.waka.sevenhack.internal.di.ServiceComponent;
import io.github.waka.sevenhack.internal.di.ServiceModule;
import io.github.waka.sevenhack.utils.BundleUtil;
import io.github.waka.sevenhack.logics.EnclosureCacheLogic;
import io.github.waka.sevenhack.views.notifications.DownloadFailedNotification;
import io.github.waka.sevenhack.views.notifications.DownloadedNotification;
import io.github.waka.sevenhack.views.notifications.DownloadingNotification;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EnclosureDownloadService extends IntentService {

    private static final String EXTRA_KEY = "extra";

    @Inject
    EnclosureCacheLogic enclosureCacheLogic;

    private static final List<Episode> downloadingList = new ArrayList<>();

    public static void start(Context context, Episode episode) {
        Intent intent = new Intent(context, EnclosureDownloadService.class);
        intent.putExtra(EXTRA_KEY, BundleUtil.createWithEpisode(episode));
        context.startService(intent);
    }

    public static void cancel(Context context, Episode episode) {
        int index = getItemIndexFromDownloadingList(episode);
        if (index == -1) {
            return;
        }

        removeItemFromDownloadingList(episode);
        DownloadingNotification.cancel(context, episode);
    }

    @SuppressWarnings("unused")
    public EnclosureDownloadService() {
        this(EnclosureDownloadService.class.getSimpleName());
    }

    public EnclosureDownloadService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ServiceComponent component = ((MainApplication) getApplicationContext())
                .getComponent()
                .plus(new ServiceModule(getApplicationContext()));
        component.inject(this);
    }

    public static boolean isDownloading(Episode episode) {
        int index = getItemIndexFromDownloadingList(episode);
        return (index != -1);
    }

    private static int getItemIndexFromDownloadingList(Episode episode) {
        if (episode == null) {
            return -1;
        }

        int index = 0;
        while (index < downloadingList.size()) {
            Episode downloading = downloadingList.get(index);
            if (downloading.equals(episode)) {
                return index;
            }
            index++;
        }

        return -1;
    }

    private static void removeItemFromDownloadingList(Episode episode) {
        if (episode == null) {
            return;
        }

        int index = getItemIndexFromDownloadingList(episode);
        if (index == -1) {
            return;
        }

        downloadingList.remove(index);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getBundleExtra(EXTRA_KEY);
        final Episode episode = BundleUtil.getEpisode(bundle);
        if (episode == null || isDownloading(episode)) {
            return;
        }

        downloadingList.add(episode);
        DownloadingNotification.notify(getApplicationContext(), episode);

        // do episode
        enclosureCacheLogic.download(episode.enclosureUrl)
                .flatMap(enclosureCacheLogic.processFile(getApplicationContext(), episode))
                .flatMap(enclosureCacheLogic.processCreate(episode))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EnclosureCache>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onComplete() {}

                    @Override
                    public void onError(Throwable e) {
                        DownloadingNotification.cancel(getApplicationContext(), episode);
                        DownloadFailedNotification.notify(getApplicationContext(), episode);
                    }

                    @Override
                    public void onNext(EnclosureCache cache) {
                        DownloadingNotification.cancel(getApplicationContext(), episode);

                        if (isDownloading(episode)) {
                            downloadingList.remove(episode);
                            DownloadedNotification.notify(getApplicationContext(), episode);
                            DownloadedEventProvider.getInstance().send(episode);
                        } else {
                            enclosureCacheLogic.remove(getApplicationContext(), cache);
                        }
                    }
                });
    }
}
