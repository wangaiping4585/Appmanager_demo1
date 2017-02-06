package com.example.administrator.appmanager_frg_vp_demo1.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.appmanager_frg_vp_demo1.R;
import com.example.administrator.appmanager_frg_vp_demo1.application.SysApplication;
import com.example.administrator.appmanager_frg_vp_demo1.util.Util;

public class OptionActivity extends Activity implements View.OnClickListener{

    //按两次退出
    private long exitTime = 0;

    //退出箭头
    private LinearLayout mLl_exit;//退出箭头

    //底部Activity的选项卡
    private LinearLayout mLl_app;
    private LinearLayout mLl_file;
    private TextView mTxv_option;

    //导航
    private RelativeLayout mRl_backApp;
    private RelativeLayout mRl_backFile;
    private RelativeLayout mRl_morefunc;

    private RelativeLayout mLl_corner;//右上角下拉布局

    public static PopupWindow mPopup_option;//设置popup


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加到activity集合中
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_option);

        initView();

        initEvent();


    }



    private void initView() {
        mLl_app = (LinearLayout) findViewById(R.id.ll_app);
        mLl_file = (LinearLayout) findViewById(R.id.ll_file);
        mTxv_option = (TextView) findViewById(R.id.txv_option);

        mLl_app.setBackgroundColor(Color.WHITE);
        mLl_file.setBackgroundColor(Color.WHITE);
        mTxv_option.setTextColor(Color.WHITE);

        //导航
        mRl_backApp = (RelativeLayout) findViewById(R.id.rl_backapp_slide);
        mRl_backFile = (RelativeLayout) findViewById(R.id.rl_backfile_slide);
        mRl_morefunc = (RelativeLayout) findViewById(R.id.rl_morefunc);

        mLl_corner = (RelativeLayout) findViewById(R.id.ll_corner);//右上角下拉布局

        mLl_exit = (LinearLayout) findViewById(R.id.ll_exit);
    }

    private void initEvent() {
        mLl_app.setOnClickListener(this);
        mLl_file.setOnClickListener(this);
        mLl_corner.setOnClickListener(this);
        mLl_exit.setOnClickListener(this);
        mRl_backApp.setOnClickListener(this);
        mRl_backFile.setOnClickListener(this);
        mRl_morefunc.setOnClickListener(this);
    }


    //弹出设置popupwindow
    private View showPopup_option(View v) {
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.popupwindow_option_option, null);

        mPopup_option = new PopupWindow(myView, 400, 400);//设置宽高
        mPopup_option.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup_option.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopup_option.setBackgroundDrawable(new BitmapDrawable());//一定要设置背景

        mPopup_option.setOutsideTouchable(true);

        mPopup_option.setFocusable(true);

        mPopup_option.showAsDropDown(v);
        return myView;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        //退出箭头
        if (v.getId() == R.id.ll_exit){
            Util.doExit(this);
        }

        //底部Activity跳转
        if (v.getId() == R.id.ll_app) {
            Intent intentApp = new Intent();
            intentApp.setClass(this, AppActivity.class);
            startActivity(intentApp);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
        }

        if (v.getId() == R.id.ll_file) {
            Intent intentMe = new Intent();
            intentMe.setClass(this, FileActivity.class);
            startActivity(intentMe);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
        }

        //导航
        if (v.getId() == R.id.rl_backapp_slide){
            Intent intent = new Intent();
            intent.setClass(this, AppActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
        }

        if (v.getId() == R.id.rl_backfile_slide){
            Intent intent1 = new Intent();
            intent1.setClass(this, FileActivity.class);
            startActivity(intent1);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
        }

        if (v.getId() == R.id.rl_morefunc){
            Util.show(this,"更多功能敬请期待!");
        }


        //右上角下拉菜单
        if (v.getId() == R.id.ll_corner) {
            View myViewToPopup = showPopup_option(v);
            myViewToPopup.findViewById(R.id.ll_backapp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(OptionActivity.this, AppActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
                    mPopup_option.dismiss();
                    finish();
                }
            });

            myViewToPopup.findViewById(R.id.ll_backfile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(OptionActivity.this, FileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
                    mPopup_option.dismiss();
                    finish();
                }
            });
        }




    }


    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            Util.doExit(this);
        }
    }
}
