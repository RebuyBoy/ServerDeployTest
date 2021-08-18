package com.rakecounter.models;

public class Session {
    private long start;
    private long end;
    private long last;
    private long duration;
    private int handCount;
    private int handsPerHour;

    public long getLast() {
        return last;
    }

    public void setLast(long last) {
        this.last = last;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getHandCount() {
        return handCount;
    }

    public void setHandCount(int handCount) {
        this.handCount = handCount;
    }

    public int getHandsPerHour() {
        return handsPerHour;
    }

    public void setHandsPerHour(int handsPerHour) {
        this.handsPerHour = handsPerHour;
    }
}
