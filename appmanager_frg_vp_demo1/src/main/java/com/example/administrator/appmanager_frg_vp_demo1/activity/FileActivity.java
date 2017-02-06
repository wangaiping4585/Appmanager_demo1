package com.example.administrator.appmanager_frg_vp_demo1.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.appmanager_frg_vp_demo1.R;
import com.example.administrator.appmanager_frg_vp_demo1.adapter.FileInfo_adapter;
import com.example.administrator.appmanager_frg_vp_demo1.application.SysApplication;
import com.example.administrator.appmanager_frg_vp_demo1.entity.FileInfo;
import com.example.administrator.appmanager_frg_vp_demo1.util.FileUtil;
import com.example.administrator.appmanager_frg_vp_demo1.util.Util;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

public class FileActivity extends Activity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener, View.OnClickListener {
    //按两次退出
    private long exitTime = 0;

    //底部Activity的选项卡
    private LinearLayout mLl_app;
    private LinearLayout mLl_option;
    private TextView mTxv_file;

    private LinearLayout mLl_exit;//退出箭头

    private LayoutInflater mInflater;//反射器

    //搜索相关
    private static final int MSG_DATA = 1;//数据加载message.what
    private static final int REQ_UNINSTALL = 0;//删除数据的请求码
    public static String KEYWORD = "";//搜索的关键字

    private EditText mEdt_search;//搜索框
    private TextView mTxv_search_result;//搜索结果文字
    private TextView mTxv_sort_file;//排序方式

    //排序相关
    private LinearLayout mLl_sort_type_internal;//排序方式线性布局
    private Button mBtn_sort;//排序选择按钮
    public static PopupWindow mPopup_sort;//排序选择popup
    private TextView mTxv_sort;//排序信息
    private RelativeLayout mLl_corner;//右上角下拉布局

    public static PopupWindow mPopup_option;//设置popup

    //全局滚动条变化文字,用于监听点击事件
    TextView txvInScrollBegin = null;
    TextView txvInScroll = null;

    int currpos = 0;//当前行号,用于记录位置
    String[] pathsArr;//全局路径数组

    ListView mFileInfoListView;
    List<FileInfo> mFileInfoList;
    FileInfo_adapter mFileInfoAdapter;

    LinearLayout ll_topbar2;
    HorizontalScrollView hsv_topbar2;

    TextView txv_nofile;
    ImageView imgv_nofile;

    String currPath; // 当前目录
    String parentPath; // 上级目录

    Stack<Integer> posStack = new Stack<Integer>();//用栈的方式,记录上次操作位置

    final String ROOT = FileUtil.getSDCardPath(); //SDCard根目录

    public static final int T_NOTHING = -1;
    public static final int T_DIR = 0;// 文件夹
    public static final int T_FILE = 1;// 文件

    // 声明一个变量,记录当前窗口显示的第一个item的下标
    int firstItemPosition = 0;


    //排序其他
    public static final int SORT_TYPE_NAME = 0;//按名称排序
    public static final int SORT_TYPE_DATE = 1;//按日期排序
    public static final int SORT_TYPE_SIZE = 2;//按大小排序

    public static int currSortType = SORT_TYPE_NAME;//当前排序
    Comparator<FileInfo> currComparator = null;//当前比较器


    /**
     * 分组排序
     *
     * @param list
     * @return
     */
    private List<FileInfo> getGroupList(List<FileInfo> list) {
        List<FileInfo> dirs = new ArrayList<FileInfo>();// 文件夹列表
        List<FileInfo> files = new ArrayList<FileInfo>();// 文件列表
        for (int i = 0; i < list.size(); i++) {
            FileInfo item = list.get(i);
            if (item.type == T_DIR) {
                dirs.add(item);
            } else {
                files.add(item);
            }
        }
        if (currSortType == SORT_TYPE_NAME) {
            currComparator = nameComparator;
        } else if (currSortType == SORT_TYPE_DATE) {
            currComparator = dateComparator;
        } else if (currSortType == SORT_TYPE_SIZE) {
            currComparator = sizeComparator;
        }
        // 排序
        Collections.sort(dirs, currComparator);
        Collections.sort(files, currComparator);
        // 合并
        dirs.addAll(files);//文件夹-文件
        return dirs;

        //files.addAll(dirs);//文件-文件夹
        //return files;
    }

    // 大小比较器
    Comparator<FileInfo> sizeComparator = new Comparator<FileInfo>() {
        @Override
        public int compare(FileInfo lhs, FileInfo rhs) {
            if (rhs.byteSize > lhs.byteSize) {
                return 1;
            } else if (rhs.byteSize == lhs.byteSize) {
                return 0;
            } else {
                return -1;
            }
        }
    };
    // 应用名比较器  中文排序效果不好
    Comparator<FileInfo> nameComparator = new Comparator<FileInfo>() {
        @Override
        public int compare(FileInfo lhs, FileInfo rhs) {
            Collator c = Collator.getInstance(Locale.CHINA);
            return c.compare(lhs.name, rhs.name);
        }
    };
    // 日期比较器
    Comparator<FileInfo> dateComparator = new Comparator<FileInfo>() {
        @Override
        public int compare(FileInfo lhs, FileInfo rhs) {
            if (rhs.ltime > lhs.ltime) {
                return 1;
            } else if (rhs.ltime == lhs.ltime) {
                return 0;
            } else {
                return -1;
            }
        }
    };


    //----------------------------------------------------------------------------------------------
    //todo FileActivity回调函数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加到activity集合中
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_file);

        // 打印SDCard根目录
        String SDroot = Environment.getExternalStorageDirectory().getAbsolutePath();
        //Util.show(this, "根目录:" + SDroot);

        //初始化控件
        initView();

        //初始化事件
        initEvent();

        //加载数据
        initData(ROOT);// 加载数据
    }

    @Override
    public void onBackPressed() {
        // 返回 --> 打开上级
        if (currPath.equals(ROOT)) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Util.doExit(this);
            }
        } else {
            //Utils.print("onBackPressed--当前:"+currPath+"\n上级:"+parentPath);
            //打开上级目录
            firstItemPosition = posStack.pop();
            mFileInfoListView.setSelection(firstItemPosition);
            initData(parentPath);

        }

    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.trans, R.anim.trans_right_out);
        super.finish();
    }

    //----------------------------------------------------------------------------------------------
    //todo Handler Runnable TextWatcher

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
                //Util.show(getApplicationContext(), "afterTextChanged = (" + KEYWORD + ")");
                //搜索
                //每次都要从全部的结果集里搜索
                mFileInfoList = Util.getSearchResultForFile(FileUtil.getListData(currPath), KEYWORD);//随写随搜

                //todo 显示搜索结果的提示文字!!

                /*无文件时提示文字和图标*/
                showNoResult();

                //Util.show(getApplicationContext(),mFileInfoList.toString());
                mFileInfoAdapter.setList(mFileInfoList);
                mFileInfoAdapter.notifyDataSetChanged();
                //多次搜索缩小范围
                //updateData_sort(currSortType);
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0) {
                mFileInfoListView.setSelection(firstItemPosition);//???
            } else if (msg.what == 1) {

                /*无文件时提示文字和图标*/
                showNoResult();

                mFileInfoAdapter.setList(mFileInfoList);
                mFileInfoAdapter.notifyDataSetChanged();// 刷新视图(原地)
                //mTxv_file_path.setText(currPath);//显示的路径

                //切割的字符串数组
                pathsArr = Util.cutPathToArray(currPath);
                //根据arr数组动态生成TextView
                createView_scroll(pathsArr);
//                for (int i = 0; i < paths.length; i++) {
//                    Log.i("wxs","currPath元素["+i+"]:"+paths[i]);
//                    Log.i("wxs","currPath元素["+i+"]:"+paths[i]);
//                }

                mFileInfoListView.setSelection(firstItemPosition);
            }
        }
    };

    /**
     * 任务内容:渲染完成scrollview后,滚动到制定位置
     */
    private Runnable mScrollToBottom = new Runnable() {
        @Override
        public void run() {
            int off = hsv_topbar2.getMeasuredWidth();//计算移动量
            Log.i("wxs", "移动距离==" + off);
            if (off > 0) {
                hsv_topbar2.scrollTo(off, 0);//自动移动X轴
            }
        }
    };


    //----------------------------------------------------------------------------------------------
    //todo 封装的方法 initXxx() updateXxx() showXxx() 等等
    //初始化控件
    private void initView() {
        /*显示App icon左侧的back键*/
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        //底部Activity的选项卡
        mLl_app = (LinearLayout) findViewById(R.id.ll_app);
        mLl_option = (LinearLayout) findViewById(R.id.ll_option);
        mTxv_file = (TextView) findViewById(R.id.txv_file);

        mLl_app.setBackgroundColor(Color.WHITE);
        mLl_option.setBackgroundColor(Color.WHITE);
        mTxv_file.setTextColor(Color.WHITE);

        //退出箭头
        mLl_exit = (LinearLayout) findViewById(R.id.ll_exit);

        //创建topbar横向滚动条
        ll_topbar2 = (LinearLayout) findViewById(R.id.ll_topbar2);
        hsv_topbar2 = (HorizontalScrollView) findViewById(R.id.hsv_topbar2);
        //createView_scroll();

        //没有文件时显示
        txv_nofile = (TextView) findViewById(R.id.txv_nofile);
        imgv_nofile = (ImageView) findViewById(R.id.imgv_nofile);

        mFileInfoListView = (ListView) findViewById(R.id.lv_file);
        mFileInfoAdapter = new FileInfo_adapter(this);
        mFileInfoListView.setAdapter(mFileInfoAdapter);

        //搜索框
        mEdt_search = (EditText) findViewById(R.id.edt_search_input);

        //排序
        mLl_sort_type_internal = (LinearLayout) findViewById(R.id.ll_sort_type_internal_file);//排序线性布局
        mTxv_sort_file = (TextView) findViewById(R.id.txv_sort_file);

        mLl_corner = (RelativeLayout) findViewById(R.id.ll_corner);
    }


    /**
     * 创建横向滚动条
     */
    private void createView_scroll(String[] pathsArr) {


        ll_topbar2.removeAllViews();//先清空一下
        txvInScrollBegin = new TextView(this);
        txvInScrollBegin.setText("本地存储");//设置属性
        txvInScrollBegin.setTextSize(16f);
        txvInScrollBegin.setTextColor(Color.GRAY);

        //todo 此处有点击事件
        txvInScrollBegin.setOnClickListener(this);//关联点击事件

        ll_topbar2.addView(txvInScrollBegin);//把textview加入到容器中

        if (pathsArr != null) {
            for (int i = 0; i < pathsArr.length; i++) {
                txvInScroll = new TextView(this);
                //一定要设置tag,否则onclick的v.getId()不好使,会无法找到路径
                txvInScroll.setTag(i);//附属属性,(这里防止id重叠,自定义的),利用标签属性帮助我们保存数据,用于区分
                txvInScroll.setText(" >> " + pathsArr[i] + "");
                txvInScroll.setTextColor(Color.GRAY);
                txvInScroll.setTextSize(16f);
                //txvInScroll.setId(i);
                txvInScroll.setOnClickListener(this);
                Log.i("wxs", "w=" + txvInScroll.getMeasuredWidth());//动态获取组件宽高
                ll_topbar2.addView(txvInScroll);
            }
            //hs.fullScroll(HorizontalScrollView.FOCUS_DOWN);
            //关键是这里,发送一个延迟消息,让scroll在次时间内完成渲染
            handler.postDelayed(mScrollToBottom, 500);//进入消息队列
        }


    }

    //初始化事件
    private void initEvent() {
        mLl_app.setOnClickListener(this);
        mLl_option.setOnClickListener(this);

        mFileInfoListView.setOnItemClickListener(this);
        mFileInfoListView.setOnScrollListener(this);
        mLl_sort_type_internal.setOnClickListener(this);
        mLl_exit.setOnClickListener(this);
        mEdt_search.addTextChangedListener(mTextWatcher);
        mLl_corner.setOnClickListener(this);
    }

    //加载数据
    private void initData(final String path) {
        //加载数据放到线程里
        new Thread() {
            @Override
            public void run() {
                currPath = path;
                File file = new File(path);
                parentPath = file.getParent();//更新了上级目录(返回)

                mFileInfoList = FileUtil.getListData(path);//数据
                mFileInfoList = getGroupList(mFileInfoList);//分组排序

                handler.sendEmptyMessage(1);
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

        Collections.sort(mFileInfoList, currComparator);//排序

        //适配器关联数据
        mFileInfoAdapter.setList(mFileInfoList);
        //动态更新
        mFileInfoAdapter.notifyDataSetChanged();
    }


    /**
     * 没有文件时进行提示
     */
    private void showNoResult() {
        if (mFileInfoList.size() == 0) {
            txv_nofile.setVisibility(View.VISIBLE);
            imgv_nofile.setVisibility(View.VISIBLE);
        } else {
            txv_nofile.setVisibility(View.GONE);
            imgv_nofile.setVisibility(View.GONE);
        }
    }


    //弹出设置popupwindow
    private View showPopup_option(View v) {
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.popupwindow_file_option, null);

        mPopup_option = new PopupWindow(myView, 400, 400);//设置宽高
        mPopup_option.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup_option.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopup_option.setBackgroundDrawable(new BitmapDrawable());//一定要设置背景

        mPopup_option.setOutsideTouchable(true);

        mPopup_option.setFocusable(true);

        mPopup_option.showAsDropDown(v);
        return myView;
    }


    //真实路径
    public static String realCurrPath = "";

    //弹出排序popupwindow
    private View showPopup_sort(View v) {
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.popupwindow_file, null);

        mPopup_sort = new PopupWindow(myView, 400, 400);//设置宽高
        mPopup_sort.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup_sort.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopup_sort.setBackgroundDrawable(new BitmapDrawable());//一定要设置背景

        mPopup_sort.setOutsideTouchable(true);

        mPopup_sort.setFocusable(true);

        mPopup_sort.showAsDropDown(v);
        return myView;
    }

    //----------------------------------------------------------------------------------------------
    //todo 各种事件回调函数


    @Override
    public void onClick(View v) {
        //底部Activity跳转
        if (v.getId() == R.id.ll_app) {
            Intent intentApp = new Intent();
            intentApp.setClass(this, AppActivity.class);
            startActivity(intentApp);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
        }

        if (v.getId() == R.id.ll_option) {
            Intent intentMe = new Intent();
            intentMe.setClass(this, OptionActivity.class);
            startActivity(intentMe);
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans);
        }


        //右上角下拉菜单
        if (v.getId() == R.id.ll_corner) {
            View myViewToPopup = showPopup_option(v);
            myViewToPopup.findViewById(R.id.ll_backapp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(FileActivity.this, AppActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans);
                    mPopup_option.dismiss();
                    //finish();
                }
            });

            myViewToPopup.findViewById(R.id.ll_backoption).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(FileActivity.this, OptionActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans);
                    mPopup_option.dismiss();
                    //finish();
                }
            });
        }


        //退出箭头
        if (v.getId() == R.id.ll_exit) {
            Util.doExit(this);
        }


//        ll_topbar2 = (LinearLayout) findViewById(R.id.ll_topbar2);
//        hsv_topbar2
        //点击了滚动条
        //做好判断,防止空指针nullPointer
        if (txvInScrollBegin != null && txvInScroll != null) {
            if (v.getId() == txvInScrollBegin.getId() || v.getId() == txvInScroll.getId()) {
                //点击动态textView 到达点击的路径
                TextView onclickTxvInScorll = (TextView) v;
                int id = onclickTxvInScorll.getId();
                if (onclickTxvInScorll.getTag() != null) {//通过拿到tag的方式来寻找路径,因为这里的v.getId()不好使
                    int tag = Integer.parseInt(onclickTxvInScorll.getTag().toString());
                    //Util.show(this,"------------------------->"+id);
                    String path = Util.getJoinPath(pathsArr, tag + 1);//id
                    initData(path);
                } else {//点击本地存储返回根路径
                    String path = Util.getJoinPath(pathsArr, 0);//id
                    initData(path);
                }
            }
        }

        //点击了排序
        if (v.getId() == R.id.ll_sort_type_internal_file) {
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
                    mTxv_sort_file.setText("占用空间排序");
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
                    mTxv_sort_file.setText("按照时间排序");
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
                    mTxv_sort_file.setText("文件名称排序");
                    updateData_sort(currSortType);//把选择的比较方式传入,然后按排好的顺序更新列表
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获取行的信息(实体类对象)
        FileInfo item = (FileInfo) parent.getItemAtPosition(position);
        //判断文件/文件夹
        if (item.type == T_DIR) {
            //posStack.push(T_DIR);//入栈
            posStack.push(currpos);//入栈 记录listview当前位置
            //进入此文件夹
            initData(item.path);

        } else {
            //如果是文件
            String end = FileUtil.getFileEXT(item.name).toLowerCase();
            if (FileUtil.checkEndsInArray(end, new String[]{"txt", "ini", "log", "java", "xml", "html"})) {
                //用自己的文本阅读器打开
                Util.show(this, FileUtil.readSdcardFile(item.path));
            } else {
                FileUtil.openFile(this, new File(item.path));
            }
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * 用于记录当前行号
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        currpos = firstVisibleItem;
    }

    //----------------------------------------------------------------------------------------------


}
