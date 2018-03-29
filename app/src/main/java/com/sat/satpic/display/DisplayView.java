package com.sat.satpic.display;

import android.graphics.Bitmap;

import com.sat.satpic.base.BaseView;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public interface DisplayView extends BaseView {


    public void loading();

    public void disPlayRemoteDesk(Bitmap bitmap);

    public void fila();

    public void connectSucess();

    public void displayTimeout();
}
