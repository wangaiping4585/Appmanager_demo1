package com.example.administrator.appmanager_frg_vp_demo1.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.appmanager_frg_vp_demo1.R;
import com.example.administrator.appmanager_frg_vp_demo1.activity.FileActivity;
import com.example.administrator.appmanager_frg_vp_demo1.entity.FileInfo;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件读取工具类
 *
 * @author
 */
public class FileUtil {

    /**
     * 获取SDcard根路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 将文件写到SDCard
     *
     * @param fileName  绝对路径
     * @param write_str
     */
    public static void writeFileSdcardFile(String fileName, String write_str) {
        try {

            // 判断文件是否存在
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();// 创建
            }

            FileOutputStream out = new FileOutputStream(fileName);
            byte[] bytes = write_str.getBytes();
            out.write(bytes);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 读SD中的文件
     *
     * @param fileName
     * @return
     */
    public static String readFileSdcardFile(String fileName) {
        String res = "";
        try {
            // 获取输入流
            FileInputStream in = new FileInputStream(fileName);

            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String readSdcardFile(String fileName) {
        //获取当前SDCard卡状态，若已挂载则继续
        String content = null;
        File file = new File(fileName);
        try {
            if (file.exists()) {
                FileInputStream in = new FileInputStream(file);
                //构造StringBuffer，动态添加数据内容
                StringBuffer sb = new StringBuffer();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = in.read(buffer)) != -1) {

                    sb.append(new String(buffer).trim());
                    //StringBuffer添加数据后，清空字节数组buffer
                    buffer = new byte[1024];
                }
                content = sb.toString();
                Log.i("spl", "读取文件成功，内容是:" + content);
                in.close();
            }
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("spl", "读取文件失败!");
        return null;
    }

    // 写数据 data/data/<包名>/files/

    /**
     * 写数据 data/data/package
     *
     * @param fileName
     * @param writestr
     * @param context
     */
    public static void writeDataFile(String fileName, String writestr, Context context) {
        try {

            // 获取文件输出流
            FileOutputStream out = context.openFileOutput(fileName,
                    context.MODE_PRIVATE);

            // 通过输出流把文本接入
            byte[] bytes = writestr.getBytes();
            out.write(bytes);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 读数据

    /**
     * 读数据
     *
     * @param fileName
     * @param context
     * @return
     */
    public static String readDataFile(String fileName, Context context) {
        String res = "";
        try {
            // 获取输入流
            FileInputStream in = context.openFileInput(fileName);

            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }

    /**
     * res/raw
     *
     * @param context 上下文
     * @param resId   资源ID(文件ID)
     * @return
     */
    public static String readRawFile(Context context, int resId) {
        String res = "";
        try {
            //得到资源中的Raw数据流(输入流)
            InputStream in = context.getResources().openRawResource(resId);

            //得到数据的大小
            int length = in.available();
            // 开辟缓冲空间,空间等于文件大小
            byte[] buffer = new byte[length];
            //读取数据
            in.read(buffer);
            //依test.txt的编码类型选择合适的编码，如果不调整会乱码
            res = EncodingUtils.getString(buffer, "UTF-8");
            //关闭
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     * Assets 文件的读取
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String readAssetsFile(Context context, String fileName) {
        String res = "";

        try {

            //得到资源中的asset数据流
            // AssetManager assetManager = context.getResources().getAssets()
            InputStream in = context.getResources().getAssets().open(fileName);

            int length = in.available();
            byte[] buffer = new byte[length];

            in.read(buffer);
            in.close();
            res = EncodingUtils.getString(buffer, "UTF-8");

        } catch (Exception e) {

            e.printStackTrace();

        }
        return res;
    }

    /**
     * 检查扩展名end 是否在ends数组中
     *
     * @param end
     * @param ends
     * @return
     */
    public static boolean checkEndsInArray(String end, String[] ends) {
        for (String aEnd : ends) {
            if (end.equals(aEnd)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件类型 例如"mp3","txt"
     *
     * @param fileName 文件名
     * @return
     */
    public static String getFileType(String fileName) {
        if (fileName != null && fileName.contains(".")
                && fileName.lastIndexOf(".") != fileName.length() - 1) {
            return fileName.substring(fileName.lastIndexOf(".") + 1,
                    fileName.length());
        } else {
            return ""; // 这里一定要写空字符串，写null会造成空指针
        }
    }

    /**
     * 将Bitmap对象保存的SDCard上
     *
     * @param bm
     * @param filename 需要给个绝对路径
     */
    public static void saveImage(Bitmap bm, String filename) {
        File f = new File(filename);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i("spl", "已经保存");
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * 从SDCard中读取出Bitmap对象
     *
     * @param path
     * @return
     */
    public static Bitmap getImageFile(String path) {
        Bitmap userImage = null;
        try {// 从文件中获取照片

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; //Downsample 10x
            userImage = BitmapFactory.decodeFile(path, options);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userImage;
    }


    /**
     * 打开文件
     *
     * @param context
     * @param aFile
     */
    public static void openFile(Context context, File aFile) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        //取得文件名
        String fileName = aFile.getName();
        String end = getFileType(fileName).toLowerCase();
        if (aFile.exists()) {
            // 根据不同的文件类型来打开文件
            if (checkEndsInArray(end, new String[]{"png", "gif", "jpg", "bmp"})) {
                // 图片
                intent.setDataAndType(Uri.fromFile(aFile), "image/*");
            } else if (checkEndsInArray(end, new String[]{"apk"})) {
                // apk
                intent.setDataAndType(Uri.fromFile(aFile), "application/vnd.android.package-archive");
            } else if (checkEndsInArray(end, new String[]{"mp3", "amr", "ogg", "mid", "wav"})) {
                // audio
                intent.setDataAndType(Uri.fromFile(aFile), "audio/*");
            } else if (checkEndsInArray(end, new String[]{"mp4", "3gp", "mpeg", "mov"})) {
                // video
                intent.setDataAndType(Uri.fromFile(aFile), "video/*");
            } else if (checkEndsInArray(end, new String[]{"txt", "ini", "log", "java", "xml", "html"})) {
                // text
                intent.setDataAndType(Uri.fromFile(aFile), "text/*");
            } else if (checkEndsInArray(end, new String[]{"doc", "docx"})) {
                // word
                intent.setDataAndType(Uri.fromFile(aFile), "application/msword");
            } else if (checkEndsInArray(end, new String[]{"xls", "xlsx"})) {
                // excel
                intent.setDataAndType(Uri.fromFile(aFile), "application/vnd.ms-excel");
            } else if (checkEndsInArray(end, new String[]{"ppt", "pptx"})) {
                // ppt
                intent.setDataAndType(Uri.fromFile(aFile), "application/vnd.ms-powerpoint");
            } else if (checkEndsInArray(end, new String[]{"chm"})) {
                // chm
                intent.setDataAndType(Uri.fromFile(aFile), "application/x-chm");
            } else {
                // 其他
                intent.setDataAndType(Uri.fromFile(aFile), "application/" + end);
            }
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "没有找到适合打开此文件的应用", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * 检查文件名的结尾是否是媒体 类型
     *
     * @param fileName
     * @return
     */
    public static int checkFileName(String fileName) {
        String end = getFileType(fileName).toLowerCase();
        if (checkEndsInArray(end, new String[]{"png", "gif", "jpg", "bmp"})) {
            // 图片
            return 1;
        } else if (checkEndsInArray(end, new String[]{"mp3", "amr", "ogg", "mid", "wav"})) {
            // audio
            return 2;
        } else if (checkEndsInArray(end, new String[]{"mp4", "3gp", "mpeg", "mov"})) {
            // video
            return 3;
        }

        return 0;
    }

    /**
     * 移动文件
     *
     * @param srcFileName 源文件完整路径
     * @param destDirName 目的目录完整路径
     * @return 文件移动成功返回true，否则返回false
     */
    public static boolean moveFile(String srcFileName, String destDirName) {

        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();

        return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));
    }


    /**
     * 截取文件的扩展名
     *
     * @param filename 文件全名
     * @return 扩展名(mp3, txt)
     */
    public static String getFileEXT(String filename) {
        if (filename.contains(".")) {
            int dot = filename.lastIndexOf(".");// 123.abc.txt
            String ext = filename.substring(dot + 1);
            return ext;
        } else {
            return "";
        }
    }

    /**
     * 移动目录
     *
     * @param srcDirName  源目录完整路径
     * @param destDirName 目的目录完整路径
     * @return 目录移动成功返回true，否则返回false
     */
    public static boolean moveDirectory(String srcDirName, String destDirName) {

        File srcDir = new File(srcDirName);
        if (!srcDir.exists() || !srcDir.isDirectory())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();

        /**
         * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹
         * 注意移动文件夹时保持文件夹的树状结构
         */
        File[] sourceFiles = srcDir.listFiles();
        for (File sourceFile : sourceFiles) {
            if (sourceFile.isFile())
                moveFile(sourceFile.getAbsolutePath(), destDir.getAbsolutePath());
            else if (sourceFile.isDirectory())
                moveDirectory(sourceFile.getAbsolutePath(),
                        destDir.getAbsolutePath() + File.separator + sourceFile.getName());
            else
                ;
        }
        return srcDir.delete();
    }

    /**
     * 通过传入的路径,返回该路径下的所有的文件和文件夹列表
     *
     * @param path
     * @return
     */
    public static List<FileInfo> getListData(String path) {

        List<FileInfo> list = new ArrayList<FileInfo>();

        File pfile = new File(path);// 文件对象
        File[] files = null;// 声明了一个文件对象数组
        if (pfile.exists()) {// 判断路径是否存在
            files = pfile.listFiles();// 该文件对象下所属的所有文件和文件夹列表
        }

        if (files != null && files.length > 0) {// 非空验证
            for (File file : files) {// foreach循环遍历
                FileInfo item = new FileInfo();
                if (file.isDirectory()// 文件夹? 如果是文件夹
                        && file.canRead()//是否可读
                    //&&file.isHidden()//过滤隐藏

                        ) {
                    //file.isHidden();//  是否是隐藏文件
                    // 获取文件夹目录结构
                    item.icon = R.drawable.folder;//图标
                    //item.size = "";//大小
                    item.byteSize = 0;
                    item.type = FileActivity.T_DIR;

                    item.items = file.listFiles().length;//子项目数
                    item.size = "" + item.items +"个项目";//没有大小
                } else if (file.isFile()
                    //&&!file.isHidden()//如果文件不是隐藏的
                        ) {// 文件

                    String ext = getFileEXT(file.getName());
                    //Log.i("spl", "ext=" + ext);

                    // 文件的图标
                    item.icon = getDrawableIcon(ext);// 根据扩展名获取图标
                    // 文件的图标
                    //item.icon=R.drawable.default_fileicon;
                    // 文件的大小
                    String size = getSize(file.length());
                    item.size = size;// 带单位的字符串
                    item.byteSize = file.length();//真正的大小
                    item.type = FileActivity.T_FILE;

                } else {// 其它,基本用不上
                    item.icon = R.drawable.mul_file;
                }
                //下面是文件和文件夹共有的属性
                item.name = file.getName();// 名称
                item.path = file.getPath();// 路径(核心)
                // 最后修改时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = new Date(file.lastModified());
                String time = sdf.format(date);
                item.time = time;
                item.ltime = file.lastModified();
                list.add(item);//添加元素

                //最后修改时间
                //item.time = getTime(file.lastModified());
                //item.ltime = file.lastModified();//毫秒时间

            }
        }
        files = null;//释放数组资源
        return list;
    }

    /**
     * 获得与扩展名对应的图标资源id
     *
     * @param end 扩展名
     * @return
     */
    public static int getDrawableIcon(String end) {
        int id = 0;
        if (end.equals("asf")) {
            id = R.drawable.asf;
        } else if (end.equals("avi")) {
            id = R.drawable.avi;
        } else if (end.equals("bmp")) {
            id = R.drawable.bmp;
        } else if (end.equals("doc")) {
            id = R.drawable.doc;
        } else if (end.equals("gif")) {
            id = R.drawable.gif;
        } else if (end.equals("html")) {
            id = R.drawable.html;
        } else if (end.equals("apk")) {
            id = R.drawable.iapk;
        } else if (end.equals("ico")) {
            id = R.drawable.ico;
        } else if (end.equals("jpg")) {
            id = R.drawable.jpg;
        } else if (end.equals("log")) {
            id = R.drawable.log;
        } else if (end.equals("mov")) {
            id = R.drawable.mov;
        } else if (end.equals("mp3")) {
            id = R.drawable.mp3;
        } else if (end.equals("mp4")) {
            id = R.drawable.mp4;
        } else if (end.equals("mpeg")) {
            id = R.drawable.mpeg;
        } else if (end.equals("pdf")) {
            id = R.drawable.pdf;
        } else if (end.equals("png")) {
            id = R.drawable.png;
        } else if (end.equals("ppt")) {
            id = R.drawable.ppt;
        } else if (end.equals("rar")) {
            id = R.drawable.rar;
        } else if (end.equals("txt") || end.equals("dat") || end.equals("ini")
                || end.equals("java")) {
            id = R.drawable.txt;
        } else if (end.equals("vob")) {
            id = R.drawable.vob;
        } else if (end.equals("wav")) {
            id = R.drawable.wav;
        } else if (end.equals("wma")) {
            id = R.drawable.wma;
        } else if (end.equals("wmv")) {
            id = R.drawable.wmv;
        } else if (end.equals("xls")) {
            id = R.drawable.xls;
        } else if (end.equals("xml")) {
            id = R.drawable.xml;
        } else if (end.equals("zip")) {
            id = R.drawable.zip;
        } else if (end.equals("3gp") || end.equals("flv")) {
            id = R.drawable.file_video;
        } else if (end.equals("amr")) {
            id = R.drawable.file_audio;
        } else {
            id = R.drawable.default_fileicon;
        }
        return id;
    }

    /**
     * 格式转换应用大小 单位"B,KB,MB,GB"
     */
    public static String getSize(float length) {

        long kb = 1024;
        long mb = 1024 * kb;
        long gb = 1024 * mb;
        if (length < kb) {
            return String.format("%dB", (int) length);
        } else if (length < mb) {
            return String.format("%.2fKB", length / kb);
        } else if (length < gb) {
            return String.format("%.2fMB", length / mb);
        } else {
            return String.format("%.2fGB", length / gb);
        }
    }


}
