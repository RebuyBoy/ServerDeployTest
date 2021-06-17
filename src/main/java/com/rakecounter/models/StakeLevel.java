package com.rakecounter.models;

import java.util.HashMap;
import java.util.Map;

public enum StakeLevel {
    A1000(10), A500(5), A200(2), A100(1), A50(0.5), A25(0.25), A10(0.1), A5(0.05);
    private static final Map<StakeLevel, Double> BY_LEVEL = new HashMap<>();

    static {
        for (StakeLevel e : values()) {
            BY_LEVEL.put(e, e.ante);
        }
    }

    private double ante;

    StakeLevel(double ante) {
        this.ante = ante;
    }

    public static double getStakeAnte(StakeLevel level) {
        return BY_LEVEL.get(level);
    }
}

