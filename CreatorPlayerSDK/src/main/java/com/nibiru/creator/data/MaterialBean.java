package com.nibiru.creator.data;

import java.util.List;

public class MaterialBean {
    private long createTime;
    private int creator;
    private String iconImg;
    private int id;
    private String nameZh;
    private List<String> nptPathList;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getIconImg() {
        return iconImg;
    }

    public void setIconImg(String iconImg) {
        this.iconImg = iconImg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public List<String> getNptPathList() {
        return nptPathList;
    }

    public void setNptPathList(List<String> nptPathList) {
        this.nptPathList = nptPathList;
    }

    @Override
    public String toString() {
        return "MaterialBean{" +
                "createTime=" + createTime +
                ", creator=" + creator +
                ", iconImg='" + iconImg + '\'' +
                ", id=" + id +
                ", nameZh='" + nameZh + '\'' +
                ", nptPathList=" + nptPathList +
                '}';
    }
}
