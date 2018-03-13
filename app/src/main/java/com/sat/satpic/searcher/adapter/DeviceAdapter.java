package com.sat.satpic.searcher.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sat.satpic.R;
import com.sat.satpic.bean.DeviceInfo;
import com.sat.satpic.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public class DeviceAdapter extends BaseAdapter {

    private static final String TAG = "DeviceAdapter";


    private Context mContext;
    private ArrayList<DeviceInfo> deviceInfos;


    public DeviceAdapter(Context mContext, ArrayList<DeviceInfo> deviceInfos) {
        this.mContext = mContext;
        this.deviceInfos = deviceInfos;
    }

    public ArrayList<DeviceInfo> getDeviceInfos() {
        return deviceInfos;
    }

    public void setDeviceInfos(ArrayList<DeviceInfo> deviceInfos) {
        this.deviceInfos = deviceInfos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (deviceInfos.size() > 0) ? deviceInfos.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {
            vHolder = new ViewHolder();
            convertView = View.inflate(mContext,
                    R.layout.activity_searcher_listview_item, null);
            vHolder.deviceName = (TextView) convertView
                    .findViewById(R.id.tv_list_ip);
            convertView.setTag(vHolder);
        }
        LogUtils.i(TAG, "hdb---name:" + deviceInfos.get(position).getName());
        vHolder = (ViewHolder) convertView.getTag();
        vHolder.deviceName.setText(deviceInfos.get(position).getName());
        return convertView;
    }

    class ViewHolder {
        TextView deviceName;
    }
}
