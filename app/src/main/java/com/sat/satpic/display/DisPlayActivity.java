package com.sat.satpic.display;

import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.ProgressBar;

import com.sat.satpic.Config;
import com.sat.satpic.R;
import com.sat.satpic.base.AbstractMVPActivity;
import com.sat.satpic.utils.HideSystemUIUtils;
import com.sat.satpic.utils.LogUtils;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public class DisPlayActivity extends AbstractMVPActivity<DisplayView, DisplayPresenter> implements DisplayView {


    public static final String TAG = "DisPlayActivity";

    private SurfaceView displayRemoteDeviceSurface;
    private ProgressBar displayRemoteDeviceWaitProgress;
    private String remoteServiceIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HideSystemUIUtils.hideSystemUI(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        remoteServiceIP = getIntent().getExtras().getString(Config.SystemKey.KEY_BUNDLE_SERVICE_IP);
        LogUtils.e(TAG, "remoteServiceIP:"+remoteServiceIP);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_display;
    }

    @Override
    protected void initView() {
        displayRemoteDeviceSurface = findViewById(R.id.dispaly_remote_service_surface);
        displayRemoteDeviceWaitProgress = findViewById(R.id.display_remote_service_wait);
    }

    @Override
    protected DisplayPresenter createPresenter() {
        return new DisplayPresenter();
    }
}
