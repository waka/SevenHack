package io.github.waka.sevenhack.logics;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.waka.sevenhack.data.dao.EnclosureCacheDao;
import io.github.waka.sevenhack.data.entities.EnclosureCache;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.network.ApiClient;
import io.github.waka.sevenhack.utils.FileUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;

@Singleton
public class EnclosureCacheLogic {

    private final ApiClient apiClient;
    private final EnclosureCacheDao enclosureCacheDao;

    @Inject
    public EnclosureCacheLogic(ApiClient apiClient, EnclosureCacheDao enclosureCacheDao) {
        this.apiClient = apiClient;
        this.enclosureCacheDao = enclosureCacheDao;
    }

    private int create(EnclosureCache cache) {
        return (int) enclosureCacheDao.insert(cache);
    }

    public boolean remove(Context context, EnclosureCache cache) {
        int rows = (int) enclosureCacheDao.delete(cache);
        boolean deleted = FileUtil.getEnclosureCacheFile(context, cache).delete();

        return rows > 0 && deleted;
    }

    public Observable<EnclosureCache> get(Episode episode) {
        return Observable.just(enclosureCacheDao.find(episode))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<ResponseBody>> download(String url) {
        return apiClient.fetchEnclosure(url);
    }

    public Function<Response<ResponseBody>, Observable<File>> processFile(Context context, Episode episode) {
        return response -> writeFile(context, response, episode);
    }

    public Function<File, Observable<EnclosureCache>> processCreate(Episode episode) {
        return file -> createRecord(episode, file);
    }

    private Observable<File> writeFile(final Context context, final Response<ResponseBody> response, final Episode episode) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> subscriber) {
                try {
                    File dir = new File(FileUtil.getEnclosureCacheDir(context), String.valueOf(episode.podcastId));
                    if (!dir.exists() && !dir.mkdir()) {
                        throw new IOException("can not make directory");
                    }

                    File file = new File(
                            FileUtil.getEnclosureCacheDir(context),
                            String.valueOf(episode.podcastId) + "/" + (new File(episode.enclosureUrl)).getName());

                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                    sink.writeAll(response.body().source());
                    sink.close();

                    subscriber.onNext(file);
                } catch (IOException e) {
                    subscriber.onError(e);
                } finally {
                    response.body().close();
                }
            }
        });
    }

    private Observable<EnclosureCache> createRecord(final Episode episode, final File file) {
        return Observable.create(new ObservableOnSubscribe<EnclosureCache>() {
            @Override
            public void subscribe(ObservableEmitter<EnclosureCache> subscriber) {
                EnclosureCache cache = new EnclosureCache();
                cache.episodeId = episode.id;
                cache.path = String.valueOf(episode.podcastId) + "/" + file.getName();

                int id = create(cache);
                if (id > -1) {
                    cache.id = id;
                    episode.enclosureCache = cache;

                    subscriber.onNext(cache);
                } else {
                    subscriber.onError(new RuntimeException("failed to save cache"));
                }
            }
        });
    }
}
