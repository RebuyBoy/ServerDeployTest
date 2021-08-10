package com.rakecounter.models;

public class Preflop {
    //    private double totalPot;
    private boolean isFlopDealt;
    private String actions;


    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }


    public boolean isFlopDealt() {
        return isFlopDealt;
    }

    public void setFlopDealt(boolean flopDealt) {
        isFlopDealt = flopDealt;
    }
}
