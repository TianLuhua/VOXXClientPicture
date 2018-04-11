package com.sat.satpic;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public class Config {

   //用于管理Activity处于onPause状态时，不显示Dialog的标志
    public static boolean isFullScreen = false;

    public static class MotionEventKey {
        public static final String JACTION = "action";
        public static final String JX = "x";
        public static final String JY = "y";

        public static final int ACTION_DOWN = 0;
        public static final int ACTION_UP = 1;
        public static final int ACTION_MOVE = 2;
    }

    public static class ErrorDialogKey {
        public static final String DISPALY_DIALOG_FRAGMENT = "DiaplayDialogFragment";
        public static final String NETWORP_DIALOG_FRAGMENT = "NetworkDialogFragment";
    }

    public static class PortGlob {
        public static final int MULTIPORT = 9696;
        public static final int DATAPORT = 8686;
        public static final int TOUCHPORT = 8181;
        public static final int BACKPORT = 9191;

    }


    public static class SystemTime {
        public static final long ACTIVITY_BACKTIME = 2000;
        public static final int SCAN_SERVER_OUTTIME = 20000;
        public static final int CHECKOUT_DISPLAY_TIMEOUT = 4000;
        public static final int CHECKOUT_DISPLAY_TIMEOUT_DELAY = 0;
    }

    public static class HandlerGlod {
        public static final int CONNET_SUCCESS = 1;
        public static final int SCAN_IP_OVER = 2;
        public static final int CLEAR_FAILCOUNT = 3;
        public static final int TIME_OUT = 4;
        public static final int CONNECT_FAIL = 5;
        public static final int SCAN_DEVICE_SUCESS = 6;
        public static final int IS_LOOP_SENDBROADCAST = 7;
        public static final int SHOW_IMAGEVIEW = 8;
        public static final int NET_ERROR = 9;
        public static final int SEARCHER_TIMEOUT = 10;
    }

    public static class SystemAction {
        public static final String ACTIVITY_DISPAY_REMOTE = "com.sat.action.display.remote";
    }

    public static class SystemKey {
        public static final String KEY_BUNDLE_SERVICE_IP = "serverIp";
    }


}
