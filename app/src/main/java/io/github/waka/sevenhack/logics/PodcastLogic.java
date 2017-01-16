package io.github.waka.sevenhack.logics;

import android.content.Context;

import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.waka.sevenhack.data.dao.EpisodeDao;
import io.github.waka.sevenhack.data.dao.PodcastDao;
import io.github.waka.sevenhack.data.dxo.EpisodeDxo;
import io.github.waka.sevenhack.data.dxo.PodcastDxo;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.entities.Podcast;
import io.github.waka.sevenhack.data.models.Rss;
import io.github.waka.sevenhack.data.models.SearchResult;
import io.github.waka.sevenhack.utils.FileUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class PodcastLogic {

    private final PodcastDao podcastDao;
    private final EpisodeDao episodeDao;

    @Inject
    public PodcastLogic(PodcastDao podcastDao, EpisodeDao episodeDao) {
        this.podcastDao = podcastDao;
        this.episodeDao = episodeDao;
    }

    public Observable<Long> exists(String url) {
        return Observable.just(podcastDao.countByURL(url))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Podcast create(SearchResult searchResult, Rss rss) {
        Podcast podcast = PodcastDxo.convert(searchResult, rss);

        BriteDatabase.Transaction transaction = podcastDao.newTransaction();
        try {
            podcast.id = (int) podcastDao.insert(podcast);

            List<Episode> episodes = EpisodeDxo.convert(podcast, rss);
            for (Episode episode : episodes) {
                episodeDao.insert(episode);
            }

            transaction.markSuccessful();
        } finally {
            transaction.end();
        }

        return podcast;
    }

    public boolean remove(Context context, Podcast podcast) {
        int rows = 0;

        BriteDatabase.Transaction transaction = podcastDao.newTransaction();
        try {
            rows = (int) podcastDao.delete(podcast);
            FileUtil.deleteEnclosureCacheFiles(context, podcast);

            transaction.markSuccessful();
        } finally {
            transaction.end();
        }

        return rows > 0;
    }

    public Observable<List<Podcast>> list() {
        return Observable.just(podcastDao.findAll())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
