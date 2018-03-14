package com.sat.satpic.base;

import android.content.Context;

import com.sat.satpic.searcher.SearcherActivity;

/**
 * Created by Tianluhua on 2018/3/13.
 *
 * @param <V>
 */
public abstract class AbstractPresenter<V extends BaseView> {

    private V mView;

    /**
     * Bind View
     *
     * @param view
     */
    public void attachView(V view) {
        this.mView = view;
    }

    /**
     * Unbind View
     */
    public void detachView() {
        this.mView = null;
    }

    /**
     * Get View
     *
     * @return
     */
    public V getView() {
        return mView;
    }

    /**
     * Display remoteService by service id
     *
     * @param mContext
     */
    public abstract void startDispayRemoteByServiceID(String remoteServiceID);
}
