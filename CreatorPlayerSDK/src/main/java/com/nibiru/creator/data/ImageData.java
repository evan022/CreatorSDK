package com.nibiru.creator.data;

public class ImageData {
    private int id;
    private int type; // 图片类型
    private String imageName;
    private float imageWidth;
    private float imageHeight;

    public ImageData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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
        return "ImageData{" +
                "id=" + id +
                ", type=" + type +
                ", imageName='" + imageName + '\'' +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                '}';
    }
}
