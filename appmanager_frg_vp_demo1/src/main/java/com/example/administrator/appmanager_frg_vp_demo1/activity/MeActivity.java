package com.example.administrator.appmanager_frg_vp_demo1.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.example.administrator.appmanager_frg_vp_demo1.R;
import com.example.administrator.appmanager_frg_vp_demo1.application.SysApplication;
import com.example.administrator.appmanager_frg_vp_demo1.mView.BinarySlidingMenu;
import com.example.administrator.appmanager_frg_vp_demo1.util.Util;

public class MeActivity extends Activity implements View.OnClickListener {

    private LayoutInflater mInflater;

    private RelativeLayout mRl_backApp;
    private RelativeLayout mRl_backFile;
    private RelativeLayout mRl_backNav;
    private RelativeLayout mRl_morefunc;

    private RelativeLayout rl_me_center;//中间布局

    private BinarySlidingMenu mMenu;

    View left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加到activity集合中
        SysApplication.getInstance().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_me);

        mInflater = LayoutInflater.from(getApplicationContext());
        left = mInflater.inflate(R.layout.layout_menu_left, null);

        initView();

        initEvent();

        mMenu = (BinarySlidingMenu) findViewById(R.id.id_menu);
        mMenu.setOnMenuOpenListener(new BinarySlidingMenu.OnMenuOpenListener() {
            @Override
            public void onMenuOpen(boolean isOpen, int flag) {
                if (isOpen) {
                } else {
                }

            }
        });
    }

//-------------------------------------------------------------------------------
    //todo initXxx()

    private void initView() {
        mRl_backApp = (RelativeLayout) findViewById(R.id.rl_backapp_slide);
        mRl_backFile = (RelativeLayout) findViewById(R.id.rl_backfile_slide);
        //mRl_backNav = (RelativeLayout) findViewById(R.id.rl_backnav_slide);
        mRl_morefunc = (RelativeLayout) findViewById(R.id.rl_morefunc);
        rl_me_center = (RelativeLayout) findViewById(R.id.rl_me_center);
    }

    private void initEvent() {
        mRl_backApp.setOnClickListener(this);
        mRl_backFile.setOnClickListener(this);
//        mRl_backNav.setOnClickListener(this);
        mRl_morefunc.setOnClickListener(this);
        rl_me_center.setOnClickListener(null);
    }


    //-------------------------------------------------------------------------
    //todo 事件
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rl_backapp_slide:
                Intent intent = new Intent();
                intent.setClass(this, AppActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.rl_backfile_slide:
                Intent intent1 = new Intent();
                intent1.setClass(this, FileActivity.class);
                startActivity(intent1);
                finish();
                break;

            /*case R.id.rl_backnav_slide:
                Intent intent2 = new Intent();
                intent2.setClass(this, NavActivity.class);
                startActivity(intent2);
                finish();
                break;*/

            case R.id.rl_morefunc:
                Util.show(this,"更多功能敬请期待!");
                break;

            default:
                break;
        }
    }

//------------------------------------------------------------------------------
    //todo 各种Mainactivity回调

    /*@Override
    public void onBackPressed() {
        //跳转到导航页面
        this.finish();
        Intent intent = new Intent();
        intent.setClass(this, AppActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in,R.anim.trans);
    }
*/

}
