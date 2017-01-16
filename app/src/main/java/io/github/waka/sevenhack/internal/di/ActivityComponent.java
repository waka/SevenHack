package io.github.waka.sevenhack.internal.di;

import dagger.Subcomponent;
import io.github.waka.sevenhack.activities.EpisodeActivity;
import io.github.waka.sevenhack.activities.PodcastActivity;
import io.github.waka.sevenhack.activities.MainActivity;
import io.github.waka.sevenhack.activities.SearchPodcastActivity;
import io.github.waka.sevenhack.internal.di.scope.ActivityScope;

@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(SearchPodcastActivity activity);
    void inject(PodcastActivity activity);
    void inject(EpisodeActivity activity);

    FragmentComponent plus(FragmentModule module);
}
