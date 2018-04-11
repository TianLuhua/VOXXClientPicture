package com.sat.satpic.searcher;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sat.satpic.Config;
import com.sat.satpic.R;
import com.sat.satpic.base.AbstractMVPActivity;
import com.sat.satpic.bean.DeviceInfo;
import com.sat.satpic.searcher.adapter.DeviceAdapter;
import com.sat.satpic.utils.HideSystemUIUtils;
import com.sat.satpic.utils.LogUtils;
import com.sat.satpic.utils.VersionNumberUtils;
import com.sat.satpic.widget.NetworkDialog;
import com.sat.satpic.widget.RadarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public class SearcherActivity extends AbstractMVPActivity<SearcherView, SearcherPrecenter> implements SearcherView {

    public static final String TAG = "SearcherActivity";

    private ListView display_remote_devices_list;
    private RadarView searCherView;
    private TextView searCherView_text;
    private TextView versionNumber;

    private SearcherPrecenter searcherPrecenter;
    private DeviceAdapter deviceAdapter;

    private List<DeviceInfo> remoteDeviceInfos;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HideSystemUIUtils.hideSystemUI(this);
        searcherPrecenter = getPresenter();
        fragmentManager = getFragmentManager();
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_searcher;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Config.isFullScreen = true;
        startSearchService();

    }

    /**
     * 搜索引擎，查找远程设备
     */
    public void startSearchService() {
        if (searcherPrecenter != null) {
            searcherPrecenter.searchRemoteDevices(getApplicationContext());
        }
    }

    @Override
    protected void initView() {
        searCherView = (RadarView) findViewById(R.id.iv_search);
        searCherView_text = (TextView) findViewById(R.id.iv_search_text);
        versionNumber = findViewById(R.id.iv_search_version_number);
        versionNumber.setText(VersionNumberUtils.getVersion(getApplicationContext()));
        display_remote_devices_list = (ListView) findViewById(R.id.remote_device_list);
        deviceAdapter = new DeviceAdapter(getApplicationContext());
        display_remote_devices_list.setAdapter(deviceAdapter);
        display_remote_devices_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String serverIp = remoteDeviceInfos.get(position).getIpAddress();
                Toast.makeText(getApplicationContext(),
                        "serverIp:" + serverIp, Toast.LENGTH_SHORT).show();
                searcherPrecenter.startDispayRemoteByServiceID(serverIp);
                SearcherActivity.this.finish();
            }
        });
    }

    @Override
    protected SearcherPrecenter createPresenter() {
        return new SearcherPrecenter();
    }

    @Override
    public void searchLoading() {
        LogUtils.e(TAG, "searchLoading");
        if (searCherView != null) {
            searCherView.startScan();
        }
    }


    @Override
    public void searchSuccess(Map<String, DeviceInfo> deviceInfos) {

        remoteDeviceInfos = new ArrayList<>(deviceInfos.values());
        if (deviceAdapter != null) {
            deviceAdapter.setDeviceInfos(remoteDeviceInfos);
            deviceAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void searchEnd() {
        LogUtils.e(TAG, "searchEnd");
        showDevice();
    }

    @Override
    public void searchFila(String msg) {
        LogUtils.e(TAG, "searchFila---msg:" + msg);

    }

    @Override
    public void searchOutTime() {
        LogUtils.e(TAG, "searchOutTime");
        showNetworkDialogFragment(R.string.searcher_out_time_title, R.string.network_please_check_the_network);
    }

    @Override
    public void networkError() {
        LogUtils.e(TAG, "netError");
        showNetworkDialogFragment(R.string.network_dialog_title, R.string.network_please_check_the_network);
    }

    /**
     * 系统提示对话框
     *
     * @param titleID
     * @param messageID
     */
    private void showNetworkDialogFragment(int titleID, int messageID) {
        if (Config.isFullScreen)
            if (fragmentManager.findFragmentByTag(Config.ErrorDialogKey.NETWORP_DIALOG_FRAGMENT) == null) {
                NetworkDialog dialog = new NetworkDialog();
                dialog.setTitle(titleID);
                dialog.setMessage(messageID);
                dialog.setPositoveButton(R.string.ok);
                dialog.setCancelable(false);
                dialog.setNetworkDialogInterface(new NetworkDialog.NetworkDialogInterface() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SearcherActivity.this.finish();
                    }
                });
                dialog.show(fragmentManager, Config.ErrorDialogKey.NETWORP_DIALOG_FRAGMENT);
            }
    }

    /**
     * 远程服务器有反馈，就显示在UI上
     */
    private void showDevice() {
        if (searCherView != null)
            searCherView.clearAnimation();

        if (searCherView.getVisibility() == View.VISIBLE)
            searCherView.setVisibility(View.GONE);

        if (versionNumber.getVisibility() == View.VISIBLE)
            versionNumber.setVisibility(View.GONE);

        if (searCherView_text.getVisibility() == View.VISIBLE)
            searCherView_text.setVisibility(View.GONE);

        if (display_remote_devices_list.getVisibility() != View.VISIBLE)
            display_remote_devices_list.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Config.isFullScreen = false;
    }
}
