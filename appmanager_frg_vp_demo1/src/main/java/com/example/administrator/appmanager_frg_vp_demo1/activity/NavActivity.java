package com.example.administrator.appmanager_frg_vp_demo1.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.appmanager_frg_vp_demo1.R;
import com.example.administrator.appmanager_frg_vp_demo1.adapter.HorizontalScrollViewAdapter;
import com.example.administrator.appmanager_frg_vp_demo1.application.SysApplication;
import com.example.administrator.appmanager_frg_vp_demo1.mView.MyHorizontalScrollView;
import com.example.administrator.appmanager_frg_vp_demo1.service.FloatService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavActivity extends Activity implements View.OnClickListener {


    //横向滚动条
    private MyHorizontalScrollView mHorizontalScrollView;
    private HorizontalScrollViewAdapter mHSVadapter;
    private ImageView mImg;

    private static int mBackKeyPressedTimes = 0;//按两次退出

    private Button mBtn_app;
    private Button mBtn_file;

    private LinearLayout mLl_toapp;
    private LinearLayout mLl_tofile;
    private LinearLayout mLl_tome;
    private RelativeLayout mLl_corner;//右上角下拉布局

    public static PopupWindow mPopup_option;//设置popup
    public static PopupWindow mPopup_float;//悬浮框设置popup
    private LinearLayout mLl_exit;//退出箭头
    private ImageView mImgv_arrowcorner;//右上角箭头

    private List<Integer> mDatas = new ArrayList<Integer>(Arrays.asList(
            R.drawable.xs1, R.drawable.xs4, R.drawable.xs10, R.drawable.xs6,
            R.drawable.xs12, R.drawable.xs13, R.drawable.xs15, R.drawable.xs16,
            R.drawable.xs19, R.drawable.xs20, R.drawable.xs21, R.drawable.xs22));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nav);
        //添加到activity集合中
        SysApplication.getInstance().addActivity(this);


        initView();

        initEvent();


    }


    private void initView() {
        mBtn_app = (Button) findViewById(R.id.btn_nav_app);
        mBtn_file = (Button) findViewById(R.id.btn_nav_file);

        mLl_toapp = (LinearLayout) findViewById(R.id.ll_toapp);
        mLl_tofile = (LinearLayout) findViewById(R.id.ll_tofile);
        mLl_tome = (LinearLayout) findViewById(R.id.ll_tome);
        mLl_corner = (RelativeLayout) findViewById(R.id.ll_corner);

        mLl_exit = (LinearLayout) findViewById(R.id.ll_exit);//退出箭头
        mImgv_arrowcorner = (ImageView) findViewById(R.id.imgv_arrowcorner);

        mImg = (ImageView) findViewById(R.id.imgv_banner_content);

        mHorizontalScrollView = (MyHorizontalScrollView) findViewById(R.id.id_horizontalScrollView);

        mHSVadapter = new HorizontalScrollViewAdapter(this, mDatas);
    }

    private void initEvent() {
        //退出按钮
        mLl_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysApplication sysApplication = SysApplication.getInstance();
                sysApplication.exitAllActivity();

                System.exit(0);//防止退出是报空指针异常
            }
        });

        //添加滚动回调
        mHorizontalScrollView
                .setCurrentImageChangeListener(new MyHorizontalScrollView.CurrentImageChangeListener() {
                    @Override
                    public void onCurrentImgChanged(int position,
                                                    View viewIndicator) {
                        mImg.setImageResource(mDatas.get(position));
                        viewIndicator.setBackgroundColor(Color
                                .parseColor("#AA024DA4"));
                    }
                });
        //添加点击回调
        mHorizontalScrollView.setOnItemClickListener(new MyHorizontalScrollView.OnItemClickListener() {

            @Override
            public void onClick(View view, int position) {
                mImg.setImageResource(mDatas.get(position));
                view.setBackgroundColor(Color.parseColor("#AA024DA4"));
            }
        });
        //设置适配器
        mHorizontalScrollView.initDatas(mHSVadapter);

        //右上角弹出popup
        mLl_corner.setOnClickListener(this);
    }

    public void toApp(View view) {
        Intent intent = new Intent();
        intent.setClass(this, AppActivity.class);
        startActivity(intent);
    }

    public void toFile(View view) {
        Intent intent = new Intent();
        intent.setClass(this, FileActivity.class);
        startActivity(intent);
    }

    public void toMe(View view) {
        Intent intent = new Intent();
        intent.setClass(this, MeActivity.class);
        startActivity(intent);
    }


    /**
     * 按两次退出
     */
    @Override
    public void onBackPressed() {
        if (mBackKeyPressedTimes == 0) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mBackKeyPressedTimes = 1;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        mBackKeyPressedTimes = 0;
                    }
                }
            }.start();
            return;
        } else {

            SysApplication sysApplication = SysApplication.getInstance();
            sysApplication.exitAllActivity();

            this.finish();
            System.exit(0);//防止退出是报空指针异常
        }
        super.onBackPressed();
    }


    //弹出popup
    @Override
    public void onClick(final View v) {
        //点击了
        if (v.getId() == R.id.ll_corner) {
            /**
             * 弹出排序选项popup窗
             */
            View myViewToPopup = showPopup_option(v);

            myViewToPopup.findViewById(R.id.ll_option).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Util.show(getApplicationContext(),"www");
                    /*Intent intent = new Intent();
                    intent.setClass(NavActivity.this, OptionActivity.class);
                    startActivity(intent);*/
                    mPopup_option.dismiss();
                    final View myViewToPopupFloat = showPopup_float();

                    myViewToPopupFloat.findViewById(R.id.ll_open_float).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(NavActivity.this, FloatService.class);
                            startService(intent);
                            mPopup_float.dismiss();
                        }
                    });

                    myViewToPopupFloat.findViewById(R.id.ll_close_float).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(NavActivity.this, FloatService.class);
                            stopService(intent);
                            mPopup_float.dismiss();
                        }
                    });
                }
            });


        }
    }


    //弹出popupwindow
    private View showPopup_option(View v) {
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.popupwindow_nav_option, null);

        mPopup_option = new PopupWindow(myView, 400, 140);//设置宽高
        mPopup_option.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup_option.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopup_option.setBackgroundDrawable(new BitmapDrawable());//一定要设置背景

        mPopup_option.setOutsideTouchable(true);

        mPopup_option.setFocusable(true);

        mPopup_option.showAsDropDown(v);
        return myView;
    }

    //弹出悬浮框设置popupwindow
    private View showPopup_float() {
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.popupwindow_float_set, null);

        mPopup_float = new PopupWindow(myView, 400, 260);//设置宽高
        mPopup_float.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup_float.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopup_float.setBackgroundDrawable(new BitmapDrawable());//一定要设置背景

        mPopup_float.setOutsideTouchable(true);

        mPopup_float.setFocusable(true);

        mPopup_float.showAtLocation(findViewById(R.id.imgv_banner_content), Gravity.CENTER,0,10);
        return myView;
    }

}
