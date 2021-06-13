package com.rakecounter;

public class CountResult {
    private int numberOfHands;
    private double generalRake;
    private double jackpotRake;

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
                '}';
    }
}
