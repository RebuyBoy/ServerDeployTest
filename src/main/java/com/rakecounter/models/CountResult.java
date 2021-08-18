package com.rakecounter.models;

import java.util.ArrayList;
import java.util.List;

public class CountResult {
    private int numberOfHands;
    private double generalRake;
    private double jackpotRake;
    private double profit;
    private double JPCount;
    private double handsPerHour;

    public double getHandsPerHour() {
        return handsPerHour;
    }

    public void setHandsPerHour(double handsPerHour) {
        this.handsPerHour = handsPerHour;
    }

    public double getJPCount() {
        return JPCount;
    }

    public void setJPCount(double JPCount) {
        this.JPCount = JPCount;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public int getNumberOfHands() {
        return numberOfHands;
    }

    public void setNumberOfHands(int numberOfHands) {
        this.numberOfHands = numberOfHands;
    }

    public double getGeneralRake() {
        return generalRake;
    }

    public void setGeneralRake(double generalRake) {
        this.generalRake = generalRake;
    }

    public double getJackpotRake() {
        return jackpotRake;
    }

    public void setJackpotRake(double jackpotRake) {
        this.jackpotRake = jackpotRake;
    }

}
