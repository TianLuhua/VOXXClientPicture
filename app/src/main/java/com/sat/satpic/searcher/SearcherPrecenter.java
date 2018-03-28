package com.sat.satpic.searcher;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.sat.satpic.Config;
import com.sat.satpic.base.AbstractPresenter;
import com.sat.satpic.bean.DeviceInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public class SearcherPrecenter extends AbstractPresenter<SearcherView> {

    public static final String TAG = "SearcherPrecenter";

    private SearcherMode searcherMode;
    private Context mContext;

    public SearcherPrecenter() {
        this.searcherMode = new SearcherMode();
    }

    public void searchRemoteDevices(Context mContext) {
        if (searcherMode != null) {

            searcherMode.setCallBack(new SearcherMode.CallBack() {

                @Override
                public void searchSuccess(Map deviceInfos) {
                    if (getView() != null) {
                        getView().searchSuccess(deviceInfos);

                    }
                }

                @Override
                public void searchLoading() {

                    if (getView() != null) {
                        getView().searchLoading();
                    }

                }

                @Override
                public void searchFila(String msg) {

                    if (getView() != null) {
                        getView().searchFila(msg);

                    }
                }

                @Override
                public void searchEnd() {
                    if (getView() != null) {
                        getView().searchEnd();

                    }

                }

                @Override
                public void searchOutTime() {
                    if (getView() != null) {
                        getView().searchOutTime();

                    }

                }

                @Override
                public void networkError() {
                    if (getView() != null) {
                        getView().networkError();

                    }
                }
            });
            this.mContext = mContext;
            searcherMode.searchRemoteDevices(mContext);
        }

    }


    @Override
    public void detachView() {
        super.detachView();
        searcherMode.onDestroy();
        mContext = null;
    }

    public void startDispayRemoteByServiceID(String remoteServiceID) {
        Intent intent = new Intent(Config.SystemAction.ACTIVITY_DISPAY_REMOTE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString(Config.SystemKey.KEY_BUNDLE_SERVICE_IP, remoteServiceID);
        intent.putExtras(bundle);
        if (mContext.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            try {
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, "Start Activity Error:" + remoteServiceID, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Not found DisplayActivity:" + remoteServiceID, Toast.LENGTH_SHORT).show();
        }
    }
}
