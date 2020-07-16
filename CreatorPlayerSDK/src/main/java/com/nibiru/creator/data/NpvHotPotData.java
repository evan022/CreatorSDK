package com.nibiru.creator.data;


import android.view.View;

public class NpvHotPotData {
    // 热点显示时间
    private long startTime;
    // 热点消失时间
    private long endTime;
    private View hotpotActor;

    public NpvHotPotData() {

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

    public View getHotpotActor() {
        return hotpotActor;
    }

    public void setHotpotActor(View hotpotActor) {
        this.hotpotActor = hotpotActor;
    }

    @Override
    public String toString() {
        return "NpvHotPotData{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", hotpotActor=" + hotpotActor +
                '}';
    }
}
