package com.sat.satpic.searcher;

import com.sat.satpic.base.BaseView;
import com.sat.satpic.bean.DeviceInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public interface SearcherView extends BaseView {

    //加载Loading
    public void searchLoading();

    //搜索到设备回调
    public void searchSuccess(Map<String, DeviceInfo> deviceInfos);
    //搜索完毕回调
    public void searchEnd();
    //搜索失败回调
    public void searchFila(String msg);
    //搜索超时回调
    public void searchOutTime();
    //网络异常回调
    public void networkError();
}
