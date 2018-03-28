package com.sat.satpic.display;

import android.graphics.Bitmap;
import android.os.Handler;

import com.sat.satpic.Config;
import com.sat.satpic.base.AbstractPresenter;
import com.sat.satpic.utils.LogUtils;
import com.sat.satpic.utils.ThreadUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public class DisplayPresenter extends AbstractPresenter<DisplayView> {

    public static final String TAG = "DisplayPresenter";

    private Socket touchSocket;
    private DataOutputStream dos;

    private JSONObject jObject = new JSONObject();
    private byte[] intToByte = new byte[1];
    private byte[] data;
    private byte[] jBytes;

    private DisplayMode displayMode;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case Config.HandlerGlod.CONNECT_FAIL:
                    if (getView() != null) {
                        getView().fila();
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


    public DisplayPresenter() {
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

            }

            @Override
            public void displayTimeout() {

            }
        });
    }

    public void startDisPlayRomoteDesk(String serverIp) {
        startTouchServer(serverIp);
        displayMode.startServer(serverIp);
    }


    private void startTouchServer(final String serverIp) {

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
                    mHandler.sendEmptyMessage(Config.HandlerGlod.CONNECT_FAIL);
                }
            }
        });

    }


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
    public void detachView() {
        super.detachView();
        if (displayMode != null) {
            displayMode.onDestroy();
        }
    }
}
