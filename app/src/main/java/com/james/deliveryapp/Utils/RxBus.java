package com.james.deliveryapp.Utils;

import io.reactivex.subjects.PublishSubject;

// PublishSubject for publish and subscribe
public class RxBus {
    private static RxBus instance = null;
    private PublishSubject<String> subject;

    public static RxBus getInstance() {
        if (instance == null) {
            instance = new RxBus();
        }

        return instance;
    }

    private RxBus() {
        subject = PublishSubject.create();
    }

    // publish the messages
    public void sendEvent(String event) {
        subject.onNext(event);
    }

    public PublishSubject<String> getSubject() {
        return subject;
    }
}
