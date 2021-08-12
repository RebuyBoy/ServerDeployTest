package com.rakecounter.models;

import java.util.List;

public class Player {
    private String name;
    private Position position;
    private Hand hand;
    private double stackSize;
    private double BU;
    private double ante;

    private double preInvestments;
    private boolean isSawFlop;
    //TODO DO REAL IS SAW FLOP, NOW ITS WORK HOW isFlopDealt. Same turn, river.
    private double flopInvestments;
    private boolean isSawTurn;
    private double turnInvestments;
    private boolean isSawRiver;
    private double riverInvestments;

    private double profit;
    private double ggRake;
    private double jpRake;
    private boolean isJackpot;

    public boolean isJackpot() {
        return isJackpot;
    }

    public void setJackpot(boolean jackpot) {
        isJackpot = jackpot;
    }

    public double getBU() {
        return BU;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public boolean isSawFlop() {
        return isSawFlop;
    }

    public void setSawFlop(boolean sawFlop) {
        isSawFlop = sawFlop;
    }

    public boolean isSawTurn() {
        return isSawTurn;
    }

    public void setSawTurn(boolean sawTurn) {
        isSawTurn = sawTurn;
    }

    public boolean isSawRiver() {
        return isSawRiver;
    }

    public void setSawRiver(boolean sawRiver) {
        isSawRiver = sawRiver;
    }

    public void setBU(double BU) {
        this.BU = BU;
    }

    public double getAnte() {
        return ante;
    }

    public void setAnte(double ante) {
        this.ante = ante;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public double getStackSize() {
        return stackSize;
    }

    public void setStackSize(double stackSize) {
        this.stackSize = stackSize;
    }

    public double getPreInvestments() {
        return preInvestments;
    }

    public void setPreInvestments(double preInvestments) {
        this.preInvestments = preInvestments;
    }

    public double getFlopInvestments() {
        return flopInvestments;
    }

    public void setFlopInvestments(double flopInvestments) {
        this.flopInvestments = flopInvestments;
    }

    public double getTurnInvestments() {
        return turnInvestments;
    }

    public void setTurnInvestments(double turnInvestments) {
        this.turnInvestments = turnInvestments;
    }

    public double getRiverInvestments() {
        return riverInvestments;
    }

    public void setRiverInvestments(double riverInvestments) {
        this.riverInvestments = riverInvestments;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getGgRake() {
        return ggRake;
    }

    public void setGgRake(double ggRake) {
        this.ggRake = ggRake;
    }

    public double getJpRake() {
        return jpRake;
    }

    public void setJpRake(double jpRake) {
        this.jpRake = jpRake;
    }
}
