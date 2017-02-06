package com.example.administrator.appmanager_frg_vp_demo1.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.administrator.appmanager_frg_vp_demo1.activity.AppActivity;
import com.example.administrator.appmanager_frg_vp_demo1.R;
import com.example.administrator.appmanager_frg_vp_demo1.adapter.AppInfo_third_adapter;
import com.example.administrator.appmanager_frg_vp_demo1.entity.AppInfo;
import com.example.administrator.appmanager_frg_vp_demo1.entity.AppInfoList;
import com.example.administrator.appmanager_frg_vp_demo1.mInter.Iuninstall;
import com.example.administrator.appmanager_frg_vp_demo1.mView.MyListView;
import com.example.administrator.appmanager_frg_vp_demo1.util.SPUtil;
import com.example.administrator.appmanager_frg_vp_demo1.util.Util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppThirdFragment extends Fragment implements Iuninstall,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        View.OnClickListener,
        MyListView.OnRefreshListener {

    /*--------------------------------------------------------------------------------------------*/
    //todo 属性及变量
    View view;//全局变量
    private LayoutInflater mInflater;//反射器

    private static final int MSG_DATA = 1;//数据加载message.what
    private static final int REQ_UNINSTALL = 0;//删除数据的请求码
    public static String KEYWORD = "";//搜索的关键字

    //private ListView mListView;
    private AppInfo_third_adapter mThirdAdapter;
    private List<AppInfo> mThirdAppInfoList;

    private MyListView customListView;//自定义的ListView

    private ProgressDialog mPgd_data;//进度框

    private LinearLayout mLl_sort_type_internal;//排序方式线性布局
    private TextView mTxv_sort_app;//排序方式文字
    public static PopupWindow mPopup_sort;//排序选择popup
    private TextView mTxv_sort;//排序信息
    private EditText mEdt_search;//搜索框
    private TextView mTxv_search_result;//搜索结果文字
    private ImageView mImgv_arrow;//搜索箭头

    //用常量区分排序方式
    public static final int SORT_TYPE_NAME = 0;
    public static final int SORT_TYPE_DATE = 1;
    public static final int SORT_TYPE_SIZE = 2;
    public static final String[] SORT_TYPE_ARR = {"按名称", "按日期", "按大小"};

    //当前排序
    private int currSortType = SORT_TYPE_SIZE;//当前排序方式,默认是按大小
    Comparator<AppInfo> currComparator;//当前比较器

    //比较器
    /**
     * 日期比较器 倒序
     */
    Comparator<AppInfo> dateComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            if (lhs.getUpdTime() > rhs.getUpdTime()) {
                return -1;
            } else if (lhs.getUpdTime() == rhs.getUpdTime()) {
                return 0;
            } else {
                return 1;
            }
        }
    };

    /**
     * 大小比较器 倒序
     */
    Comparator<AppInfo> sizeComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            if (lhs.getByteSize() > rhs.getByteSize()) {
                return -1;
            } else if (lhs.getByteSize() == rhs.getByteSize()) {
                return 0;
            } else {
                return 1;
            }
        }
    };

    /**
     * 名称比较器 正序
     */
    Comparator<AppInfo> nameComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            return lhs.getAppName().toLowerCase().compareTo(rhs.getAppName().toLowerCase());
        }
    };


    /*--------------------------------------------------------------------------------------------*/
    //todo Fragment回调函数
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //反射布局
        view = inflater.inflate(R.layout.fragment_app_third, container, false);

        //初始化控件
        initView();

        //初始化事件
        initEvent();

        //初始化数据
        initData();

        //加载进度框
        //showPgd_data();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();//删除完成,再次加载数据
    }


    //----------------------------------------------------------------------------------------------
    //todo Handler TextWatcher
    /**
     * 句柄,负责UI线程
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_DATA) {
                //mThirdAdapter.setmAppInfoList(mThirdAppInfoList);
                //mThirdAdapter.notifyDataSetChanged();
                //调用默认排序方式加载数据
                updateData_sort(currSortType);
                //mPgd_data.dismiss();
                AppActivity.mTxv_appthird.setText("已安装(" + Util.getAppInfoList(getActivity()).size() + ")");//实时为Tab文字显示当前应用数量
            }
        }
    };

    /**
     * 监听搜索框输入变化
     */
    TextWatcher mTextWatcher = new TextWatcher() {
        private String beforeText = KEYWORD;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            beforeText = s.toString();//接收输入之前的字符串
            //Util.show(getActivity(), "beforeTextChanged = (" + s + ")");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!beforeText.equals(s.toString())) {//防止产生两次afterTextChanged的字符串
                KEYWORD = s.toString();
                //Util.show(getActivity(), "afterTextChanged = (" + KEYWORD + ")");
                //搜索
                //每次都要从全部的结果集里搜索
                mThirdAppInfoList = Util.getSearchResult(Util.getAppInfoList(getActivity()), KEYWORD);//随写随搜

                //todo 显示搜索结果的提示文字!!
                if (mThirdAppInfoList.size() == 0) {
                    mTxv_search_result.setVisibility(View.VISIBLE);
                } else {
                    mTxv_search_result.setVisibility(View.GONE);
                }

                //多次搜索缩小范围
                updateData_sort(currSortType);
            }
        }
    };

    /*--------------------------------------------------------------------------------------------*/
    //todo 封装的方法  1.initXxx() 2.showXxx() 3.set() get()

    private void initView() {
        //mListView = (ListView) view.findViewById(R.id.lv_app_third);
        customListView = (MyListView) view.findViewById(R.id.lv_my_forThird);
        mThirdAdapter = new AppInfo_third_adapter(getActivity());

        //mListView.setAdapter(mThirdAdapter);
        customListView.setAdapter(mThirdAdapter);
        mThirdAdapter.setmIuninstall(this);

        mTxv_sort_app = (TextView) view.findViewById(R.id.txv_sort_app);//排序文字
        mLl_sort_type_internal = (LinearLayout) view.findViewById(R.id.ll_sort_type_internal);//排序线性布局

        //搜索框
        mEdt_search = (EditText) view.findViewById(R.id.edt_search_input);
        //搜索箭头
        mImgv_arrow = (ImageView) view.findViewById(R.id.imgv_arrow);

        //搜索结果
        mTxv_search_result = (TextView) view.findViewById(R.id.txv_search_result);

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

                //加上搜索功能后:必须用如下的方式,在全部的结果集里,找到搜索结果集!
                mThirdAppInfoList = Util.getSearchResult(Util.getAppInfoList(getActivity()), KEYWORD);

//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                //设置分享首选项
                setSP();

                //没有搜索功能时.
                //mThirdAppInfoList = Util.getAppInfoList(getActivity());

                handler.sendEmptyMessage(MSG_DATA);
            }
        }.start();
    }

    /**
     * 有排序功能的加载数据方法
     *
     * @param currSortType
     */
    private void updateData_sort(int currSortType) {
        if (currSortType == SORT_TYPE_NAME) {
            currComparator = nameComparator;
        }

        if (currSortType == SORT_TYPE_DATE) {
            currComparator = dateComparator;
        }

        if (currSortType == SORT_TYPE_SIZE) {
            currComparator = sizeComparator;
        }

        Collections.sort(mThirdAppInfoList, currComparator);//排序

        //适配器关联数据
        mThirdAdapter.setmAppInfoList(mThirdAppInfoList);
        //动态更新
        mThirdAdapter.notifyDataSetChanged();
    }

    private void initEvent() {
        //mListView.setOnItemClickListener(this);
        //mListView.setOnItemLongClickListener(this);
        customListView.setOnItemClickListener(this);
        customListView.setOnItemLongClickListener(this);
        customListView.setonRefreshListener(this);
        mTxv_sort_app.setOnClickListener(this);
        mLl_sort_type_internal.setOnClickListener(this);
        mEdt_search.addTextChangedListener(mTextWatcher);
    }

    //进度条
//    private void showPgd_data() {
//        mPgd_data = new ProgressDialog(getActivity());
//        mPgd_data.setMessage("加载中,请稍候...");
//        mPgd_data.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mPgd_data.setCancelable(true);
//        mPgd_data.show();
//    }

    //弹出popupwindow
    private View showPopup_sort(View v) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.popupwindow_app, null);

        mPopup_sort = new PopupWindow(myView, 400, 400);//设置宽高
        mPopup_sort.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup_sort.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopup_sort.setBackgroundDrawable(new BitmapDrawable());//一定要设置背景

        mPopup_sort.setOutsideTouchable(true);

        mPopup_sort.setFocusable(true);

        mPopup_sort.showAsDropDown(v);
        return myView;
    }


    /**
     * 放入分享首选项
     */
    private void setSP() {
        AppInfoList applist = new AppInfoList();
        applist.setList(mThirdAppInfoList);
        String strJson = JSON.toJSONString(applist);
        SPUtil.put(getActivity(), "thirdAppList", strJson);
    }

    /**
     * 从缓存中取出分享首选项
     */
    private void getSP() {
        //取出分享首选项,一进入当前页面就显示结果(也算是初始化视图的一部分)
        if (SPUtil.contains(getActivity(), "thirdAppList")) {
            String str = SPUtil.getString(getActivity(), "thirdAppList", "");
            AppInfoList appList = JSON.parseObject(str, AppInfoList.class);
            mThirdAppInfoList = appList.getList();
            mThirdAdapter.setmAppInfoList(mThirdAppInfoList);
            mThirdAdapter.notifyDataSetChanged();
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    //todo 控件上事件的回调函数

    @Override
    public void btnClick_uninstall(int position) {
        //AppInfo appInfo = (AppInfo) mListView.getItemAtPosition(position);
        //Util.uninstallApk(getActivity(), appInfo.getPackageName(), REQ_UNINSTALL);
        AppInfo appInfo = (AppInfo) customListView.getItemAtPosition(position + 1);//此处+1 防止找不到position= -1!

        Util.uninstallApk(getActivity(), appInfo.getPackageName(), REQ_UNINSTALL);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final AppInfo appInfo = (AppInfo) parent.getItemAtPosition(position);
        new AlertDialog.Builder(getActivity())
                .setMessage("确定要打开该App吗?")
                .setCancelable(true)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                        Util.openPackage(getActivity(), appInfo.getPackageName());
                    }
                }).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfo appInfo = (AppInfo) parent.getItemAtPosition(position);
        Util.openSysDetail(getActivity(), appInfo.getPackageName());
        return true;
    }


    //排序按钮事件
    @Override
    public void onClick(View v) {
        /**
         * 箭头变化
         */
        /*if (mImgv_arrow.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.arrowdown).getConstantState())
                || mPopup_sort.isShowing()) {
            mImgv_arrow.setImageResource(R.drawable.arrowup);
        }else if (mImgv_arrow.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.arrowup).getConstantState())
                || !mPopup_sort.isShowing()){
            mImgv_arrow.setImageResource(R.drawable.arrowdown);
        }*/


        /**
         * 弹出排序选项popup窗
         */
        View myViewToPopup = showPopup_sort(v);

        myViewToPopup.findViewById(R.id.ll_sort_size).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Util.show(getActivity(), currSortType + "");
                //Util.show(getActivity(), "占用空间");
                currSortType = SORT_TYPE_SIZE;
                currComparator = sizeComparator;
                mPopup_sort.dismiss();
                mTxv_sort_app.setText("占用空间排序");
                updateData_sort(currSortType);//把选择的比较方式传入,然后按排好的顺序更新列表

            }
        });


        myViewToPopup.findViewById(R.id.ll_sort_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Util.show(getActivity(), "安装时间");
                currSortType = SORT_TYPE_DATE;
                currComparator = dateComparator;
                mPopup_sort.dismiss();
                mTxv_sort_app.setText("安装时间排序");
                updateData_sort(currSortType);//把选择的比较方式传入,然后按排好的顺序更新列表
            }
        });

        myViewToPopup.findViewById(R.id.ll_sort_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Util.show(getActivity(), "应用名称");
                currSortType = SORT_TYPE_NAME;
                currComparator = nameComparator;
                mPopup_sort.dismiss();
                mTxv_sort_app.setText("应用名称排序");
                updateData_sort(currSortType);//把选择的比较方式传入,然后按排好的顺序更新列表
            }
        });


    }


    /**
     * 下拉刷新事件
     */
    @Override
    public void onRefresh() {
        new AsyncTask<Void, Void, Void>() {
            /**
             * 下拉事件后台逻辑
             * @param params
             * @return
             */
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1000);//给用户正在刷新列表的体验,其实毫无意义
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //下拉重新加载所有的数据.
                initData();

                return null;
            }

            /**
             * 下拉刷新事件提交
             * @param aVoid
             */
            @Override
            protected void onPostExecute(Void aVoid) {
                KEYWORD = "";//这里要把搜索的关键字重置,否则Util的高亮会报"数组下标越界的exception"
                mEdt_search.setText("");//清空搜索栏
                /**
                 * 以下是可选项:
                 */
                currSortType = SORT_TYPE_SIZE;//排序条件回到默认状态
                mTxv_sort_app.setText("选择排序方式");

                mThirdAdapter.notifyDataSetChanged();
                customListView.onRefreshComplete();
                super.onPostExecute(aVoid);
            }
        }.execute(null, null, null);
    }
}
