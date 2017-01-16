package io.github.waka.sevenhack.logics;

import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.waka.sevenhack.data.dao.EnclosureCacheDao;
import io.github.waka.sevenhack.data.dao.EpisodeDao;
import io.github.waka.sevenhack.data.dxo.EpisodeDxo;
import io.github.waka.sevenhack.data.entities.EnclosureCache;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.entities.Podcast;
import io.github.waka.sevenhack.data.models.Rss;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class EpisodeLogic {

    private final EpisodeDao episodeDao;
    private final EnclosureCacheDao enclosureCacheDao;

    @Inject
    public EpisodeLogic(EpisodeDao episodeDao, EnclosureCacheDao enclosureCacheDao) {
        this.episodeDao = episodeDao;
        this.enclosureCacheDao = enclosureCacheDao;
    }

    public List<Episode> createOnlyNewers(Podcast podcast, Rss rss) {
        List<Episode> newEpisodes = EpisodeDxo.convertOnlyDifference(podcast, rss);

        BriteDatabase.Transaction transaction = episodeDao.newTransaction();
        try {
            for (Episode episode : newEpisodes) {
                episodeDao.insert(episode);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }

        return newEpisodes;
    }

    public Observable<List<Episode>> list(Podcast podcast, int limit, int offset) {
        return Observable.just(episodeDao.findAll(podcast, limit, offset))
                .flatMap(this::withEnclosureCache)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Episode>> downloads(int limit, int offset) {
        return Observable.just(episodeDao.findAllWithDownloads(limit, offset))
                .flatMap(this::withEnclosureCache)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<List<Episode>> withEnclosureCache(List<Episode> episodes) {
        return Observable.just(enclosureCacheDao.findAll(episodes))
                .map(enclosureCaches -> {
                    for (Episode episode : episodes) {
                        for (EnclosureCache cache : enclosureCaches) {
                            if (cache.episodeId == episode.id) {
                                episode.enclosureCache = cache;
                            }
                        }
                    }
                    return episodes;
                });
    }
}
