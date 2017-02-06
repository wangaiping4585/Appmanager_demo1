package com.example.administrator.appmanager_frg_vp_demo1.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.appmanager_frg_vp_demo1.R;
import com.example.administrator.appmanager_frg_vp_demo1.entity.AppInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */
public class AppInfo_sys_adapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<AppInfo> mSysAppInfoList;
    private PackageManager mPm;

    public AppInfo_sys_adapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mPm = context.getPackageManager();
    }


    public void setmAppInfoList(List<AppInfo> list) {
        this.mSysAppInfoList = list;
    }

    @Override
    public int getCount() {
        return (mSysAppInfoList == null) ? 0 : mSysAppInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSysAppInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            //反射布局
            convertView = mInflater.inflate(R.layout.item_listview_sys, null);
            //反射布局内控件
            holder.iv_sys_logo = (ImageView) convertView.findViewById(R.id.iv_sys_logo);
            holder.tv_sys_appname = (TextView) convertView.findViewById(R.id.tv_sys_appname);
            holder.tv_sys_version = (TextView) convertView.findViewById(R.id.tv_sys_version);
            holder.tv_sys_size = (TextView) convertView.findViewById(R.id.tv_sys_size);
            holder.tv_sys_packagename = (TextView) convertView.findViewById(R.id.tv_sys_packagename);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //给控件赋值
        //拆箱
        AppInfo appInfo = mSysAppInfoList.get(position);
        holder.iv_sys_logo.setImageDrawable(appInfo.getApplicationInfo().loadIcon(mPm));
/*
        if (AppActivity.KEYWORD == null) {//没有搜索
            holder.tv_sys_appname.setText(appInfo.getAppName());
        } else {
            holder.tv_sys_appname.setText(Util.highLightText(appInfo.getAppName(), AppActivity.KEYWORD));
        }
*/
        holder.tv_sys_appname.setText(appInfo.getAppName());
        holder.tv_sys_version.setText(appInfo.getVersionName());
        holder.tv_sys_size.setText(appInfo.getSize());
        holder.tv_sys_packagename.setText(appInfo.getPackageName());

        return convertView;
    }

    public class ViewHolder {
        ImageView iv_sys_logo;
        TextView tv_sys_appname;
        TextView tv_sys_version;
        TextView tv_sys_size;
        TextView tv_sys_packagename;
    }
}
