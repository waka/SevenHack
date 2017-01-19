package io.github.waka.sevenhack;

import android.content.Context;

import javax.inject.Inject;

public class StethoWrapper {

    @Inject
    Context context;

    static void init(MainApplication app) {
        StethoWrapper wrapper = new StethoWrapper(app);
        wrapper.setup();
    }

    private StethoWrapper(MainApplication app) {
        app.getComponent().inject(this);
    }

    private void setup() {
        //Stetho.initializeWithDefaults(context);
    }
}
