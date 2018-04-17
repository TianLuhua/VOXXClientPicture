package com.sat.satpic.display;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.sat.satpic.Config;
import com.sat.satpic.base.AbstractPresenter;
import com.sat.satpic.utils.LogUtils;
import com.sat.satpic.utils.ThreadUtils;
import com.sat.satpic.widget.HotspotManager.CheckHotspotChangTask;
import com.sat.satpic.widget.HotspotManager.ClientScanResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by Tianluhua on 2018/3/13.
 * 用于协调UI和数据
 */

public class DisplayPresenter extends AbstractPresenter<DisplayView> implements CheckHotspotChangTask.CallBack {

    public static final String TAG = "DisplayPresenter";

    private boolean isHotSpot = false;

    private String serverIp;
    private Socket touchSocket;
    private DataOutputStream dos;

    private JSONObject jObject = new JSONObject();
    private byte[] intToByte = new byte[1];
    private byte[] data;
    private byte[] jBytes;

    private DisplayMode displayMode;

    private Context mContext;
    private Timer checkHotSpotTimer;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case Config.HandlerGlod.TOUCH_EVENT_CONNECT_FAIL:
                    if (getView() != null) {
                        getView().initTouchEventFila();
                    }
                    break;

                case Config.HandlerGlod.CONNET_SUCCESS:
                    if (getView() != null) {
                        getView().connectSucess();
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };


    public DisplayPresenter(Context mContext) {

        this.mContext = mContext;
        displayMode = new DisplayMode(new DisplayMode.CallBack() {
            @Override
            public void loading() {
                if (getView() != null) {
                    getView().loading();
                }
            }

            @Override
            public void disPlayRemoteDesk(Bitmap bitmap) {
                if (getView() != null) {
                    getView().disPlayRemoteDesk(bitmap);
                } else {
                    //todo
                    throw new NullPointerException("DisplayPresenter ---> disPlayRemoteDesk --->getView()==null ");
                }
            }

            @Override
            public void fila() {
                if (getView() != null) {
                    getView().fila();
                }
            }

            @Override
            public void connectSucess() {
                if (getView() != null) {
                    getView().connectSucess();
                }
            }

        });

        checkHotSpotTimer = new Timer();


    }

    /**
     * 与远程服务端建立连接
     *
     * @param serverIp 远程服务端设备的
     */
    public void startDisPlayRomoteDesk(String serverIp) {
        this.serverIp = serverIp;
        startTouchServer();
        displayMode.startServer(serverIp);
    }

    /**
     * 监听热点连接情况
     */
    public void startChekcoutHotSpotChange() {
        LogUtils.e("tlh", "startChekcoutHotSpotChange--111");
        startChekcoutHotSpotChange(Config.SystemTime.CHECKOUT_DISPLAY_TIMEOUT_DELAY, Config.SystemTime.CHECKOUT_DISPLAY_TIMEOUT);
    }

    /**
     * 定时轮询检测热点是否断开连接
     *
     * @param delay
     * @param period
     */
    public void startChekcoutHotSpotChange(long delay, long period) {
        if (checkHotSpotTimer != null) {
            LogUtils.e("tlh", "startChekcoutHotSpotChange----222");
            CheckHotspotChangTask checkHotspotChangTask = new CheckHotspotChangTask(mContext);
            checkHotspotChangTask.setCallBack(this);
            checkHotSpotTimer.scheduleAtFixedRate(checkHotspotChangTask, delay, period);
        }
    }

    /**
     * 移除定时检测
     */
    public void removeChekcoutHotSpotChange() {
        if (checkHotSpotTimer != null) {
            LogUtils.e("tlh", "removeChekcoutHotSpotChange");
            checkHotSpotTimer.cancel();
        }

    }

    /**
     * 和远程服务器建立TCP连接，用于屏幕事件的交互
     */
    private void startTouchServer() {

        ThreadUtils.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtils.e(TAG, "tlh--startTouchServer-:" + serverIp);
                    if (touchSocket != null) return;
                    touchSocket = new Socket(serverIp,
                            Config.PortGlob.TOUCHPORT);
                    dos = new DataOutputStream(touchSocket.getOutputStream());
                } catch (Exception e) {
                    LogUtils.e(TAG, "hdb--touchServer-ex:" + e.toString());
                    mHandler.sendEmptyMessage(Config.HandlerGlod.TOUCH_EVENT_CONNECT_FAIL);
                }
            }
        });

    }

    /**
     * 发送本地屏幕事件到远程服务端
     *
     * @param actionType 时间类型
     * @param changeX    事件对应的 X 值
     * @param changeY    事件对应的  值
     */
    public void sendTouchData(final int actionType, final int changeX, final int changeY) {
        LogUtils.i(TAG, "sendTouchData---action:" + actionType + "  changeX:" + changeX
                + "  changeY:" + changeY);
        ThreadUtils.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                if (dos != null) {
                    if (changeX >= 0 && changeX <= 1024 && changeY >= 0 && changeY <= 600) {

                        try {
                            jObject.put(Config.MotionEventKey.JACTION, actionType);
                            jObject.put(Config.MotionEventKey.JX, changeX);
                            jObject.put(Config.MotionEventKey.JY, changeY);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        jBytes = jObject.toString().getBytes();
                        intToByte[0] = (byte) jBytes.length;
                        data = new byte[jBytes.length + 1];
                        System.arraycopy(intToByte, 0, data, 0, 1);
                        System.arraycopy(jBytes, 0, data, 1, jBytes.length);
                        LogUtils.i(TAG, "hdb----data:" + new String(data));
                        try {
                            dos.write(data);
                            dos.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }

            }
        });
    }

    @Override
    public void hotspotHasChanged(ArrayList<ClientScanResult> clients) {
        LogUtils.e(TAG, "hotspotHasChanged");
        //如果app处于后台时，getView()会返回null
        if (Config.isFullScreen)
            for (ClientScanResult clientScanResult : clients) {
                //代表设备是通热点链接到手机。因为设备的信息会保存到手机上，当设备连接手机的热点的时候。
                if (serverIp.equals(clientScanResult.getIpAddr())) {
                    isHotSpot = true;
                    //当设备断开热点后，信息可能还保存在手机里面。但是此时设备是不可达的
                    if (!clientScanResult.isReachable()) {
                        if (getView() != null) {
                            getView().displayTimeout();
                        } else {
//                        throw new NullPointerException("getView() is null (hotspotHasChanged)");
                            LogUtils.i(TAG, "hotspotHasChanged:" + "getView() is null (hotspotHasChanged)");
                        }
                    }
                }
            }
    }

    @Override
    public void detachView() {
        super.detachView();
        if (displayMode != null) {
            displayMode.onDestroy();
        }
        if (checkHotSpotTimer != null) {
            LogUtils.i(TAG, "checkHotSpotTimer canceled");
            checkHotSpotTimer.cancel();
        }
    }


}
