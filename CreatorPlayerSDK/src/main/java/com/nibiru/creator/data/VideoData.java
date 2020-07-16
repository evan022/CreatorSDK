package com.nibiru.creator.data;

public class VideoData {
    // 热点id
    private int id;
    private String dataSource;
    private int videoType;
    private float videoWidth;
    private float videoHeight;
    private int sourceMode;
    private String snapPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public float getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(float videoWidth) {
        this.videoWidth = videoWidth;
    }

    public float getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(float videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getSourceMode() {
        return sourceMode;
    }

    public void setSourceMode(int sourceMode) {
        this.sourceMode = sourceMode;
    }

    public String getSnapPath() {
        return snapPath;
    }

    public void setSnapPath(String snapPath) {
        this.snapPath = snapPath;
    }

    @Override
    public String toString() {
        return "VideoData{" +
                "id=" + id +
                ", dataSource='" + dataSource + '\'' +
                ", videoType=" + videoType +
                ", videoWidth=" + videoWidth +
                ", videoHeight=" + videoHeight +
                ", sourceMode=" + sourceMode +
                ", snapPath='" + snapPath + '\'' +
                '}';
    }
}
