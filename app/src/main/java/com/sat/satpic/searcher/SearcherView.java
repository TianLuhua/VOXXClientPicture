package com.sat.satpic.searcher;

import com.sat.satpic.base.BaseView;
import com.sat.satpic.bean.DeviceInfo;

import java.util.ArrayList;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public interface SearcherView extends BaseView {

    public void searchLoading();
    public void searchSuccess(ArrayList<DeviceInfo> deviceInfos);
    public void searchEnd();
    public void searchFila(String msg);
    public void searchOutTime();



}
