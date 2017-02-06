package com.example.administrator.appmanager_frg_vp_demo1.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.appmanager_frg_vp_demo1.R;
import com.example.administrator.appmanager_frg_vp_demo1.activity.FileActivity;
import com.example.administrator.appmanager_frg_vp_demo1.entity.FileInfo;
import com.example.administrator.appmanager_frg_vp_demo1.fragment.AppThirdFragment;
import com.example.administrator.appmanager_frg_vp_demo1.util.FileUtil;
import com.example.administrator.appmanager_frg_vp_demo1.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */
public class FileInfo_adapter extends BaseAdapter {

    private List<FileInfo> mFileInfoList;
    private LayoutInflater mInfater;
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mOptions;

    public FileInfo_adapter(Context context) {
        this.mInfater = LayoutInflater.from(context);
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)// 加载时的图标
                .showImageForEmptyUri(R.drawable.ic_launcher)//地址错误时的图标
                .showImageOnFail(R.drawable.ic_launcher)//加载失败的图标
                        //.resetViewBeforeLoading(true)// 在加载前重置View
                .cacheInMemory(false)
                .cacheOnDisk(true)// 在SDCard上缓存
                .imageScaleType(ImageScaleType.EXACTLY)//拉伸类型
                .bitmapConfig(Bitmap.Config.RGB_565)//图片的解码格式
                .considerExifParams(true)// 考虑JPEG的旋转
                .displayer(new RoundedBitmapDisplayer(16))// 淡入特效时间
                .build();
    }

    public void setList(List<FileInfo> list) {
        this.mFileInfoList = list;
    }


    @Override
    public int getCount() {
        return (mFileInfoList == null) ? 0 : mFileInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            //反射布局
            convertView = mInfater.inflate(R.layout.item_listview_file, null);
            holder = new ViewHolder();
            //反射控件
            holder.imgv_file_icon = (ImageView) convertView.findViewById(R.id.imgv_file_icon);
            holder.txv_file_name = (TextView) convertView.findViewById(R.id.txv_file_name);
            holder.txv_file_desc = (TextView) convertView.findViewById(R.id.txv_file_desc);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //给控件赋值
        FileInfo itemFileInfo = mFileInfoList.get(position);

        //给imageview赋值
        //获得扩展名
        String end = FileUtil.getFileEXT(itemFileInfo.name);
        if (FileUtil.checkEndsInArray(end, new String[]{"mp4", "3gp", "mpeg", "mov", "flv"})) {
            // 视频缩略图
            // 生成缩略图 Thumbnails.MICRO_KIND(最小的缩略图)
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
                    itemFileInfo.path, MediaStore.Video.Thumbnails.MICRO_KIND);
            holder.imgv_file_icon.setImageBitmap(bitmap);// 设定ImageView的Bitmap
            bitmap = null;
        } else if (FileUtil.checkEndsInArray(end, new String[]{"png", "gif", "jpg", "bmp"})) {

            // 图片缩略图
            mImageLoader.displayImage("file://" + itemFileInfo.path, holder.imgv_file_icon, mOptions);

        } else {
            holder.imgv_file_icon.setImageResource(itemFileInfo.icon);//之前的图标
        }

        //给textview名称赋值


        //todo 添加搜索高亮
        //if用indexOf()判断 如果不是-1 再进行
        if (itemFileInfo.name.indexOf(FileActivity.KEYWORD) != -1) {
            if (FileActivity.KEYWORD == null || FileActivity.KEYWORD.equals("")) {//没有搜索
                holder.txv_file_name.setText(itemFileInfo.name);
            } else {
                holder.txv_file_name.setText(Util.highLightText(itemFileInfo.name, FileActivity.KEYWORD));
            }
        }

        /*if (FileActivity.KEYWORD == null || FileActivity.KEYWORD.equals("")) {//没有搜索
            holder.txv_file_name.setText(itemFileInfo.name);
        } else {
            holder.txv_file_name.setText(Util.highLightText(itemFileInfo.name, FileActivity.KEYWORD));
        }*/

        //holder.txv_file_name.setText(itemFileInfo.name);

        //给textview详细(时间)赋值
        holder.txv_file_desc.setText(itemFileInfo.size + "  " + itemFileInfo.time);

        return convertView;
    }


    public class ViewHolder {
        ImageView imgv_file_icon;
        TextView txv_file_name;
        TextView txv_file_desc;

    }
}
