package com.example.administrator.appmanager_frg_vp_demo1.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.administrator.appmanager_frg_vp_demo1.R;
import com.example.administrator.appmanager_frg_vp_demo1.activity.NavActivity;
import com.example.administrator.appmanager_frg_vp_demo1.mView.CircleView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FloatService extends Service{
	
	private static final int HANDLE_CHECK_ACTIVITY = 1;
	protected static final String TAG = "FloatService";
	private boolean isAdded = false; // 是否已增加悬浮窗
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;
	
	private List<String> homeList; // 桌面应用程序包名列表
	private ActivityManager mActivityManager;
	private View mainView;
	private LayoutInflater inflater;
	TextView precent ;
	View menu;
	TextView menu0;
	TextView menu1;
	TextView menu2;
	private CircleView circleView;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		homeList = getHomes();
		createFloatView();
		Log.i(TAG , "onCreate");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
		Log.i(TAG , "onStart");
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_CHECK_ACTIVITY:
				if (isHome()){
					if (!isAdded) {
						Log.i(TAG , "wm.addView(mainView, params);");
						wm.addView(mainView, params);
						isAdded = true;
					}
                    // 只有主界面才更新
					precent.setText(getPrecent() + "%");
					circleView.setPrecent(getPrecent());
				}else{
					if (isAdded){
						wm.removeView(mainView);
						isAdded = false;
					}
				}
				
				mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000);
				break;
			}
		}
	};
	
	
	/**
	 * ������
	 */
	private void createFloatView() {
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();

        // 设置window type
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		/*
		 * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
		 * 即拉下通知栏不可见
		 */
		params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        // 设置Window flag
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		/*
		 * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
		 * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		 */

        // 设置悬浮窗的长得宽
		params.width = LayoutParams.WRAP_CONTENT;
		params.height = LayoutParams.WRAP_CONTENT;
		
		if (null == inflater){
			inflater = LayoutInflater.from(getApplicationContext());
			mainView = inflater.inflate(R.layout.window_view_float, null);
			precent = (TextView) mainView.findViewById(R.id.precent);
			circleView = (CircleView) mainView.findViewById(R.id.window_circle);
			menu = mainView.findViewById(R.id.menu_layout);
			menu0 =  (TextView) mainView.findViewById(R.id.menu0);
			menu1 =  (TextView) mainView.findViewById(R.id.menu1);
			menu2 =  (TextView) mainView.findViewById(R.id.menu2);
		}

        // 设置悬浮窗的Touch监听
		mainView.setOnTouchListener(new OnTouchListener() {
			int lastX, lastY;
			int paramX, paramY;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					paramX = params.x;
					paramY = params.y;
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int) event.getRawX() - lastX;
					int dy = (int) event.getRawY() - lastY;
					params.x = paramX + dx;
					params.y = paramY + dy;
                    // 更新悬浮窗位置
					wm.updateViewLayout(mainView, params);
					break;
				}

                // return true的 click事件是没有反应的，至于为什么 大家去了解触摸事件分发与处理 就知道了
				return false;
			}
		});
		
		mainView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (menu.getVisibility() == View.VISIBLE){
					menu.setVisibility(View.GONE);  // 必须是Gone 不能是 INVISIBLE
				}else{
					menu.setVisibility(View.VISIBLE);
				}
			}
		});
		
		menu0.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(FloatService.this , NavActivity.class);
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(it);
			}
		});

		menu1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				menu.setVisibility(View.GONE);
			}
		});

		menu2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isAdded  = false;
				wm.removeView(mainView);
                // 记得要移动掉handler  否则还是一秒运行一次
				mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
				stopSelf();
			}
		});

	}
	
    /**
     * �õ�ϵͳ���ڴ� ��λKB
     * @param context
     * @return
     */
    public  long getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";// 获取android当前可用内存大小
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();//读取meminfo第一行，系统总内存大小


            arrayOfString = str2.split("\\s+");
         /*   for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }*/

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte

            localBufferedReader.close();

        } catch (IOException e) {
        }
        return initial_memory;
    }
    
    public  long getAvailMemory(Context context) {// 获取android当前可用内存大小

        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }
    
    private int getPrecent(){
    	long totalSize = getTotalMemory(getApplicationContext());
    	long aliSize = getAvailMemory(getApplicationContext());
    	
    	int precent = 100 - (int) (aliSize * 100  / (float)totalSize);
    	
    	return precent;
    }

    /** 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
	private List<String> getHomes() {
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = this.getPackageManager();
        // 属性
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}

    /**
     * 判断当前界面是否是桌面
     */
	public boolean isHome() {
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		}
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		return homeList.contains(rti.get(0).topActivity.getPackageName());
	}
    


}
