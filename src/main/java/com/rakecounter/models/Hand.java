package com.rakecounter.models;

import java.util.List;

public class Hand {
    private List<Card> cards;
    private boolean isSuit;
    private boolean isPocketPair;

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public boolean isSuit() {
        return isSuit;
    }

    public void setSuit(boolean suit) {
        isSuit = suit;
    }

    public boolean isPocketPair() {
        return isPocketPair;
    }

    public void setPocketPair(boolean pocketPair) {
        isPocketPair = pocketPair;
    }
}
