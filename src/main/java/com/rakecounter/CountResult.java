package com.rakecounter;

public class CountResult {
    private int numberOfHands;
    private double generalRake;
    private double jackpotRake;
    private int numberOfHandsHU;
    private double generalRakeHU;
    private double jackpotRakeHU;
    private double profit;
    private double profitHu;

    public double getProfitHu() {
        return profitHu;
    }

    public void setProfitHu(double profitHu) {
        this.profitHu = profitHu;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public int getNumberOfHandsHU() {
        return numberOfHandsHU;
    }

    public void setNumberOfHandsHU(int numberOfHandsHU) {
        this.numberOfHandsHU = numberOfHandsHU;
    }

    public double getGeneralRakeHU() {
        return generalRakeHU;
    }

    public void setGeneralRakeHU(double generalRakeHU) {
        this.generalRakeHU = generalRakeHU;
    }

    public double getJackpotRakeHU() {
        return jackpotRakeHU;
    }

    public void setJackpotRakeHU(double jackpotRakeHU) {
        this.jackpotRakeHU = jackpotRakeHU;
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

    @Override
    public String toString() {
        return "CountResult{" +
                "numberOfHands=" + numberOfHands +
                ", generalRake=" + generalRake +
                ", jackpotRake=" + jackpotRake +
                ", numberOfHandsHU=" + numberOfHandsHU +
                ", generalRakeHU=" + generalRakeHU +
                ", jackpotRakeHU=" + jackpotRakeHU +
                '}';
    }
}
