package com.example.administrator.appmanager_frg_vp_demo1.entity;

/**
 * Created by Administrator on 2016/12/1.
 */
public class FileInfo {

    public int icon; //图标
    public String name;
    public String size;
    public String time;
    public String path;// 最重要
    public long ltime;// 时间
    public long byteSize; // 真实大小

    public int type;//文件类型(文件/文件夹)

    public int items;//子项目数

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLtime() {
        return ltime;
    }

    public void setLtime(long ltime) {
        this.ltime = ltime;
    }

    public long getByteSize() {
        return byteSize;
    }

    public void setByteSize(long byteSize) {
        this.byteSize = byteSize;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "\nFileInfo{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", time='" + time + '\'' +
                ", path='" + path + '\'' +
                ", ltime=" + ltime +
                ", byteSize=" + byteSize +
                ", type=" + type +
                ", items=" + items +
                '}';
    }
}
