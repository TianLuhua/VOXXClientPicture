package com.sat.satpic.display;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.sat.satpic.Config;
import com.sat.satpic.utils.ByteUtils;
import com.sat.satpic.utils.LogUtils;
import com.sat.satpic.utils.ThreadPoolManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public class DisplayMode {

    public static final String TAG = "DisplayMode";

    private Socket dataSocket;
    private boolean isRun = true;

    private byte[] receveByteslen = new byte[3];
    private byte[] receveBytes;

    private Bitmap bm;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case Config.HandlerGlod.TOUCH_EVENT_CONNECT_FAIL:
                    if (callBack != null) {
                        callBack.fila();
                    }
                    break;

                case Config.HandlerGlod.CONNET_SUCCESS:
                    if (callBack != null) {
                        callBack.connectSucess();
                    }
                    break;
                case Config.HandlerGlod.SHOW_IMAGEVIEW:
                    if (callBack != null && bm != null) {
                        callBack.disPlayRemoteDesk(bm);
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    /**
     * 开启远程服务端,通过远程服务器的
     *
     * @param serverIp
     */
    public void startServer(final String serverIp) {
        if (callBack != null) {
            callBack.loading();
        }
        ThreadPoolManager.newInstance().addExecuteTask(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtils.i(TAG, "hdb---data--连接start");
                    if (dataSocket != null) {
                        LogUtils.i(TAG, "hdb---data--dataSocket != null");
                        return;
                    }
                    dataSocket = new Socket(serverIp, Config.PortGlob.DATAPORT);// 10.0.0.24
                    DataInputStream dis = new DataInputStream(
                            dataSocket.getInputStream());
                    LogUtils.i(TAG, "hdb---data--连接成功");
                    mHandler.sendEmptyMessage(Config.HandlerGlod.CONNET_SUCCESS);
                    isRun = true;
                    while (isRun) {
                        readFile(dis);
                    }
                    mHandler.sendEmptyMessage(Config.HandlerGlod.CONNECT_FAIL);
                } catch (Exception ex) {
                    LogUtils.e(TAG, "hdb--dataServer-ex:" + ex.toString());
                    mHandler.sendEmptyMessage(Config.HandlerGlod.CONNECT_FAIL);
                }
            }
        });
    }


    /**
     * 读取远程通过TCP 传输过来的数据
     *
     * @param dis
     * @throws IOException
     */
    private synchronized void readFile(DataInputStream dis) throws IOException {
        LogUtils.i(TAG, "hdb---readFile");
        dis.read(receveByteslen);
        int length = ByteUtils.bufferToInt(receveByteslen);
        LogUtils.i(TAG, "hdb---readFile--length:" + length);
        receveBytes = new byte[length];
        dis.readFully(receveBytes);
        bm = BitmapFactory.decodeByteArray(receveBytes, 0, length);
        LogUtils.i(TAG, "hdb----接收文件<>成功-------bm:" + bm);
        if (bm != null) {
            LogUtils.i(TAG, "hdb-------bm:" + bm.getByteCount());
            mHandler.sendEmptyMessage(Config.HandlerGlod.SHOW_IMAGEVIEW);
        }
    }

    private CallBack callBack;

    public DisplayMode(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {

        public void loading();

        public void disPlayRemoteDesk(Bitmap bitmap);

        public void fila();

        public void connectSucess();

    }

    public void onDestroy() {
        receveBytes = null;
        dataSocket = null;
    }

}
