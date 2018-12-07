package com.zuojie.soundrecorder.rxjava;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SimpleRxJava<T> {
    private Observable<T> mObservable;

    public static <T> SimpleRxJava<T> create(ObservableOnSubscribe<T> source) {
        return new SimpleRxJava<>(source);
    }

    private SimpleRxJava(ObservableOnSubscribe<T> source) {
        mObservable = Observable.create(source).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void subscribe(Observer<T> observer) {
        mObservable.subscribe(new io.reactivex.Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(T t) {
                observer.onNext(t);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public interface Observer<T> {
        void onNext(T t);
    }
}
