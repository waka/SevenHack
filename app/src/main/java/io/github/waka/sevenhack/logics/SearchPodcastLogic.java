package io.github.waka.sevenhack.logics;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.waka.sevenhack.data.models.SearchResult;
import io.github.waka.sevenhack.data.models.Rss;
import io.github.waka.sevenhack.data.network.ApiClient;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class SearchPodcastLogic {

    private final ApiClient apiClient;

    @Inject
    public SearchPodcastLogic(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Observable<List<SearchResult>> search(String keyword) {
        return apiClient.fetchPodcasts(keyword)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Rss> fetchRss(String url) {
        return apiClient.fetchRss(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
