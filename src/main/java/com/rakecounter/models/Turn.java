package com.rakecounter.models;

public class Turn {
    private Card card;
    private double totalPot;
    private String  actions;
    private boolean isRiverDealt;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public double getTotalPot() {
        return totalPot;
    }

    public void setTotalPot(double totalPot) {
        this.totalPot = totalPot;
    }

    public boolean isRiverDealt() {
        return isRiverDealt;
    }

    public void setRiverDealt(boolean riverDealt) {
        isRiverDealt = riverDealt;
    }
}
