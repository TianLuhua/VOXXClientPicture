package com.sat.satpic.display;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ProgressBar;

import com.sat.satpic.Config;
import com.sat.satpic.R;
import com.sat.satpic.base.AbstractMVPActivity;
import com.sat.satpic.utils.HideSystemUIUtils;
import com.sat.satpic.utils.LogUtils;
import com.sat.satpic.widget.ImageSurfaceView;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public class DisPlayActivity extends AbstractMVPActivity<DisplayView, DisplayPresenter> implements DisplayView {


    public static final String TAG = "DisPlayActivity";

    private ImageSurfaceView displayRemoteDeviceSurface;
    private ProgressBar displayRemoteDeviceWaitProgress;

    private String remoteServiceIP;
    private DisplayPresenter displayPresenter;

    private float densityX = 0;
    private float densityY = 0;

    private int changeX = 0;
    private int changeY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.e("tlh", "onCreate" );
        HideSystemUIUtils.hideSystemUI(this);
        displayPresenter = getPresenter();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        densityX = 1024f / (float) widthPixels;
        densityY = 600f / (float) heightPixels;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.e("tlh", "onRestart" );
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.e("tlh", "onStart" );
        remoteServiceIP = getIntent().getExtras().getString(Config.SystemKey.KEY_BUNDLE_SERVICE_IP);
        LogUtils.e(TAG, "remoteServiceIP:" + remoteServiceIP);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (displayPresenter != null) {
            displayPresenter.startDisPlayRomoteDesk(remoteServiceIP);
        }
        LogUtils.e("tlh", "onResume" );
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e("tlh", "onPause" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.e("tlh", "onStop" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("tlh", "onStop" );
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


    @Override
    public void loading() {
        LogUtils.e(TAG, "loading");
        if (displayRemoteDeviceWaitProgress.getVisibility() != View.VISIBLE) {
            displayRemoteDeviceWaitProgress.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void disPlayRemoteDesk(Bitmap bitmap) {
        if (displayRemoteDeviceWaitProgress.getVisibility() == View.VISIBLE) {
            displayRemoteDeviceWaitProgress.setVisibility(View.GONE);
        }
        LogUtils.e(TAG, "disPlayRemoteDesk---Bitmap:" + bitmap.getByteCount());
        displayRemoteDeviceSurface.setBitmap(bitmap);
    }


    @Override
    public void fila() {
        LogUtils.e(TAG, "fila");
    }

    @Override
    public void connectSucess() {
        LogUtils.e(TAG, "connectSucess");
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        changeX = (int) (x * densityX);
        changeY = (int) (y * densityY);
        if (displayPresenter == null)
            return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                displayPresenter.sendTouchData(Config.MotionEventKey.ACTION_DOWN, changeX, changeY);
                break;

            case MotionEvent.ACTION_MOVE:
                displayPresenter.sendTouchData(Config.MotionEventKey.ACTION_MOVE, changeX, changeY);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                displayPresenter.sendTouchData(Config.MotionEventKey.ACTION_UP, changeX, changeY);
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }


}
