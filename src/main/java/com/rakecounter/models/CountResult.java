package com.rakecounter.models;

public class CountResult {
    private int numberOfHands;
    private double generalRake;
    private double jackpotRake;
    private double profit;
    private double JPCount;
    private double handsPerHour;
    private int vPip;
    private int allFolds;
    private int count;

    public int getAllFolds() {
        return allFolds;
    }

    public void setAllFolds(int allFolds) {
        this.allFolds = allFolds;
    }

    public int getvPip() {
        return vPip;
    }

    public void setvPip(int vPip) {
        this.vPip = vPip;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

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
