package io.github.waka.sevenhack.internal.di;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.waka.sevenhack.BuildConfig;
import io.github.waka.sevenhack.data.DbInitializer;
import io.github.waka.sevenhack.data.dao.EnclosureCacheDao;
import io.github.waka.sevenhack.data.dao.EpisodeDao;
import io.github.waka.sevenhack.data.dao.PodcastDao;
import io.github.waka.sevenhack.data.network.ApiClient;
import io.github.waka.sevenhack.data.network.RequestInterceptor;
import io.github.waka.sevenhack.logics.EnclosureCacheLogic;
import io.github.waka.sevenhack.logics.EpisodeLogic;
import io.github.waka.sevenhack.logics.PodcastLogic;
import io.github.waka.sevenhack.logics.SearchPodcastLogic;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@Module
public final class AppModule {
    private static final String CACHE_FILE_NAME = "okhttp.cache";
    private static final long MAX_CACHE_SIZE = 4 * 1024 * 1024;

    private final Context context;

    public AppModule(Application application) {
        this.context = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }

    @Provides
    CompositeSubscription provideCompositeSubscription() {
        return new CompositeSubscription();
    }

    @Provides
    @Singleton
    BriteDatabase provideDatabase(Context context) {
        SqlBrite.Builder builder = new SqlBrite.Builder();
        SqlBrite sqlBrite = builder.build();

        BriteDatabase db = sqlBrite.wrapDatabaseHelper(new DbInitializer(context), Schedulers.io());
        db.setLoggingEnabled(BuildConfig.DEBUG);
        return db;
    }

    @Provides
    @Singleton
    ConnectivityManager provideConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    @Singleton
    Interceptor provideRequestInterceptor(ConnectivityManager connectivityManager) {
        return new RequestInterceptor(connectivityManager);
    }

    @Provides
    OkHttpClient provideHttpClient(Context context, Interceptor interceptor) {
        File cacheDir = new File(context.getCacheDir(), CACHE_FILE_NAME);
        Cache cache = new Cache(cacheDir, MAX_CACHE_SIZE);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(interceptor);
        return builder.build();
    }

    @Provides
    PodcastDao providePodcastDao(BriteDatabase db) {
        return new PodcastDao(db);
    }

    @Provides
    EpisodeDao provideEpisodeDao(BriteDatabase db) {
        return new EpisodeDao(db);
    }

    @Provides
    EnclosureCacheDao provideEnclosureCacheDao(BriteDatabase db) {
        return new EnclosureCacheDao(db);
    }

    @Provides
    SearchPodcastLogic provideSearchPodcastLogic(ApiClient apiClient) {
        return new SearchPodcastLogic(apiClient);
    }

    @Provides
    PodcastLogic providePodcastLogic(PodcastDao podcastDao, EpisodeDao episodeDao) {
        return new PodcastLogic(podcastDao, episodeDao);
    }

    @Provides
    EpisodeLogic provideEpisodeLogic(EpisodeDao episodeDao, EnclosureCacheDao enclosureCacheDao) {
        return new EpisodeLogic(episodeDao, enclosureCacheDao);
    }

    @Provides
    EnclosureCacheLogic provideEnclosureCacheLogic(ApiClient apiClient, EnclosureCacheDao enclosureCacheDao) {
        return new EnclosureCacheLogic(apiClient, enclosureCacheDao);
    }
}
