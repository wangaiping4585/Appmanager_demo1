package com.example.administrator.appmanager_frg_vp_demo1.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.example.administrator.appmanager_frg_vp_demo1.activity.AppActivity;
import com.example.administrator.appmanager_frg_vp_demo1.R;
import com.example.administrator.appmanager_frg_vp_demo1.adapter.AppInfo_sys_adapter;
import com.example.administrator.appmanager_frg_vp_demo1.entity.AppInfo;
import com.example.administrator.appmanager_frg_vp_demo1.entity.AppInfoList;
import com.example.administrator.appmanager_frg_vp_demo1.util.SPUtil;
import com.example.administrator.appmanager_frg_vp_demo1.util.Util;

import java.util.List;


public class AppSysFragment extends Fragment implements AdapterView.OnItemLongClickListener {

    View view;//全局视图

    private ProgressDialog mPgd_data;
    private ListView mSysListView;
    private AppInfo_sys_adapter mSysAdapter;

    private List<AppInfo> mSysAppInfoList;

    private static final int MSG_DATA_SYS = 1;//数据加载message.what

    /*--------------------------------------------------------------------------------------------*/
    //todo thirdfragment回调函数
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //反射布局
        view = inflater.inflate(R.layout.fragment_app_sys, container, false);

        //初始化控件
        initView();

        //初始化事件
        initEvent();

        //初始化数据
        initData();

        //显示进度框
        //showPgd_data();

        return view;
    }

    /*--------------------------------------------------------------------------------------------*/
    //todo Handler
    /**
     * 句柄,负责UI线程
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_DATA_SYS) {
                mSysAdapter.setmAppInfoList(mSysAppInfoList);
                mSysAdapter.notifyDataSetChanged();
                initData();
//                mPgd_data.dismiss();
                AppActivity.mTxv_appsys.setText("系统应用(" + mSysAppInfoList.size() + ")");
            }
        }
    };

    /*--------------------------------------------------------------------------------------------*/
    //todo 封装的方法 initXxx() showXxx() setXxx() getXxx()


    private void initView() {
        mSysListView = (ListView) view.findViewById(R.id.lv_app_sys);
        mSysAdapter = new AppInfo_sys_adapter(getActivity());

        mSysListView.setAdapter(mSysAdapter);

        //从首选项中取出数据
        getSP();
    }

    /**
     * 加载数据,开启worker线程
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {


                mSysAppInfoList = Util.getSysAppInfoList(getActivity());
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                //放入分享首选项,做缓存
                setSP();

                handler.sendEmptyMessage(MSG_DATA_SYS);

            }
        }.start();
    }



    private void initEvent() {
        mSysListView.setOnItemLongClickListener(this);

    }

    /*private void showPgd_data() {
        mPgd_data = new ProgressDialog(getActivity());
        mPgd_data.setMessage("加载中,请稍候...");
        mPgd_data.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPgd_data.setCancelable(true);
        mPgd_data.show();
    }*/


    /**
     * 放入分享首选项
     */
    private void setSP() {
        AppInfoList applist = new AppInfoList();
        applist.setList(mSysAppInfoList);
        String strJson = JSON.toJSONString(applist);
        SPUtil.put(getActivity(), "sysAppList", strJson);
    }


    /**
     * 从缓存中取出分享首选项
     */
    private void getSP() {
        //取出分享首选项,一进入当前页面就显示结果(也算是初始化视图的一部分)
        if (SPUtil.contains(getActivity(), "sysAppList")) {
            String str = SPUtil.getString(getActivity(), "sysAppList", "");
            AppInfoList appList = JSON.parseObject(str, AppInfoList.class);
            mSysAppInfoList = appList.getList();
            mSysAdapter.setmAppInfoList(mSysAppInfoList);
            mSysAdapter.notifyDataSetChanged();
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    //todo 各种事件的回调函数
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfo appInfo = (AppInfo) parent.getItemAtPosition(position);
        Util.openSysDetail(getActivity(), appInfo.getPackageName());
        return true;
    }




}
