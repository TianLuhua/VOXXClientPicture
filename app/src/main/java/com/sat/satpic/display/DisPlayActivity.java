package com.sat.satpic.display;

import android.os.Bundle;

import com.sat.satpic.R;
import com.sat.satpic.base.AbstractMVPActivity;
import com.sat.satpic.utils.HideSystemUIUtils;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public class DisPlayActivity extends AbstractMVPActivity<DisplayView, DisplayPresenter> implements DisplayView{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HideSystemUIUtils.hideSystemUI(this);
        setContentView(R.layout.activity_display);
    }

    @Override
    protected int getContentViewID() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected DisplayPresenter createPresenter() {
        return new DisplayPresenter();
    }
}
