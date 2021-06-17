package com.rakecounter.models;

import java.util.HashMap;
import java.util.Map;

public enum Stake {
    S_1000(10), S_500(5), S_200(2), S_100(1), S_50(0.5), S_25(0.25), S_10(0.1), S_5(0.05), UNK(0);

    private static final Map<Stake, Double> BY_LEVEL = new HashMap<>();

    static {
        for (Stake e : values()) {
            BY_LEVEL.put(e, e.ante);
        }
    }

    private double ante;

    Stake(double ante) {
        this.ante = ante;
    }

    public static double getStakeAnte(Stake level) {
        return BY_LEVEL.get(level);
    }
}
