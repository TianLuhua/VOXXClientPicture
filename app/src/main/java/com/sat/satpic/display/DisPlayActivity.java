package com.sat.satpic.display;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.sat.satpic.Config;
import com.sat.satpic.R;
import com.sat.satpic.base.AbstractMVPActivity;
import com.sat.satpic.utils.HideSystemUIUtils;
import com.sat.satpic.utils.LogUtils;
import com.sat.satpic.widget.ImageSurfaceView;
import com.sat.satpic.widget.NetworkDialog;

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

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.e("tlh", "onCreate");
        HideSystemUIUtils.hideSystemUI(this);
        displayPresenter = getPresenter();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        fragmentManager = getFragmentManager();
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        densityX = 1024f / (float) widthPixels;
        densityY = 600f / (float) heightPixels;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.e("tlh", "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.e("tlh", "onStart");
        remoteServiceIP = getIntent().getExtras().getString(Config.SystemKey.KEY_BUNDLE_SERVICE_IP);
        LogUtils.e(TAG, "remoteServiceIP:" + remoteServiceIP);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Config.isFullScreen = true;
        if (displayPresenter != null) {
            displayPresenter.startDisPlayRomoteDesk(remoteServiceIP);
            displayPresenter.startChekcoutHotSpotChange();
        }
        LogUtils.e("tlh", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        LogUtils.e("tlh", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.e("tlh", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (displayPresenter != null) {
            displayPresenter.removeChekcoutHotSpotChange();
        }
        LogUtils.e("tlh", "onDestroy");
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
        return new DisplayPresenter(getApplicationContext());
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
        cancelNetworkDialogFragment();
        displayRemoteDeviceSurface.setBitmap(bitmap);
    }


    @Override
    public void fila() {
        LogUtils.e(TAG, "fila");
        showNetworkDialogFragment(R.string.display_connect_fail, R.string.display_connect_fail_message);
    }

    @Override
    public void initTouchEventFila() {
        LogUtils.e(TAG, "initTouchEventFila");
        showNetworkDialogFragment(R.string.init_touch_event_fila, R.string.init_touch_event_fila_message);
    }


    @Override
    public void connectSucess() {
        LogUtils.e(TAG, "connectSucess");
        cancelNetworkDialogFragment();
    }

    @Override
    public void displayTimeout() {
        LogUtils.e(TAG, "displayTimeout");
        showNetworkDialogFragment(R.string.display_lost_host, R.string.network_please_check_the_network);
    }

    /**
     * 系统提示对话框
     *
     * @param titleID
     * @param messageID
     */
    private void showNetworkDialogFragment(int titleID, int messageID) {
        LogUtils.e(TAG, "showNetworkDialogFragment--111111111111111");
        if (Config.isFullScreen)
            if (fragmentManager.findFragmentByTag(Config.ErrorDialogKey.DISPALY_DIALOG_FRAGMENT) == null) {
                NetworkDialog dialog = new NetworkDialog();
                dialog.setTitle(titleID);
                dialog.setMessage(messageID);
                dialog.setPositoveButton(R.string.ok);
                dialog.setCancelable(false);
                dialog.setNetworkDialogInterface(new NetworkDialog.NetworkDialogInterface() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DisPlayActivity.this.finish();
                    }
                });
                dialog.show(fragmentManager, Config.ErrorDialogKey.DISPALY_DIALOG_FRAGMENT);
            }
    }

    /**
     * 当服务器有数据时，确保错误对话框不显示
     */
    private void cancelNetworkDialogFragment() {
        if (Config.isFullScreen) {
            Fragment fragment = fragmentManager.findFragmentByTag(Config.ErrorDialogKey.DISPALY_DIALOG_FRAGMENT);
            if (fragment != null) {
                LogUtils.e(TAG, "cancelNetworkDialogFragment");
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Config.isFullScreen = false;
        LogUtils.e(TAG, "onSaveInstanceState");
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
