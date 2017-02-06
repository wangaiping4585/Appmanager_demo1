package com.example.administrator.appmanager_frg_vp_demo1.entity;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/11/24.
 */
public class AppInfo {
    /**包信息集合*/
    private ApplicationInfo applicationInfo;
    /** 包名 */
    private String packageName;
    /** 版本名 */
    private String versionName;
    /** 版本号 */
    private int versionCode;
    /** 第一次安装时间 */
    private long insTime;
    /** 更新时间 */
    private long updTime;
    /** 程序名 */
    private String appName;
    /** 图标 */
    private Drawable icon;
    /** 字节大小 */
    private long byteSize;
    /** 大小 */
    private String size;

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public long getInsTime() {
        return insTime;
    }

    public void setInsTime(long insTime) {
        this.insTime = insTime;
    }

    public long getUpdTime() {
        return updTime;
    }

    public void setUpdTime(long updTime) {
        this.updTime = updTime;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getByteSize() {
        return byteSize;
    }

    public void setByteSize(long byteSize) {
        this.byteSize = byteSize;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "\nAppInfo{" +
                "applicationInfo=" + applicationInfo +
                ", packageName='" + packageName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", insTime=" + insTime +
                ", updTime=" + updTime +
                ", appName='" + appName + '\'' +
                ", icon=" + icon +
                ", byteSize=" + byteSize +
                ", size='" + size + '\'' +
                '}';
    }
}
