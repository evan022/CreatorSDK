package com.nibiru.creator.data;

import java.util.List;

public class ImageListData {
    private int id;
    private List<String> pathList;
    private int type;
    private int imageSum;
    private int autoPlay; // 1 自动播放，0 非自动播放
    private float imageWidth;
    private float imageHeight;

    public ImageListData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getPathList() {
        return pathList;
    }

    public void setPathList(List<String> pathList) {
        this.pathList = pathList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getImageSum() {
        return imageSum;
    }

    public void setImageSum(int imageSum) {
        this.imageSum = imageSum;
    }

    public int getAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(int autoPlay) {
        this.autoPlay = autoPlay;
    }

    public float getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(float imageWidth) {
        this.imageWidth = imageWidth;
    }

    public float getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(float imageHeight) {
        this.imageHeight = imageHeight;
    }

    @Override
    public String toString() {
        return "ImageListData{" +
                "id=" + id +
                ", pathList=" + pathList +
                ", type=" + type +
                ", imageSum=" + imageSum +
                ", autoPlay=" + autoPlay +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                '}';
    }
}
