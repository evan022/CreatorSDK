package com.nibiru.creator.data;

public class NptData {
    private String name;
    private String sortLetter;
    private String textureName;
    private long lastModified;

    private int id;
    private String descEn;
    private String descZh;
    private String iconImg;
    private String nameEn;
    private String nameZh;
    private int typePid;
    private long updateTime;
    private String updateTimeStr;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetter() {
        return sortLetter;
    }

    public void setSortLetter(String sortLetter) {
        this.sortLetter = sortLetter;
    }

    public String getTextureName() {
        return textureName;
    }

    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getDescEn() {
        return descEn;
    }

    public void setDescEn(String descEn) {
        this.descEn = descEn;
    }

    public String getDescZh() {
        return descZh;
    }

    public void setDescZh(String descZh) {
        this.descZh = descZh;
    }

    public String getIconImg() {
        return iconImg;
    }

    public void setIconImg(String iconImg) {
        this.iconImg = iconImg;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public int getTypePid() {
        return typePid;
    }

    public void setTypePid(int typePid) {
        this.typePid = typePid;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpdateTimeStr() {
        return updateTimeStr;
    }

    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }

    @Override
    public String toString() {
        return "NptData{" +
                "name='" + name + '\'' +
                ", sortLetter='" + sortLetter + '\'' +
                ", textureName='" + textureName + '\'' +
                ", lastModified=" + lastModified +
                ", id=" + id +
                ", descEn='" + descEn + '\'' +
                ", descZh='" + descZh + '\'' +
                ", iconImg='" + iconImg + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", nameZh='" + nameZh + '\'' +
                ", typePid=" + typePid +
                ", updateTime=" + updateTime +
                ", updateTimeStr='" + updateTimeStr + '\'' +
                '}';
    }
}
