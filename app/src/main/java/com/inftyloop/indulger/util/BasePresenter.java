package com.inftyloop.indulger.util;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Presenter in MVP model, used to link View and Model
 */
public abstract class BasePresenter<V> {
    // TODO ApiService
    protected V mView;
    private CompositeSubscription mCompositeSub;

    public BasePresenter(V view) {
        attachView(view);
    }

    public void attachView(V view) { mView = view; }

    public void detachView() { mView = null; onUnsubscribe(); }

    @SuppressWarnings("unchecked")
    public void addSubscription(Observable observable, Subscriber subscriber) {
        if(mCompositeSub == null)
            mCompositeSub = new CompositeSubscription();
        mCompositeSub.add(observable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber));
    }

    public void onUnsubscribe() {
        if(mCompositeSub != null && mCompositeSub.hasSubscriptions())
            mCompositeSub.unsubscribe();
    }
}
