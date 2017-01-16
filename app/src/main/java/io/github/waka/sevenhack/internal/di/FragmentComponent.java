package io.github.waka.sevenhack.internal.di;

import dagger.Subcomponent;
import io.github.waka.sevenhack.views.fragments.DownloadedListFragment;
import io.github.waka.sevenhack.views.fragments.EpisodeFragment;
import io.github.waka.sevenhack.views.fragments.PodcastFragment;
import io.github.waka.sevenhack.views.fragments.PodcastListFragment;
import io.github.waka.sevenhack.views.fragments.SearchPodcastFragment;
import io.github.waka.sevenhack.internal.di.scope.FragmentScope;

@FragmentScope
@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent {
    void inject(PodcastListFragment fragment);
    void inject(DownloadedListFragment fragment);
    void inject(SearchPodcastFragment fragment);
    void inject(PodcastFragment fragment);
    void inject(EpisodeFragment fragment);
}
