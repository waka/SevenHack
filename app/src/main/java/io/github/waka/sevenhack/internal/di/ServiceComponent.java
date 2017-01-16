package io.github.waka.sevenhack.internal.di;

import dagger.Subcomponent;
import io.github.waka.sevenhack.internal.di.scope.ServiceScope;
import io.github.waka.sevenhack.services.EnclosureDownloadService;

@ServiceScope
@Subcomponent(modules = ServiceModule.class)
public interface ServiceComponent {

    void inject(EnclosureDownloadService service);
}
