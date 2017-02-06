package com.example.administrator.appmanager_frg_vp_demo1.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.appmanager_frg_vp_demo1.application.SysApplication;
import com.example.administrator.appmanager_frg_vp_demo1.entity.AppInfo;
import com.example.administrator.appmanager_frg_vp_demo1.entity.FileInfo;

import java.io.File;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */
public class Util {
    public static List<AppInfo> getAppInfoList(Context context) {
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();
        //pm
        PackageManager pm = context.getPackageManager();
        //pList
        List<PackageInfo> pList = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : pList) {
            //app信息,到adapter里再拆出app图标(优化性能)
            AppInfo appInfo = new AppInfo();

            //判断是第三方应用,且不是自己(通过上下文.getPackageName())
            if (Util.isThirdPartyApp(packageInfo.applicationInfo)
                    && !packageInfo.packageName.contains(context.getPackageName())) {
                appInfo.setApplicationInfo(packageInfo.applicationInfo);
                //app名称
                appInfo.setAppName((String) packageInfo.applicationInfo.loadLabel(pm));
                //app版本
                appInfo.setVersionName("版本:" + packageInfo.versionName);
                //app大小
                String dir = packageInfo.applicationInfo.publicSourceDir;
                long byteSize = new File(dir).length();
                appInfo.setByteSize(byteSize);//大小
                appInfo.setSize("大小:" + getSize(byteSize) + "Mb");//格式化后的大小
                //app包名
                appInfo.setPackageName(packageInfo.packageName);
                //app更新时间
                appInfo.setUpdTime(packageInfo.lastUpdateTime);

                //把实体类对象添加到数据源集合中
                //装箱
                appInfoList.add(appInfo);
            }
        }
        return appInfoList;
    }


    public static List<AppInfo> getSysAppInfoList(Context context) {
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();

        //todo 解决context空指针的问题


        //pm
        PackageManager pm = context.getPackageManager();
        //pList
        List<PackageInfo> pList = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : pList) {
            //app信息,到adapter里再拆出app图标(优化性能)
            AppInfo appInfo = new AppInfo();

            //判断是系统应用
            if (!Util.isThirdPartyApp(packageInfo.applicationInfo)
                    ) {
                appInfo.setApplicationInfo(packageInfo.applicationInfo);
                //app名称
                appInfo.setAppName((String) packageInfo.applicationInfo.loadLabel(pm));
                //app版本
                appInfo.setVersionName("版本:" + packageInfo.versionName);
                //app大小
                String dir = packageInfo.applicationInfo.publicSourceDir;
                long byteSize = new File(dir).length();
                appInfo.setByteSize(byteSize);//大小
                appInfo.setSize("大小:" + getSize(byteSize) + "Mb");//格式化后的大小
                //app包名
                appInfo.setPackageName(packageInfo.packageName);
                //app更新时间
                appInfo.setUpdTime(packageInfo.lastUpdateTime);

                //把实体类对象添加到数据源集合中
                //装箱
                appInfoList.add(appInfo);
            }
        }
        return appInfoList;
    }


    /**
     * 格式化时间
     *
     * @param millis
     * @return
     */
    public static String getTime(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat smf = new SimpleDateFormat("MM-dd");
        return smf.format(date);
    }

    /**
     * 格式化大小
     *
     * @param size
     * @return
     */
    public static String getSize(long size) {
        return new DecimalFormat("0.##").format(size * 1.0 / (1024 * 1024));
    }

    /**
     * 判断是否是第三方应用
     *
     * @param appInfo
     * @return
     */
    public static boolean isThirdPartyApp(ApplicationInfo appInfo) {
        boolean flag = false;
        if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            // 可更新的系统应用
            flag = true;
        } else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            // 非系统应用
            flag = true;
        }
        return flag;
    }

    /**
     * 打开App
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean openPackage(Context context, String packageName) {
        try {
            Intent intent =
                    context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                //在新的task栈中打开
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 打开系统的应用详细界面
     *
     * @param context
     * @param packageName
     */
    public static void openSysDetail(Context context, String packageName) {
        Intent intent =
                new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");// action名, 特征值
        String pkg = "com.android.settings";
        String cls = "com.android.settings.applications.InstalledAppDetails";
        intent.setComponent(new ComponentName(pkg, cls));
        intent.setData(Uri.parse("package:" + packageName));//指明要打开的应用
        context.startActivity(intent);// 用普通的方法去打开界面
    }


    /**
     * 删除App
     *
     * @param activity
     * @param packageName
     * @param requestCode
     */
    public static void uninstallApk(Activity activity, String packageName, int requestCode) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 搜索应用信息结果
     * @param appInfoList
     * @param keyword
     * @return
     */
    public static List<AppInfo> getSearchResult(List<AppInfo> appInfoList, String keyword) {
        List<AppInfo> resultList = new ArrayList<AppInfo>();
        for (AppInfo appInfo : appInfoList) {
            if (appInfo.getAppName().toLowerCase().contains(keyword.toLowerCase())) {
                resultList.add(appInfo);
            }
        }
        return resultList;
    }


    /**
     * 搜索文件信息结果
     * @param fileInfoList
     * @param keyword
     * @return
     */
    public static List<FileInfo> getSearchResultForFile(List<FileInfo> fileInfoList, String keyword) {
        List<FileInfo> fileResultList = new ArrayList<FileInfo>();
        for (FileInfo fileInfo : fileInfoList) {
            //此处用fileinfo的name,而不是path,因为要找的是名字,用path当只有文件的时候会报数组下标越界的错(高亮)
            if (fileInfo.name.toLowerCase().contains(keyword.toLowerCase())) {
                fileResultList.add(fileInfo);
            }

//            if ( !fileInfo.path.equals(fileInfo.name) &&
//                    (fileInfo.path.toLowerCase().contains(keyword.toLowerCase()))) {
//                fileResultList.add(fileInfo);
//            }


        }
        return fileResultList;
    }

    /**
     * 搜索关键字高亮
     *
     * @param str
     * @param key
     * @return
     */
    public static SpannableStringBuilder highLightText(String str, String key) {
        int start = str.toLowerCase().indexOf(key.toLowerCase());
        int end = start + key.length();

        //todo 防止下标越界的解决方法?
//        if (start < 0){
//            start += 1;
//        }

        SpannableStringBuilder sb = new SpannableStringBuilder(str);
        sb.setSpan(
                new ForegroundColorSpan(Color.RED),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        );
        return sb;
    }


    /**
     * 搜索关键字高亮
     *
     * @param str
     * @param key
     * @return
     */
    public static SpannableStringBuilder highLightText1(String str, String key) {
        int start = str.toLowerCase().indexOf(key.toLowerCase());
        int end = start + key.length();

        //todo 防止下标越界的解决方法?
//        if (start < 0){
//            start += 1;
//        }

        SpannableStringBuilder sb = new SpannableStringBuilder(str);
        sb.setSpan(
                new ForegroundColorSpan(Color.RED),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        );
        return sb;
    }

    /**
     * 前后台输出
     *
     * @param context
     * @param msg
     */
    public static void show(Context context, String msg) {
        Log.i("wxs", msg);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 获取圆角位图的方法
     *
     * @param bitmap 需要转化成圆角的位图
     * @param pixels 圆角的度数，数值越大，圆角越大
     * @return 处理后的圆角位图
     */
    public static Bitmap toRoundCornerImage(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        // 抗锯齿
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 切割文件路径
     *
     * @param currPath
     * @return
     */
    public static String[] cutPathToArray(String currPath) {
        //去头,即去掉根路径
        //"/storage/emulated"
        //根路径字符串长度
        //Log.i("wxs","----------------------->rootlength="+FileUtil.getSDCardPath().length());
        String rootStr = FileUtil.getSDCardPath();
        int rootLength = FileUtil.getSDCardPath().length();
        //如果不是根路径
        if (!rootStr.equals(currPath)) {
            //从根路径长度开始切,一直切到当前路径总长度 +1 多且一个/
            String res = currPath.substring(rootLength + 1, currPath.length());
            String[] arr = res.split("/");
            Log.i("wxs", "res=" + res);
            return arr;
        }
        return null;
    }

    /**
     * 拿到点击的路径
     * @param pathsArr
     * @param id
     * @return
     */
    public static String getJoinPath(String[] pathsArr, int id) {
        String path = FileUtil.getSDCardPath();//根路径
        for (int i = 0; i < id; i++) {
            path += "/" + pathsArr[i];
        }
        return path;

    }

    /**
     * 退出
     * @param activity
     */
    public static void doExit(Activity activity) {
        SysApplication sysApplication = SysApplication.getInstance();
        sysApplication.exitAllActivity();

        activity.finish();
        System.exit(0);//防止退出是报空指针异常
    }
}