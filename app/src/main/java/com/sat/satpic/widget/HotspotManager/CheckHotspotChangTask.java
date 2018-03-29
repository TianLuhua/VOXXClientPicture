package com.sat.satpic.widget.HotspotManager;


import android.content.Context;

import java.util.ArrayList;
import java.util.TimerTask;


/**
 * Created by Tianluhua on 2018/3/29.
 */

public class CheckHotspotChangTask extends TimerTask {

    private WifiApManager wifiApManager;

    public CheckHotspotChangTask(Context mContext ) {
        wifiApManager = new WifiApManager(mContext);
    }

    private void scan() {
        if (wifiApManager != null) {
            wifiApManager.getClientList(false, new FinishScanListener() {
                @Override
                public void onFinishScan(final ArrayList<ClientScanResult> clients) {
                    if (callBack != null) {
                        callBack.hotspotHasChanged(clients);
                    }

                }
            });
        }
    }

    @Override
    public void run() {
        scan();
    }


    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void hotspotHasChanged(ArrayList<ClientScanResult> clients);
    }
}
