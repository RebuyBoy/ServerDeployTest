package com.rakecounter;

import com.rakecounter.models.HeroActions;
import com.rakecounter.models.Stake;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RakeCounter {
    public static final String SPLIT_HANDS_BY_LINE = "Poker Hand ";
    public static final String CURRENCY = "\\$(\\d+\\.?\\d*)";
    private String preflopPointer = "*** HOLE CARDS ***";
    private String flopPointer = "*** FLOP ***";
    private String turnPointer = "*** TURN ***";
    private String riverPointer = "*** RIVER ***";
    private String showdownPointer = "SHOWDOWN ***";
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
        double profit = 0;
//        Stake stake;
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
            Stake stake = parseHandHistoryStakeLevel(hand);
            HeroActions heroActions = getHeroInvestments(hand);
            double profitHand = heroActions.getProfit();
            if (!results.containsKey(stake)) {
                CountResult countResult = new CountResult();
                addNewResult(gRakeFromHand, jpRakeFromHand, countPlayers, profitHand, countResult);
                results.put(stake, countResult);
            } else {
                CountResult countResult = results.get(stake);
                addNewResult(gRakeFromHand, jpRakeFromHand, countPlayers, profitHand, countResult);
            }
        }
    }

    private void addNewResult(double gRakeFromHand, double jpRakeFromHand, int countPlayers, double profit, CountResult countResult) {
        if (countPlayers == 2) {
            countResult.setGeneralRakeHU(gRakeFromHand + countResult.getGeneralRakeHU());
            countResult.setJackpotRakeHU(jpRakeFromHand + countResult.getJackpotRakeHU());
            countResult.setNumberOfHandsHU(countResult.getNumberOfHandsHU() + 1);
            countResult.setProfitHu(countResult.getProfitHu() + profit);
        } else {
            countResult.setGeneralRake(gRakeFromHand + countResult.getGeneralRake());
            countResult.setJackpotRake(jpRakeFromHand + countResult.getJackpotRake());
            countResult.setNumberOfHands(countResult.getNumberOfHands() + 1);
            countResult.setProfit(countResult.getProfit() + profit);
        }
    }


    private void countProfit(HeroActions heroActions) {
        double profit = 0;
        String betsRegex = "Hero: bets " + CURRENCY;
        String raisesRegex = "Hero: raises " + CURRENCY;
        String callsRegex = "Hero: calls " + CURRENCY;
        String returnedRegex = CURRENCY + "\\) returned to Hero";

        Pattern patternBets = Pattern.compile(betsRegex);
        Pattern patternRaises = Pattern.compile(raisesRegex);
        Pattern patternCalls = Pattern.compile(callsRegex);
//        Matcher matcherBets = patternBets.matcher(hand);
//        Matcher matcherRaises = patternRaises.matcher(hand);
//        Matcher matcherCalls = patternCalls.matcher(hand);
//
//        while (matcherBets.find()) {
//            String bets = matcherBets.group(1);
//            double parsedBet = Double.parseDouble(bets);
//            profit += parsedBet;
//        }
//        while (matcherRaises.find()) {
//            String raises = matcherRaises.group(1);
//            double parsedRaise = Double.parseDouble(raises);
//            profit += parsedRaise;
//        }
//        while (matcherCalls.find()) {
//            String raises = matcherRaises.group(1);
//            double parsedRaise = Double.parseDouble(raises);
//            profit += parsedRaise;
//        }


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
            if (stake.equals("5")) {
                stake1 = Stake.S_500;
            }
            if (stake.equals("10")) {
                stake1 = Stake.S_1000;
            }
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

    //    private double getProfit(String hand) {
//        double invested = 0;
//        double collected = 0;
////        Pattern betsPattern = Pattern.compile("Hero: bets " + CURRENCY);
////        Pattern callsPattern = Pattern.compile("Hero: calls " + CURRENCY);
////        Pattern raisesPattern = Pattern.compile("Hero: raises " + CURRENCY);
////        Pattern collectedPattern = Pattern.compile("Hero collected " + CURRENCY);
////        Pattern uncalledPattern = Pattern.compile("Hero collected " + CURRENCY);
////        Matcher betsMatcher = betsPattern.matcher(hand);
////        Matcher callsMatcher = callsPattern.matcher(hand);
////        Matcher raisesMatcher = raisesPattern.matcher(hand);
////        Matcher collectedMatcher = collectedPattern.matcher(hand);
////        Matcher uncalledMatcher = collectedPattern.matcher(hand);
//
//    }
    private HeroActions getHeroInvestments(String hand) {
        String preflopActions = getPreflopActions(hand);
        String flopActions = getFlopActions(hand);
        String turnActions = getTurnActions(hand);
        String riverActions = getRiverActions(hand);
        HeroActions heroActions = new HeroActions();
        double antes = 0;
        Pattern antePattern = Pattern.compile("Hero: posts the ante " + CURRENCY);
        Matcher matcher = antePattern.matcher(hand);
        if (matcher.find()) {
            double v = Double.parseDouble(matcher.group(1));
            antes += v;
        }
        Pattern anteBuPattern = Pattern.compile("Hero: posts button blind " + CURRENCY);
        Matcher matcherBU = anteBuPattern.matcher(hand);
        if (matcherBU.find()) {
            double v = Double.parseDouble(matcherBU.group(1));
            antes += v;
        }
        heroActions.setUncalled(getUncalled(hand));
        heroActions.setCollected(getCollected(hand));
        heroActions.setAntes(antes);
        heroActions.setPreActions(Arrays.asList(preflopActions.split("\n")));
        heroActions.setFlopActions(Arrays.asList(flopActions.split("\n")));
        heroActions.setTurnActions(Arrays.asList(turnActions.split("\n")));
        heroActions.setRiverActions(Arrays.asList(riverActions.split("\n")));
        setInvestments(heroActions);
        heroActions.setProfit(getProfit(heroActions));


        return heroActions;
    }

    private double getProfit(HeroActions heroActions) {
        double antes = heroActions.getAntes();
        double collected = heroActions.getCollected();
        double preInvestments = heroActions.getPreInvestments();
        double flopInvestments = heroActions.getFlopInvestments();
        double turnInvestments = heroActions.getTurnInvestments();
        double riverInvestments = heroActions.getRiverInvestments();
        double uncalled = heroActions.getUncalled();
        return collected - antes - preInvestments - flopInvestments - turnInvestments - riverInvestments - uncalled;
    }

    private double getCollected(String hand) {
        double collected = 0;
        Pattern collectedPattern = Pattern.compile("Hero collected " + CURRENCY);
        Matcher matcher = collectedPattern.matcher(hand);
        while (matcher.find()) {
            double v = Double.parseDouble(matcher.group(1));
            collected += v;
        }
        return collected;
    }

    private double getUncalled(String hand) {
        double uncalled = 0;
        Pattern uncalledPattern = Pattern.compile("Uncalled bet \\(" + CURRENCY + "\\) returned to Hero");
        Matcher matcher = uncalledPattern.matcher(hand);
        if (matcher.find()) {
            double v = Double.parseDouble(matcher.group(1));
            uncalled += v;
        }
        return uncalled;
    }

    private String getPreflopActions(String hand) {
        String preflopActions = "";
        if (hand.contains(flopPointer)) {
            preflopActions = hand.substring(hand.indexOf(preflopPointer), hand.indexOf(flopPointer));
        } else {
            preflopActions = hand.substring(hand.indexOf(preflopPointer), hand.indexOf(showdownPointer));
        }
        return preflopActions;
    }

    private String getFlopActions(String hand) {
        String preflopActions = "";
        if (hand.contains(turnPointer)) {
            preflopActions = hand.substring(hand.indexOf(flopPointer), hand.indexOf(turnPointer));
        } else if (hand.contains(flopPointer)) {
            preflopActions = hand.substring(hand.indexOf(flopPointer), hand.indexOf(showdownPointer));
        }
        return preflopActions;
    }

    private String getTurnActions(String hand) {
        String preflopActions = "";
        if (hand.contains(riverPointer)) {
            preflopActions = hand.substring(hand.indexOf(turnPointer), hand.indexOf(riverPointer));
        } else if (hand.contains(turnPointer)) {
            preflopActions = hand.substring(hand.indexOf(turnPointer), hand.indexOf(showdownPointer));
        }
        return preflopActions;
    }

    private String getRiverActions(String hand) {
        String preflopActions = "";
        if (hand.contains(riverPointer)) {
            preflopActions = hand.substring(hand.indexOf(riverPointer), hand.indexOf(showdownPointer));
        }
        return preflopActions;
    }

    private HeroActions setInvestments(HeroActions heroActions) {
        List<String> preActions = heroActions.getPreActions();
        List<String> flopActions = heroActions.getFlopActions();
        List<String> turnActions = heroActions.getTurnActions();
        List<String> riverActions = heroActions.getRiverActions();
        heroActions.setPreInvestments(getInvestments(preActions, heroActions));
        heroActions.setFlopInvestments(getInvestments(flopActions, heroActions));
        heroActions.setTurnInvestments(getInvestments(turnActions, heroActions));
        heroActions.setRiverInvestments(getInvestments(riverActions, heroActions));
        return heroActions;
    }

    private double getInvestments(List<String> actions, HeroActions heroActions) {
        double investment = 0;
        Collections.reverse(actions);
        for (String action : actions) {
            Pattern pattern = Pattern.compile("Hero: (?:raises|calls|bets) " + CURRENCY + "(?: to " + CURRENCY + ")?");
            Matcher matcher = pattern.matcher(action);
            if (matcher.find()) {
                if (matcher.group(2) == null) {
                    investment = Double.parseDouble(matcher.group(1));
                    double investment2 = Double.parseDouble(matcher.group(1));
                    if (heroActions.getUncalled() == investment) {
                        heroActions.setUncalled(0);
                        investment = 0;
                    }
                } else {
                    investment = Double.parseDouble(matcher.group(2));
                    double investmentC = Double.parseDouble(matcher.group(1));
                    if (heroActions.getUncalled() == investmentC) {
                        heroActions.setUncalled(0);
                        investment = 0;
                    }
                }
            }
        }
        return investment;
    }
}
