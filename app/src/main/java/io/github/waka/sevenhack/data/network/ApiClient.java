package io.github.waka.sevenhack.data.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.waka.sevenhack.data.models.SearchResult;
import io.github.waka.sevenhack.data.models.SearchResultContainer;
import io.github.waka.sevenhack.data.models.Rss;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

@Singleton
public class ApiClient {

    private final ITunesSearchService iTunesSearchService;
    private final RssService rssService;
    private final EnclosureService enclosureService;

    @Inject
    ApiClient(OkHttpClient client) {
        Retrofit iTunesRequest = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://itunes.apple.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        iTunesSearchService = iTunesRequest.create(ITunesSearchService.class);

        Retrofit rssRequest = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.base.url")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        rssService = rssRequest.create(RssService.class);

        Retrofit enclosureRequest = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.base.url")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        enclosureService= enclosureRequest.create(EnclosureService.class);
    }

    public Observable<List<SearchResult>> fetchPodcasts(String term) {
        Map<String, String> options = new HashMap<>();
        options.put("media", "podcast");
        options.put("entity", "podcast");
        options.put("term", term);
        return iTunesSearchService.fetchPodcasts(options)
                .map(container -> container.searchResults);
    }

    public Observable<Rss> fetchRss(String url) {
        return rssService.fetchRss(url);
    }

    public Observable<Response<ResponseBody>> fetchEnclosure(String url) {
        return enclosureService.fetchEnclosure(url);
    }

    private interface ITunesSearchService {
        @GET("/search")
        Observable<SearchResultContainer> fetchPodcasts(@QueryMap Map<String, String> options);
    }

    private interface RssService {
        @GET
        Observable<Rss> fetchRss(@Url String url);
    }

    private interface EnclosureService {
        @Streaming
        @GET
        Observable<Response<ResponseBody>> fetchEnclosure(@Url String url);
    }
}
