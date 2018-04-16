package com.sat.satpic.display;

import android.graphics.Bitmap;

import com.sat.satpic.base.BaseView;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public interface DisplayView extends BaseView {

    //Loading回调，通知界面
    public void loading();

    //将远程得到的数据回调到UI线程进行渲染
    public void disPlayRemoteDesk(Bitmap bitmap);

    //连接远程服务器失败回调
    public void fila();

    //初始化远程事件失败
    public void initTouchEventFila();

    //连接远程服务器的成功回调
    public void connectSucess();

    //由于网络等各种原因引起的超时回调
    public void displayTimeout();
}
