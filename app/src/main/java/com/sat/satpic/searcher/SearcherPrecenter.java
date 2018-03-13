package com.sat.satpic.searcher;

import android.content.Context;

import com.sat.satpic.base.AbstractPresenter;
import com.sat.satpic.bean.DeviceInfo;

import java.util.ArrayList;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public class SearcherPrecenter extends AbstractPresenter<SearcherView> {

    public static final String TAG = "SearcherPrecenter";

    private SearcherMode searcherMode;


    public SearcherPrecenter() {
        this.searcherMode = new SearcherMode();
    }

    public void searchRemoteDevices(Context mContext) {
        if (searcherMode != null) {

            searcherMode.setCallBack(new SearcherMode.CallBack() {
                @Override
                public void searchSuccess(ArrayList<DeviceInfo> deviceInfos) {
                    // TODO Auto-generated method stub
                    if (getView() != null) {
                        getView().searchSuccess(deviceInfos);

                    }

                }

                @Override
                public void searchLoading() {
                    // TODO Auto-generated method stub

                    if (getView() != null) {
                        getView().searchLoading();
                    }

                }

                @Override
                public void searchFila(String msg) {
                    // TODO Auto-generated method stub

                    if (getView() != null) {
                        getView().searchFila(msg);

                    }
                }

                @Override
                public void searchEnd() {
                    // TODO Auto-generated method stub
                    if (getView() != null) {
                        getView().searchEnd();

                    }

                }

                @Override
                public void searchOutTime() {
                    // TODO Auto-generated method stub
                    if (getView() != null) {
                        getView().searchOutTime();

                    }

                }
            });

            searcherMode.searchRemoteDevices(mContext);
        }

    }


    @Override
    public void detachView() {
        super.detachView();
        searcherMode.onDestroy();
    }
}
