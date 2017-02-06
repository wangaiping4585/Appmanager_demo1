package com.example.administrator.appmanager_frg_vp_demo1.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.appmanager_frg_vp_demo1.R;
import com.example.administrator.appmanager_frg_vp_demo1.activity.FileActivity;
import com.example.administrator.appmanager_frg_vp_demo1.entity.AppInfo;
import com.example.administrator.appmanager_frg_vp_demo1.fragment.AppThirdFragment;
import com.example.administrator.appmanager_frg_vp_demo1.mInter.Iuninstall;
import com.example.administrator.appmanager_frg_vp_demo1.util.Util;

import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */
public class AppInfo_third_adapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Iuninstall mIuninstall;
    private List<AppInfo> mAppInfoList;
    private PackageManager mPm;

    public AppInfo_third_adapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mPm = context.getPackageManager();
    }

    public void setmIuninstall(Iuninstall inter) {
        this.mIuninstall = inter;
    }

    public void setmAppInfoList(List<AppInfo> list) {
        this.mAppInfoList = list;
    }

    @Override
    public int getCount() {
        return (mAppInfoList == null) ? 0 : mAppInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppInfoList.get(position);
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
            convertView = mInflater.inflate(R.layout.item_listview_third, null);
            //反射布局内控件
            holder.iv_logo = (ImageView) convertView.findViewById(R.id.iv_third_logo);
            holder.tv_appname = (TextView) convertView.findViewById(R.id.tv_third_appname);
            holder.tv_version = (TextView) convertView.findViewById(R.id.tv_third_version);
            holder.tv_size = (TextView) convertView.findViewById(R.id.tv_third_size);
            //holder.tv_packagename = (TextView) convertView.findViewById(R.id.tv_third_packagename);
            holder.btn_delete = (Button) convertView.findViewById(R.id.btn_third_delete);
            //holder.ll_delete = (LinearLayout) convertView.findViewById(R.id.ll_third_item_right);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //给控件赋值
        //拆箱
        AppInfo appInfo = mAppInfoList.get(position);
        holder.iv_logo.setImageDrawable(appInfo.getApplicationInfo().loadIcon(mPm));

        //todo 添加搜索高亮

        //if (appInfo.getAppName().indexOf(AppThirdFragment.KEYWORD) != -1) {
            if (AppThirdFragment.KEYWORD == null) {//没有搜索
                holder.tv_appname.setText(appInfo.getAppName());
            } else {
                holder.tv_appname.setText(Util.highLightText(appInfo.getAppName(), AppThirdFragment.KEYWORD));
            }
        //}



        //holder.tv_appname.setText(appInfo.getAppName());
        holder.tv_version.setText(appInfo.getVersionName());
        holder.tv_size.setText(appInfo.getSize());
        //holder.tv_packagename.setText(appInfo.getPackageName());
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIuninstall.btnClick_uninstall(position);
            }
        });
        /*holder.ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIuninstall.btnClick_uninstall(position);
            }
        });*/

        return convertView;
    }

    public class ViewHolder {
        ImageView iv_logo;
        TextView tv_appname;
        TextView tv_version;
        TextView tv_size;
        TextView tv_packagename;
        LinearLayout ll_delete;
        Button btn_delete;
    }
}