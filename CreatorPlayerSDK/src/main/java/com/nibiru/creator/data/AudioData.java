package com.nibiru.creator.data;

public class AudioData {
    private int id;
    private String audioName;
    private int loopTime;

    public AudioData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public int getLoopTime() {
        return loopTime;
    }

    public void setLoopTime(int loopTime) {
        this.loopTime = loopTime;
    }

    @Override
    public String toString() {
        return "AudioData{" +
                "id=" + id +
                ", audioName='" + audioName + '\'' +
                ", loopTime=" + loopTime +
                '}';
    }
}
