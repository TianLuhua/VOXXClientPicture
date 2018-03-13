package com.sat.satpic.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Tianluhua on 2018/3/13.
 *
 * @param <V>
 * @param <P>
 */
public abstract class AbstractMVPActivity<V extends BaseView, P extends AbstractPresenter<V>>
        extends Activity {

    private P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        if (mPresenter == null) {

            throw new NullPointerException("Presenter is null.....");
        }
        mPresenter.attachView((V) this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mPresenter != null) {

            mPresenter.detachView();
        }
    }

    protected abstract P createPresenter();

    /**
     * Get Presenter
     *
     * @return
     */
    public P getPresenter() {
        return mPresenter;
    }

}
