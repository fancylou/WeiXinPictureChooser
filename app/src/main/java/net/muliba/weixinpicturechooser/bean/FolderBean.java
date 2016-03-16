package net.muliba.weixinpicturechooser.bean;

/**
 * Created by FancyLou on 2016/3/15.
 */
public class FolderBean {
    private String dir;//文件夹路径
    private String firstImgPath;
    private int count;
    private String name;//文件夹名称


    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndexof = this.dir.lastIndexOf("/");
        this.name = this.dir.substring(lastIndexof);
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
