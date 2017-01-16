package io.github.waka.sevenhack.internal.di;

import javax.inject.Singleton;

import dagger.Component;
import io.github.waka.sevenhack.StethoWrapper;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(StethoWrapper stethoDelegator);

    ActivityComponent plus(ActivityModule module);
    ServiceComponent plus(ServiceModule module);
}
