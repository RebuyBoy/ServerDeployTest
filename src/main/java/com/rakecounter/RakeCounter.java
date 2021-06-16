package com.rakecounter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RakeCounter {
    public static final String SPLIT_HANDS_BY_LINE = "Poker Hand ";
    public static final String CURRENCY = "\\$(\\d+\\.?\\d*)";
    private CountResult countResult;

    public RakeCounter() {
        this.countResult = new CountResult();
    }

    public CountResult process(List<String> hands) {
        for (String handsPart : hands) {
            String[] split = handsPart.split(SPLIT_HANDS_BY_LINE);
            for (String s : split) {
                count(s);
            }
        }
        return this.countResult;
    }


    public void count(String hand) {
        int winnersCount = 0;
        double gRakeFromHand = 0;
        double jpRakeFromHand = 0;
        if (hand.contains("Dealt to Hero")) {
            if (hand.contains("Hero collected")) {
                String rakeRegex = "Rake " + CURRENCY;
                String jackpotRegex = "Jackpot " + CURRENCY;
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
            int countPlayers = countPlayers(hand);
            System.out.println(countPlayers);
            System.out.println(hand);
            if (countPlayers == 2) {
                countResult.setGeneralRakeHU(gRakeFromHand + countResult.getGeneralRakeHU());
                countResult.setJackpotRakeHU(jpRakeFromHand + countResult.getJackpotRakeHU());
                countResult.setNumberOfHandsHU(countResult.getNumberOfHandsHU() + 1);
            } else {
                countResult.setGeneralRake(gRakeFromHand + countResult.getGeneralRake());
                countResult.setJackpotRake(jpRakeFromHand + countResult.getJackpotRake());
                countResult.setNumberOfHands(countResult.getNumberOfHands() + 1);
            }
        }
    }


    private void countProfit(String hand) {
        double profit = 0;
        String betsRegex = "Hero: bets " + CURRENCY;
        String raisesRegex = "Hero: raises " + CURRENCY;
        String callsRegex = "Hero: calls " + CURRENCY;
        String returnedRegex = CURRENCY + "\\) returned to Hero";

        Pattern patternBets = Pattern.compile(betsRegex);
        Pattern patternRaises = Pattern.compile(raisesRegex);
        Pattern patternCalls = Pattern.compile(callsRegex);
        Matcher matcherBets = patternBets.matcher(hand);
        Matcher matcherRaises = patternRaises.matcher(hand);
        Matcher matcherCalls = patternCalls.matcher(hand);

        while (matcherBets.find()) {
            String bets = matcherBets.group(1);
            double parsedBet = Double.parseDouble(bets);
            profit += parsedBet;
        }
        while (matcherRaises.find()) {
            String raises = matcherRaises.group(1);
            double parsedRaise = Double.parseDouble(raises);
            profit += parsedRaise;
        }
        while (matcherCalls.find()) {
            String raises = matcherRaises.group(1);
            double parsedRaise = Double.parseDouble(raises);
            profit += parsedRaise;
        }


    }

    private int countPlayers(String hand) {
        int numberOfPlayers = 0;
        Pattern playerPattern = Pattern.compile("Seat (\\d): (\\w+) \\(\\$(\\d+\\.?\\d*) in chips\\)");
        Matcher playerMatcher = playerPattern.matcher(hand);
        while (playerMatcher.find()) {
            numberOfPlayers++;
        }
        return numberOfPlayers;
    }
}
