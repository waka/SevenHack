package io.github.waka.sevenhack.events;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxBus {

    private final PublishSubject<Object> bus = PublishSubject.create();

    RxBus() {}

    public void send(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return bus;
    }
}
