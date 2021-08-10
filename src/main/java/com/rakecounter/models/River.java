package com.rakecounter.models;

public class River {
    private double totalPot;
    private String actions;
    private Card card;

    public double getTotalPot() {
        return totalPot;
    }

    public void setTotalPot(double totalPot) {
        this.totalPot = totalPot;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
