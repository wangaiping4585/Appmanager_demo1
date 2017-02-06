package com.example.administrator.appmanager_frg_vp_demo1.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.appmanager_frg_vp_demo1.R;
import com.example.administrator.appmanager_frg_vp_demo1.application.SysApplication;
import com.example.administrator.appmanager_frg_vp_demo1.fragment.AppSysFragment;
import com.example.administrator.appmanager_frg_vp_demo1.fragment.AppThirdFragment;
import com.example.administrator.appmanager_frg_vp_demo1.util.Util;

import java.util.ArrayList;
import java.util.List;


public class AppActivity extends FragmentActivity implements View.OnClickListener {
    //按两次退出
    private long exitTime = 0;

    //底部Activity的选项卡
    private LinearLayout mLl_file;
    private LinearLayout mLl_option;
    private TextView mTxv_app;

    //退出箭头
    private LinearLayout mLl_exit;//退出箭头

    //viewpager
    private ViewPager mViewpager;
    private FragmentPagerAdapter mFrgPgAdapter;
    private List<Fragment> mFrgList;
    private LayoutInflater mInflater;


    //选项卡
    private RelativeLayout mTabAppthird;
    private RelativeLayout mTabAppSys;
    //private LinearLayout mTabDetail;

    //选项卡文字
    public static TextView mTxv_appthird;
    public static TextView mTxv_appsys;
    //public static TextView mTxv_detail;

    //选项卡指示横线
    private FrameLayout mFl_thirdLine;
    private FrameLayout mFl_sysLine;

    private RelativeLayout mLl_corner;//右上角下拉布局
    public static PopupWindow mPopup_option;//设置popup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加到activity集合中
        SysApplication.getInstance().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_appinfo);

        //初始化控件
        initView();

        //初始化(点击)事件
        initEvent();

        //初始化数据
        initData();

        //初始化状态
        setSelect(0);
    }


    //---------------------------------------------------------------------------------------------

    private void initData() {
    }

    private void initView() {
        //底部Activity的选项卡
        mLl_file = (LinearLayout) findViewById(R.id.ll_file);
        mLl_option = (LinearLayout) findViewById(R.id.ll_option);
        mTxv_app = (TextView) findViewById(R.id.txv_app);

        mLl_file.setBackgroundColor(Color.WHITE);
        mLl_option.setBackgroundColor(Color.WHITE);
        mTxv_app.setTextColor(Color.WHITE);


        //右上角下拉pop
        mLl_corner = (RelativeLayout) findViewById(R.id.ll_corner);

        //退出箭头
        mLl_exit = (LinearLayout) findViewById(R.id.ll_exit);

        //viewpager
        mViewpager = (ViewPager) this.findViewById(R.id.vp_main);

        //大按钮(线性布局)
        mTabAppthird = (RelativeLayout) findViewById(R.id.rl_tab_appthird);
        mTabAppSys = (RelativeLayout) findViewById(R.id.rl_tab_appsys);
        //mTabDetail = (LinearLayout) findViewById(R.id.ll_tab_detail);

        //Tab文字
        mTxv_appthird = (TextView) findViewById(R.id.txv_tab_appthird);
        mTxv_appsys = (TextView) findViewById(R.id.txv_tab_appsys);
        //mTxv_detail = (TextView) findViewById(R.id.txv_tab_detail);

        //选项卡指示线
        mFl_thirdLine = (FrameLayout) findViewById(R.id.fl_tabline_third);
        mFl_sysLine = (FrameLayout) findViewById(R.id.fl_tabline_sys);

        //初始化frg并准备好Fragment数据源
        mFrgList = new ArrayList<Fragment>();
        Fragment mTabAppthird = new AppThirdFragment();
        Fragment mTabAppSys = new AppSysFragment();
        //Fragment mTabDetail = new DetailFragment();

        mFrgList.add(mTabAppthird);
        mFrgList.add(mTabAppSys);


        //配置适配器,适配器关联数据
        mFrgPgAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                //通过位置返回不同frg 并实现滑动
                return mFrgList.get(i);
            }

            @Override
            public int getCount() {
                return (mFrgList == null) ? 0 : mFrgList.size();
            }
        };

        //适配器关联控件
        mViewpager.setAdapter(mFrgPgAdapter);

        //监听vp变化,实现联动效果
        mViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                int currentItem = mViewpager.getCurrentItem();//当前item号
                setTab(currentItem);//联动
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }


    private void initEvent() {
        mTabAppthird.setOnClickListener(this);
        mTabAppSys.setOnClickListener(this);
        mLl_exit.setOnClickListener(this);
        mLl_corner.setOnClickListener(this);
        //底部Activity跳转
        mLl_file.setOnClickListener(this);
        mLl_option.setOnClickListener(this);

    }


    /**
     * 设置选项卡变换样式:1.字体变色,2.切换指示线
     *
     * @param tab
     */
    public void setTab(int tab) {
        //重置文字颜色
        resetTxvColor();
        //重置选项卡横线
        resetTabLine();

        switch (tab) {
            case 0:  //点击了第一个tab
                mTxv_appthird.setTextColor(Color.rgb(108, 178, 95));
                mFl_thirdLine.setVisibility(View.VISIBLE);
                break;
            case 1:
                //Color.rgb(108, 178, 95)
                mTxv_appsys.setTextColor(Color.rgb(108, 178, 95));
                mFl_sysLine.setVisibility(View.VISIBLE);
                break;
            /*case 2:
                mTxv_detail.setTextColor(Color.rgb(143,188,143));
                break;*/

            default:
                break;
        }
    }

    //重置选项卡横线
    private void resetTabLine() {
        mFl_thirdLine.setVisibility(View.GONE);
        mFl_sysLine.setVisibility(View.GONE);
    }

    //重置字体颜色
    private void resetTxvColor() {
        mTxv_appthird.setTextColor(Color.GRAY);
        mTxv_appsys.setTextColor(Color.GRAY);
        //mTxv_detail.setTextColor(Color.GRAY);
    }


    //弹出设置popupwindow
    private View showPopup_option(View v) {
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.popupwindow_app_option, null);

        mPopup_option = new PopupWindow(myView, 400, 400);//设置宽高

        mPopup_option.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup_option.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopup_option.setBackgroundDrawable(new BitmapDrawable());//一定要设置背景

        mPopup_option.setOutsideTouchable(true);

        mPopup_option.setFocusable(true);

        mPopup_option.showAsDropDown(v);
        return myView;
    }




    //---------------------------------------------------------------------
    //todo 控件事件回调
    @Override
    public void onClick(View v) {
        //点亮按钮,切换指示线
        //切换选项卡
        switch (v.getId()) {
            case R.id.rl_tab_appthird:
                setSelect(0);
                break;
            case R.id.rl_tab_appsys:
                setSelect(1);
                break;
            case R.id.ll_exit:
                Util.doExit(this);
                break;
            case R.id.ll_corner://右上角下拉菜单
                View myViewToPopup = showPopup_option(v);
                myViewToPopup.findViewById(R.id.ll_backfile).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(AppActivity.this, FileActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans);
                        mPopup_option.dismiss();
                        //finish();
                    }
                });

                myViewToPopup.findViewById(R.id.ll_backoption).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(AppActivity.this, OptionActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans);
                        mPopup_option.dismiss();
                        //finish();
                    }
                });


                break;

            case R.id.ll_file:
                Intent intentFile = new Intent();
                intentFile.setClass(this, FileActivity.class);
                startActivity(intentFile);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans);
                break;

            case R.id.ll_option:
                Intent intentMe = new Intent();
                intentMe.setClass(this, OptionActivity.class);
                startActivity(intentMe);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans);
                break;

            default:
                break;
        }
    }

    public void setSelect(int select) {
        setTab(select);//相应文字变色,切换指示线
        mViewpager.setCurrentItem(select);//切换到相应fragment
    }

    //----------------------------------------------------------------------------------------------
    //todo 各种Mainactivity回调


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
