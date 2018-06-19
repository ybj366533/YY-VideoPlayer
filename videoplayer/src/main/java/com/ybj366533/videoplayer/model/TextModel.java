package com.ybj366533.videoplayer.model;

public class TextModel {
    private String logId;
    private Long logTime;
    private Long startTime;
    private Long endTime;

    public void TextModel(String logId, Long logTime, Long startTime, Long endTime) {
        this.logId = logId;
        this.logTime = logTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public Long getLogTime() {
        return logTime;
    }

    public void setLogTime(Long logTime) {
        this.logTime = logTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return this.logId + "#logTime:" + this.logTime + "#startTime:" + this.startTime + "#endTime:" + this.endTime;
    }
}
