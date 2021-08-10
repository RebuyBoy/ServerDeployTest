package com.rakecounter.models;

public class HandHistory {
    private String HandHistory;
    private String handID;
    private String date;
    private Stake stake;
    private int numberOfPlayers;
    private double totalPot;
    private double ggRake;
    private double jpRake;
    private Rio rio;
    private Preflop preflop;
    private Flop flop;
    private Turn turn;
    private River river;
    private Showdown showdown;
//    private Summary Summary;
    private Player player;

    public Turn getTurn() {
        return turn;
    }

    public Preflop getPreflop() {
        return preflop;
    }

    public Showdown getShowdown() {
        return showdown;
    }

    public void setShowdown(Showdown showdown) {
        this.showdown = showdown;
    }

    public void setPreflop(Preflop preflop) {
        this.preflop = preflop;
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    public River getRiver() {
        return river;
    }

    public void setRiver(River river) {
        this.river = river;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalPot() {
        return totalPot;
    }

    public void setTotalPot(double totalPot) {
        this.totalPot = totalPot;
    }

    public Flop getFlop() {
        return flop;
    }

    public void setFlop(Flop flop) {
        this.flop = flop;
    }

    public Rio getRio() {
        return rio;
    }

    public void setRio(Rio rio) {
        this.rio = rio;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getHandID() {
        return handID;
    }

    public void setHandID(String handID) {
        this.handID = handID;
    }

    public Stake getStake() {
        return stake;
    }

    public void setStake(Stake stake) {
        this.stake = stake;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public String getHandHistory() {
        return HandHistory;
    }

    public void setHandHistory(String handHistory) {
        this.HandHistory = handHistory;
    }
}

