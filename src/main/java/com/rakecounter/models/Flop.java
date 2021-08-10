package com.rakecounter.models;

import java.util.List;

public class Flop {
    private double potSize;
    private List<Card> cards;
    private String actions;
    private boolean isTurnDealt;


    private List<Player> players;


    private void init(String hand){

    }


    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public boolean isTurnDealt() {
        return isTurnDealt;
    }

    public void setTurnDealt(boolean turnDealt) {
        isTurnDealt = turnDealt;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public double getPotSize() {
        return potSize;
    }

    public void setPotSize(double potSize) {
        this.potSize = potSize;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
