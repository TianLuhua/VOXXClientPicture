package com.sat.satpic.searcher;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.sat.satpic.Config;
import com.sat.satpic.base.BaseMode;
import com.sat.satpic.bean.DeviceInfo;
import com.sat.satpic.utils.IpUtils;
import com.sat.satpic.utils.LogUtils;
import com.sat.satpic.utils.ThreadPoolManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
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

public class SearcherMode implements BaseMode {

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


    private Handler searcherHandler = new SearcherHandler(this);


    public void searchRemoteDevices(Context mContext) {

        if (callBack != null) {
            callBack.searchLoading();
        }
        deviceInfos = new HashMap<>();
        searcherHandler.sendEmptyMessageDelayed(Config.HandlerGlod.IS_LOOP_SENDBROADCAST, 0);

        initNet(mContext);

        //开始计时，系统超时时间默认为10s
        if (!searcherHandler.hasMessages(Config.HandlerGlod.SEARCHER_TIMEOUT)) {
            searcherHandler.sendEmptyMessageDelayed(Config.HandlerGlod.SEARCHER_TIMEOUT,
                    Config.SystemTime.SCAN_SERVER_OUTTIME);
        }


    }

    private void initNet(Context mContext) {
        try {
            broadcastAddress = IpUtils.getBroadcastAddress();
            IpUtils.openWifiBrocast(mContext); // for some phone can
            udpBack = new DatagramSocket(Config.PortGlob.BACKPORT);

            multicastSocket = new MulticastSocket(
                    Config.PortGlob.MULTIPORT);
            if (broadcastAddress == null) {
                searcherHandler.sendEmptyMessageDelayed(Config.HandlerGlod.NET_ERROR, 0);
            } else {
                multicastSocket.joinGroup(broadcastAddress);
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.searchFila(e.getMessage());
            }
        }
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
     * 通知服务器开始发送数据到客户端
     *
     * @throws IOException
     */
    public void startRemoteService(String start) throws IOException {
        if (TextUtils.isEmpty(start))
            return;
        byte[] data = start.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length,
                broadcastAddress, Config.PortGlob.MULTIPORT);
        multicastSocket.send(packet);
        LogUtils.i(TAG, "startRemoteService:" + start);
    }

    /**
     * 接受远程设备反馈的信息，添加到deviceInfos
     */
    private synchronized void receiverBack() {
        try {


            pack = new DatagramPacket(data, data.length);
            if (!udpBack.isClosed())
                udpBack.receive(pack);
//            udpBack.setSoTimeout(Config.SystemTime.SCAN_SERVER_OUTTIME);
            back = new String(pack.getData(), pack.getOffset(),
                    pack.getLength());

            //收到服务器的答复，取消超时任务
            if (!back.isEmpty() && searcherHandler.hasMessages(Config.HandlerGlod.SEARCHER_TIMEOUT)) {
                searcherHandler.removeMessages(Config.HandlerGlod.SEARCHER_TIMEOUT);
            }
            if (back != null && back.startsWith("serverip:")) {
                String[] split = back.split(":");
                remoteServerIp = split[1];
                remoteName = split[2];

                if (!deviceInfos.containsKey(remoteServerIp)) {
                    DeviceInfo mDeviceInfo = new DeviceInfo(remoteServerIp, remoteName);
                    deviceInfos.put(remoteServerIp, mDeviceInfo);

                    searcherHandler.sendEmptyMessageDelayed(Config.HandlerGlod.SCAN_DEVICE_SUCESS,
                            500);
                }

                LogUtils.i(TAG, "hdb-------serverIp:" + remoteServerIp
                        + "   split[2]:" + split[2]);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(TAG, "tlh -------- " + e.toString());
            if (e instanceof SocketTimeoutException) {
                searcherHandler.sendEmptyMessage(Config.HandlerGlod.TIME_OUT);
            }
        }

    }

    public void onDestroy() {
        this.callBack = null;
        if (multicastSocket != null) {
//            try {
//                if (broadcastAddress != null) {
////                    multicastSocket.leaveGroup(broadcastAddress);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            multicastSocket.close();
        }
        isLoopSendBraodCast = false;
        if (udpBack != null) {
            udpBack.close();
        }
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


    public static class SearcherHandler extends Handler {
        WeakReference<SearcherMode> weakReference;

        public SearcherHandler(SearcherMode mSearcherMode) {
            weakReference = new WeakReference<SearcherMode>(mSearcherMode);
        }

        @Override
        public void handleMessage(Message msg) {
            final SearcherMode searcherMode = weakReference.get();
            if (searcherMode == null)
                return;
            switch (msg.what) {
                case Config.HandlerGlod.SCAN_DEVICE_SUCESS:
                    if (searcherMode.callBack != null && searcherMode.deviceInfos != null && searcherMode.deviceInfos.size() > 0) {
                        searcherMode.callBack.searchSuccess(searcherMode.deviceInfos);
                        searcherMode.callBack.searchEnd();
                    }

                    break;

                case Config.HandlerGlod.TIME_OUT:
                    if (searcherMode.callBack != null) {
                        searcherMode.callBack.searchOutTime();
                    }

                    break;

                case Config.HandlerGlod.IS_LOOP_SENDBROADCAST:
                    ThreadPoolManager.newInstance().addExecuteTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                searcherMode.sendBroadCast();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    if (searcherMode.isLoopSendBraodCast) {
                        sendEmptyMessageDelayed(Config.HandlerGlod.IS_LOOP_SENDBROADCAST, 200);
                    }

                    break;
                case Config.HandlerGlod.NET_ERROR:
                    if (searcherMode.callBack != null) {
                        searcherMode.callBack.networkError();
                    }
                    break;

                case Config.HandlerGlod.SEARCHER_TIMEOUT:
                    if (searcherMode.callBack != null) {
                        searcherMode.callBack.searchOutTime();
                    }
                    break;


                default:
                    break;
            }


        }
    }
}
