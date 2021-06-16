package com.rakecounter;

import com.rakecounter.models.Stake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RakeCounter {
    public static final String SPLIT_HANDS_BY_LINE = "Poker Hand ";
    public static final String CURRENCY = "\\$(\\d+\\.?\\d*)";
    private Map<Stake, CountResult> results;

    public RakeCounter() {
        this.results = new HashMap<>();
    }

    public Map<Stake, CountResult> process(List<String> hands) {
        for (String handsPart : hands) {
            String[] split = handsPart.split(SPLIT_HANDS_BY_LINE);
            for (String s : split) {
                count(s);
            }
        }
        return this.results;
    }


    public void count(String hand) {
        int winnersCount = 0;
        double gRakeFromHand = 0;
        double jpRakeFromHand = 0;
        Stake stake = Stake.UNK;
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
            stake = parseHandHistoryStakeLevel(hand);
            if (!results.containsKey(stake)) {
                CountResult countResult = new CountResult();
                addNewResult(gRakeFromHand, jpRakeFromHand, countPlayers, countResult);
                results.put(stake, countResult);
            } else {
                CountResult countResult = results.get(stake);
                addNewResult(gRakeFromHand, jpRakeFromHand, countPlayers, countResult);
            }
        }
    }

    private void addNewResult(double gRakeFromHand, double jpRakeFromHand, int countPlayers, CountResult countResult) {
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

    private Stake parseHandHistoryStakeLevel(String hand) {
        Pattern stakePattern = Pattern.compile("posts the ante " + CURRENCY);
        Matcher stakeMatcher = stakePattern.matcher(hand);
        Stake stake1 = Stake.UNK;
        if (stakeMatcher.find()) {
            String stake = stakeMatcher.group(1);
            if (stake.equals("1")) {
                stake1 = Stake.S_100;
            }
            if (stake.equals("2")) {
                stake1 = Stake.S_200;
            }
            if (stake.equals("0.5")) {
                stake1 = Stake.S_50;
            }
            if (stake.equals("0.25")) {
                stake1 = Stake.S_25;
            }
            if (stake.equals("0.1")) {
                stake1 = Stake.S_10;
            }
        }
        return stake1;
    }
}
