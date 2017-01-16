package io.github.waka.sevenhack.events;

public class DownloadedEventProvider {

    private static final RxBus BUS = new RxBus();

    private DownloadedEventProvider() {}

    public static RxBus getInstance() {
        return BUS;
    }
}
