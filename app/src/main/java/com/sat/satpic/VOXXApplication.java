package com.sat.satpic;

import android.app.Application;

import com.sat.satpic.utils.LogUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;


/**
 * Created by Tianluhua on 2018/3/13.
 */

public class VOXXApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        ZXingLibrary.initDisplayOpinion(this);
        LogUtils.isDebug=false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
