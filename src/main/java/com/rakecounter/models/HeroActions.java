package com.rakecounter.models;

import java.util.List;

public class HeroActions {
    private List<String> preActions;
    private List<String> flopActions;
    private List<String> turnActions;
    private List<String> riverActions;
    private double profit;
    private double antes;
    private double preInvestments;
    private double flopInvestments;
    private double turnInvestments;
    private double riverInvestments;
    private double collected;
    private double uncalled;

    public double getAntes() {
        return antes;
    }

    public void setAntes(double antes) {
        this.antes = antes;
    }

    public List<String> getPreActions() {
        return preActions;
    }

    public void setPreActions(List<String> preActions) {
        this.preActions = preActions;
    }

    public List<String> getFlopActions() {
        return flopActions;
    }

    public void setFlopActions(List<String> flopActions) {
        this.flopActions = flopActions;
    }

    public List<String> getTurnActions() {
        return turnActions;
    }

    public void setTurnActions(List<String> turnActions) {
        this.turnActions = turnActions;
    }

    public List<String> getRiverActions() {
        return riverActions;
    }

    public void setRiverActions(List<String> riverActions) {
        this.riverActions = riverActions;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
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

    public double getCollected() {
        return collected;
    }

    public void setCollected(double collected) {
        this.collected = collected;
    }

    public double getUncalled() {
        return uncalled;
    }

    public void setUncalled(double uncalled) {
        this.uncalled = uncalled;
    }
}
