package com.example.administrator.appmanager_frg_vp_demo1.application;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by 钧 on 2016/3/15.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)// 线程的优先级别
                .denyCacheImageMultipleSizesInMemory() // 拒绝不同的缓存大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())// 对临时文件名加密
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb SDCard上的缓存空间
                .tasksProcessingOrder(QueueProcessingType.LIFO)// 任务队列采取LIFO
                .writeDebugLogs() //调试日志-可在项目发布时删除
                .build();// 构建配置

        ImageLoader.getInstance().init(config);

    }
}
