package com.sat.satpic.display;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.sat.satpic.Config;
import com.sat.satpic.utils.ByteUtils;
import com.sat.satpic.utils.LogUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public class DisplayMode {

    public static final String TAG = "DisplayMode";

    private Socket dataSocket;
    private boolean isRun = true;

    private Bitmap bm;
    private byte[] sendBytes;
    private byte[] len;


    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case Config.HandlerGlod.CONNECT_FAIL:
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


    private ExecutorService executorService = newFixThreadPool(10);

    private ExecutorService newFixThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }


    public void startServer(final String serverIp) {
        if (callBack != null) {
            callBack.loading();
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtils.i(TAG, "hdb---data--连接start");
                    if (dataSocket!=null){
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


    private synchronized void readFile(DataInputStream dis) throws IOException {
        LogUtils.i(TAG, "hdb---readFile");
        len = new byte[3];
        dis.read(len);
        int length = ByteUtils.bufferToInt(len);
        LogUtils.i(TAG, "hdb---readFile--length:" + length);
        sendBytes = new byte[length];
        dis.readFully(sendBytes);
        bm = BitmapFactory.decodeByteArray(sendBytes, 0, length);
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
        sendBytes = null;
        bm = null;
        dataSocket = null;
    }

}
