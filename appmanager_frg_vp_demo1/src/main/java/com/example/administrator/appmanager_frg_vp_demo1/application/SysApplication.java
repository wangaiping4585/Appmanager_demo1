package com.example.administrator.appmanager_frg_vp_demo1.application;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * 关闭所有Activity
 * Created by Administrator on 2016/12/1.
 */
public class SysApplication extends Application{
    //运用list来保存每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();

    //为了实现每次使用该类时不创建新的对象而创建的静态对象
    private static SysApplication instance;

    //私有构造方法
    private SysApplication() {
    }

    //实例化一次
    public synchronized static SysApplication getInstance(){
        if (null == instance){
            instance = new SysApplication();
        }
        return instance;
    }

    //add act
    public void addActivity(Activity activity){
        mList.add(activity);
    }

    //关闭activity集合中的每一个act
    public void exitAllActivity(){
        try {
            for (Activity activity : mList) {
                if (activity!= null){
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    //杀进程
    public void onLowMemory(){
        super.onLowMemory();
        System.gc();
    }

}
