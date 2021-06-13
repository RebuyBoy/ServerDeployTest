package com.rakecounter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RakeCounter {
    public static final String SPLIT_HANDS_BY_LINE = "Poker Hand ";
    private CountResult countResult;

    public RakeCounter() {
        this.countResult =new CountResult();
    }

    public CountResult process(List<String> hands) {
        for (String handsPart : hands) {
            String[] split = handsPart.split(SPLIT_HANDS_BY_LINE);
            for (String s : split) {
                count(s);
            }
        }
        System.out.println(countResult.getNumberOfHands());
        return this.countResult;
    }


    public void count(String hand) {
        int winnersCount = 0;
        double gRakeFromHand = 0;
        double jpRakeFromHand = 0;
        if(hand.contains("Dealt to Hero")) {
            if (hand.contains("Hero collected")) {
                String rakeRegex = "Rake .(\\d+.?\\d*)";
                String jackpotRegex = "Jackpot .(\\d+.?\\d*)";
                String collected = "collected";
                Pattern pattern = Pattern.compile(rakeRegex);
                Pattern patternJ = Pattern.compile(jackpotRegex);
                Pattern patternWc = Pattern.compile(collected);
                Matcher matcher = pattern.matcher(hand);
                Matcher matcherJ = patternJ.matcher(hand);
                Matcher matcherWc = patternWc.matcher(hand);
                if (matcher.find()) {
                    String rake = matcher.group(1);
                    double parseDouble = Double.parseDouble(rake);
                    gRakeFromHand += parseDouble;
                }
                if (matcherJ.find()) {
                    String jackpot = matcherJ.group(1);
                    double jackpotD = Double.parseDouble(jackpot);
                    jpRakeFromHand += jackpotD;
                }
                while (matcherWc.find()) {
                    winnersCount++;
                }
                jpRakeFromHand /= winnersCount;
                gRakeFromHand /= winnersCount;
            }
            countResult.setGeneralRake(gRakeFromHand + countResult.getGeneralRake());
            countResult.setJackpotRake(jpRakeFromHand + countResult.getJackpotRake());
            countResult.setNumberOfHands(countResult.getNumberOfHands() + 1);
        }

    }
}
