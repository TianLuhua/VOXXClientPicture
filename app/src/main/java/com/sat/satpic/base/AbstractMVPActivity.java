package com.sat.satpic.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.sat.satpic.Config;
import com.sat.satpic.R;

/**
 * Created by Tianluhua on 2018/3/13.
 *
 * @param <V>
 * @param <P>
 */
public abstract class AbstractMVPActivity<V extends BaseView, P extends AbstractPresenter<V>>
        extends Activity {

    private P mPresenter;
    private long mExitTime = 0;

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
        setContentView(getContentViewID());
        initView();
    }
    protected abstract int getContentViewID();
    protected abstract void initView();

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > Config.SystemTime.ActivityBackTime) {
                Toast.makeText(this, getResources().getString(R.string.press_again), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();// update mExitTime
            } else {
                System.exit(0);// back system
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

}
