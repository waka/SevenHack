package io.github.waka.sevenhack.events;

public class PlayerEventProvider {

    private static final RxBus BUS = new RxBus();

    private PlayerEventProvider() {}

    public static RxBus getInstance() {
        return BUS;
    }
}
