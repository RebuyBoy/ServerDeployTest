package com.rakecounter.models;

import java.util.HashMap;
import java.util.Map;

public enum Stake {
    S_1000, S_500, S_200, S_100, S_50, S_25, S_10, S_5, S_2, UNK;

    private static final Map<Stake, Double> BY_LEVEL = new HashMap<>();

    static {
        BY_LEVEL.put(Stake.S_2, 0.02);
        BY_LEVEL.put(Stake.S_5, 0.05);
        BY_LEVEL.put(Stake.S_10, 0.1);
        BY_LEVEL.put(Stake.S_25, 0.25);
        BY_LEVEL.put(Stake.S_50, 0.5);
        BY_LEVEL.put(Stake.S_100, 1.0);
        BY_LEVEL.put(Stake.S_200, 2.0);
        BY_LEVEL.put(Stake.S_500, 5.0);
        BY_LEVEL.put(Stake.S_1000, 10.0);
    }

    public static double getAnteByStake(Stake level) {
        return BY_LEVEL.get(level);
    }

    public static Stake getStakeByAnte(String anteString) {
        Stake stake = Stake.UNK;
        double ante = 0;
        try {
            ante = Double.parseDouble(anteString);
        } catch (NumberFormatException e) {
            return stake;
        }
        for (Map.Entry<Stake, Double> entry : BY_LEVEL.entrySet()) {
            if (entry.getValue() == ante) {
                stake = entry.getKey();
            }
        }
        return stake;
    }
    //TODO exceptions
}
