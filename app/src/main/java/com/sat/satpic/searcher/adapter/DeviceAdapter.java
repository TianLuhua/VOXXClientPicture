package com.sat.satpic.searcher.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sat.satpic.R;
import com.sat.satpic.bean.DeviceInfo;
import com.sat.satpic.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Tianluhua on 2018/3/13.
 */

public class DeviceAdapter extends BaseAdapter {

    private static final String TAG = "DeviceAdapter";


    private Context mContext;
    private List<DeviceInfo> deviceInfos = new ArrayList();


    public DeviceAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDeviceInfos(List<DeviceInfo> remoteDeviceInfos) {
        this.deviceInfos = remoteDeviceInfos;
    }

    public List<DeviceInfo> getDeviceInfos() {
        return deviceInfos;
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
        DeviceInfo deviceInfo = deviceInfos.get(position);
        String name = deviceInfo.getName();
        String address = deviceInfo.getIpAddress();
        Spannable span = new SpannableString(name + "\n" + address);
        span.setSpan(new AbsoluteSizeSpan(60), name.length(), address.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        vHolder.deviceName.setText(span);
        return convertView;
    }

    class ViewHolder {
        TextView deviceName;
    }
}
