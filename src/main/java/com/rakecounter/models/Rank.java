package com.rakecounter.models;

import java.util.HashMap;
import java.util.Map;

public enum Rank {
    ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX;

    private static final Map<Rank, Integer> RANK_VALUE = new HashMap<>();

    static {
        RANK_VALUE.put(SIX, 6);
        RANK_VALUE.put(SEVEN, 7);
        RANK_VALUE.put(EIGHT, 8);
        RANK_VALUE.put(NINE, 9);
        RANK_VALUE.put(TEN, 10);
        RANK_VALUE.put(JACK, 11);
        RANK_VALUE.put(QUEEN, 12);
        RANK_VALUE.put(KING, 13);
        RANK_VALUE.put(ACE, 14);
    }

    public static int getValueOfRank(Rank rank) {
        return RANK_VALUE.get(rank);
    }
    public static Rank getRankByValue(int value){
        for (Map.Entry<Rank, Integer> entry : RANK_VALUE.entrySet()) {
            if(entry.getValue()==value){
                return entry.getKey();
            }
        }
        throw new NullPointerException();
    }
}

