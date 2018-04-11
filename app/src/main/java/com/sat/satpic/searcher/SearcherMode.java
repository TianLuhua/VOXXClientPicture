package com.sat.satpic.searcher;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.sat.satpic.Config;
import com.sat.satpic.bean.DeviceInfo;
import com.sat.satpic.utils.IpUtils;
import com.sat.satpic.utils.LogUtils;
import com.sat.satpic.utils.ThreadUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Tianluhua on 2018/3/13.
 */

public class SearcherMode {

    public static final String TAG = "SearcherMode";


    private MulticastSocket multicastSocket;
    private InetAddress broadcastAddress;
    private DatagramSocket udpBack;
    private Map<String, DeviceInfo> deviceInfos;
    private String remoteServerIp;
    private String remoteName;
    private CallBack callBack;

    private boolean isLoopSendBraodCast = true;
    private byte[] data = new byte[50];
    private DatagramPacket pack;
    private String back;

    private Object lock = new Object();


    private Handler mAsyncEventHandler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Config.HandlerGlod.SCAN_DEVICE_SUCESS:
                    if (callBack != null && deviceInfos != null && deviceInfos.size() > 0) {
                        callBack.searchSuccess(deviceInfos);
                        callBack.searchEnd();
                    }

                    break;

                case Config.HandlerGlod.TIME_OUT:
                    if (callBack != null) {
                        callBack.searchOutTime();
                    }

                    break;

                case Config.HandlerGlod.IS_LOOP_SENDBROADCAST:
                    ThreadUtils.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sendBroadCast();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    if (isLoopSendBraodCast) {
                        mAsyncEventHandler.sendEmptyMessageDelayed(Config.HandlerGlod.IS_LOOP_SENDBROADCAST, 1000);
                    }

                    break;
                case Config.HandlerGlod.NET_ERROR:
                    if (callBack != null) {
                        callBack.networkError();
                    }
                    break;

                case Config.HandlerGlod.SEARCHER_TIMEOUT:
                    if (callBack != null) {
                        callBack.searchOutTime();
                    }
                    break;


                default:
                    break;
            }

        }
    };


    public void searchRemoteDevices(Context mContext) {

        if (callBack != null) {
            callBack.searchLoading();
        }
        deviceInfos = new HashMap<>();
        mAsyncEventHandler.sendEmptyMessageDelayed(Config.HandlerGlod.IS_LOOP_SENDBROADCAST, 500);

        //开始计时，系统超时时间默认为5s
        if (!mAsyncEventHandler.hasMessages(Config.HandlerGlod.SEARCHER_TIMEOUT)) {
            mAsyncEventHandler.sendEmptyMessageDelayed(Config.HandlerGlod.SEARCHER_TIMEOUT,
                    Config.SystemTime.SCAN_SERVER_OUTTIME);
        }
        findDevice(mContext);
        startUdpBroadcast();


    }

    private void findDevice(Context mContext) {
        try {
            broadcastAddress = IpUtils.getBroadcastAddress();
        } catch (IOException e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.searchFila(e.getMessage());
            }
        }
        IpUtils.openWifiBrocast(mContext); // for some phone can
    }

    private void startUdpBroadcast() {
        ThreadUtils.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (multicastSocket == null) {
                        multicastSocket = new MulticastSocket(
                                Config.PortGlob.MULTIPORT);
                        if (broadcastAddress == null) {
                            mAsyncEventHandler.sendEmptyMessageDelayed(Config.HandlerGlod.NET_ERROR, 0);
                        } else {
                            multicastSocket.joinGroup(broadcastAddress);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
//                    mAsyncEventHandler.sendEmptyMessageDelayed(Config.HandlerGlod.NET_ERROR, 0);

                }
            }
        });

    }

    /**
     * 通过向广播地址发送信息，让服务端做出回应
     *
     * @throws IOException
     */
    private void sendBroadCast() throws IOException {
        String ipAddress = IpUtils.getHostIP();
        LogUtils.i(TAG, "hdb----send---ipAddress:" + ipAddress);
        if (ipAddress != null) {
            byte[] data = ("phoneip:" + ipAddress).getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length,
                    broadcastAddress, Config.PortGlob.MULTIPORT);
            LogUtils.i(TAG, "hdb----send---broadcastAddress:" + broadcastAddress);
            multicastSocket.send(packet);
            receiverBack();
        }

    }

    /**
     * 接受远程设备反馈的信息，添加到deviceInfos
     */
    private synchronized void receiverBack() {
        try {
            if (udpBack == null) {
                udpBack = new DatagramSocket(Config.PortGlob.BACKPORT);
            }

            pack = new DatagramPacket(data, data.length);
            udpBack.receive(pack);
//            udpBack.setSoTimeout(Config.SystemTime.SCAN_SERVER_OUTTIME);
            back = new String(pack.getData(), pack.getOffset(),
                    pack.getLength());

            //收到服务器的答复，取消超时任务
            if (!back.isEmpty() && mAsyncEventHandler.hasMessages(Config.HandlerGlod.SEARCHER_TIMEOUT)) {
                mAsyncEventHandler.removeMessages(Config.HandlerGlod.SEARCHER_TIMEOUT);
            }
            if (back != null && back.startsWith("serverip:")) {
                String[] split = back.split(":");
                remoteServerIp = split[1];
                remoteName = split[2];

                if (!deviceInfos.containsKey(remoteServerIp)) {
                    DeviceInfo mDeviceInfo = new DeviceInfo(remoteServerIp, remoteName);
                    deviceInfos.put(remoteServerIp, mDeviceInfo);

                    byte[] over = "pic".getBytes();
                    DatagramPacket packet = new DatagramPacket(over,
                            over.length, broadcastAddress,
                            Config.PortGlob.MULTIPORT);
                    multicastSocket.send(packet);
                    mAsyncEventHandler.sendEmptyMessageDelayed(Config.HandlerGlod.SCAN_DEVICE_SUCESS,
                            0);
                }

                LogUtils.i(TAG, "hdb-------serverIp:" + remoteServerIp
                        + "   split[2]:" + split[2]);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof SocketTimeoutException) {
                mAsyncEventHandler.sendEmptyMessage(Config.HandlerGlod.TIME_OUT);
            }
        }

    }

    public void onDestroy() {
        this.callBack = null;
//        if (multicastSocket != null) {
//            try {
//                if (broadcastAddress != null) {
//
//                    multicastSocket.leaveGroup(broadcastAddress);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            multicastSocket.close();
//        }
        if (udpBack != null) {
//            udpBack.close();
        }
        isLoopSendBraodCast = false;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;

    }

    /**
     * 数据以及状态回调接口
     */
    public interface CallBack {

        public void searchLoading();

        public void searchSuccess(Map<String, DeviceInfo> deviceInfos);

        public void searchEnd();

        public void searchFila(String msg);

        public void searchOutTime();

        public void networkError();
    }
}
