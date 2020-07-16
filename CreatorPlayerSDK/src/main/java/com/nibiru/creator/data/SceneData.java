package com.nibiru.creator.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.nibiru.creator.data.HotPotData.SceneRenderMode;

public class SceneData implements Parcelable {
    private int sceneId = -1;
    private String sceneName;
    private String fileName;
    private float majorViewAngle; // 主视角水平方向转动角度
    private int sceneType;
    private boolean primaryScene;
    private int renderMode = SceneRenderMode.SceneType_VR_VALUE;
    private String fileUrl;

    private long startTime;
    private long endTime;

    public SceneData() {

    }

    protected SceneData(Parcel in) {
        sceneId = in.readInt();
        sceneName = in.readString();
        fileName = in.readString();
        majorViewAngle = in.readFloat();
        sceneType = in.readInt();
        primaryScene = in.readByte() != 0;
        renderMode = in.readInt();
        fileUrl = in.readString();
        startTime = in.readLong();
        endTime = in.readLong();
    }

    public static final Creator<SceneData> CREATOR = new Creator<SceneData>() {
        @Override
        public SceneData createFromParcel(Parcel in) {
            return new SceneData(in);
        }

        @Override
        public SceneData[] newArray(int size) {
            return new SceneData[size];
        }
    };

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public float getMajorViewAngle() {
        return majorViewAngle;
    }

    public void setMajorViewAngle(float majorViewAngle) {
        this.majorViewAngle = majorViewAngle;
    }

    public int getSceneType() {
        return sceneType;
    }

    public void setSceneType(int sceneType) {
        this.sceneType = sceneType;
    }

    public boolean isPrimaryScene() {
        return primaryScene;
    }

    public int getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(int renderMode) {
        this.renderMode = renderMode;
    }

    public void setPrimaryScene(boolean primaryScene) {
        this.primaryScene = primaryScene;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "SceneData{" +
                "sceneId=" + sceneId +
                ", sceneName='" + sceneName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", majorViewAngle=" + majorViewAngle +
                ", sceneType=" + sceneType +
                ", primaryScene=" + primaryScene +
                ", renderMode=" + renderMode +
                ", fileUrl='" + fileUrl + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sceneId);
        dest.writeString(sceneName);
        dest.writeString(fileName);
        dest.writeFloat(majorViewAngle);
        dest.writeInt(sceneType);
        dest.writeByte((byte) (primaryScene ? 1 : 0));
        dest.writeInt(renderMode);
        dest.writeString(fileUrl);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
    }

}
