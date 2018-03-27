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
import java.util.ArrayList;


/**
 * Created by Tianluhua on 2018/3/13.
 */

public class SearcherMode {

    public static final String TAG = "SearcherMode";


    private MulticastSocket multicastSocket;
    private InetAddress broadcastAddress;
    private DatagramSocket udpBack;
    private ArrayList<DeviceInfo> remoteDeviceInfos;
    private String remoteServerIp;
    private CallBack callBack;

    private boolean isLoopSendBraodCast = true;
    private byte[] data = new byte[50];
    private DatagramPacket pack;
    private String back;


    private Handler mAsyncEventHandler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Config.HandlerGlod.SCAN_IP_OVER:
                    if (callBack != null) {
                        callBack.searchSuccess(remoteDeviceInfos);
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
                        mAsyncEventHandler.sendEmptyMessageDelayed(Config.HandlerGlod.IS_LOOP_SENDBROADCAST, 2000);
                    }

                    break;
                case Config.HandlerGlod.NET_ERROR:
                    if (callBack != null) {
                        callBack.networkError();
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
        remoteDeviceInfos = new ArrayList<DeviceInfo>();
        mAsyncEventHandler.sendEmptyMessageDelayed(Config.HandlerGlod.IS_LOOP_SENDBROADCAST, 2000);
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

                }
            }
        });

    }

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

    private void receiverBack() {
        try {
            if (udpBack == null) {
                udpBack = new DatagramSocket(Config.PortGlob.BACKPORT);
            }

            pack = new DatagramPacket(data, data.length);
            udpBack.receive(pack);
            udpBack.setSoTimeout(Config.SystemTime.SCAN_SERVER_OUTTIME);
            back = new String(pack.getData(), pack.getOffset(),
                    pack.getLength());
            if (back != null && back.startsWith("serverip:")) {
                String[] split = back.split(":");
                remoteServerIp = split[1];

                if (!hasDeviceInfo(remoteDeviceInfos, remoteServerIp)) {
                    LogUtils.i(TAG, "hdb-------in:");
                    DeviceInfo mDeviceInfo = new DeviceInfo(remoteServerIp, split[2]);
                    remoteDeviceInfos.add(mDeviceInfo);

                    byte[] over = "pic".getBytes();
                    DatagramPacket packet = new DatagramPacket(over,
                            over.length, broadcastAddress,
                            Config.PortGlob.MULTIPORT);
                    multicastSocket.send(packet);
                    mAsyncEventHandler.sendEmptyMessageDelayed(Config.HandlerGlod.SCAN_IP_OVER,
                            2000);
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


    private boolean hasDeviceInfo(ArrayList<DeviceInfo> Infos, String ip) {
        for (int i = 0; i < Infos.size(); i++) {
            if (ip != null && ip.equals(Infos.get(i).getIpAddress())) {
                return true;
            }
        }
        return false;
    }


    public void onDestroy() {
        this.callBack = null;
        if (multicastSocket != null) {
            try {
                if (broadcastAddress != null) {

                    multicastSocket.leaveGroup(broadcastAddress);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            multicastSocket.close();
        }
        if (udpBack != null) {
            udpBack.close();
        }
        isLoopSendBraodCast = false;


    }


    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;

    }

    public interface CallBack {

        public void searchLoading();

        public void searchSuccess(ArrayList<DeviceInfo> deviceInfos);

        public void searchEnd();

        public void searchFila(String msg);

        public void searchOutTime();

        public void networkError();
    }
}
