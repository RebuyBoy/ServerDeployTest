package com.rakecounter;

import com.rakecounter.models.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HandParser {

    static int count = 0;
    private final String SPLIT_HANDS_BY_LINE = "Poker Hand ";
    private final String CURRENCY = "\\$(\\d+(?:.\\d+)?)";
    private String basicInfoRegex = "#SD(\\d+): ShortDeck No Limit  \\(\\$(\\d+(?:.\\d+)?)\\) - (.+\\d)";
    private List<HandHistory> handlist;
    private PreflopParser preflopParser;
    private FlopParser flopParser;
    private PlayerParser playerParser;
    private TurnParser turnParser;
    private RiverParser riverParser;
    private ShowdownParser showdownParser;

    public HandParser(CardsMapper cardsMapper, PreflopParser preflopParser, FlopParser flopParser, PlayerParser playerParser, TurnParser turnParser, RiverParser riverParser, ShowdownParser showdownParser) {
        this.preflopParser = preflopParser;
        this.flopParser = flopParser;
        this.turnParser = turnParser;
        this.riverParser = riverParser;
        this.showdownParser = showdownParser;
        this.playerParser = playerParser;
        this.handlist = new ArrayList<>();
    }

    public static void main(String[] args) {
        String a = "1,204.04";
        double v = Double.parseDouble(a);
        System.out.println(v);
    }

    public Map<Stake, CountResult> parse(List<String> hands) {
        for (String partOfHands : hands) {
            String[] split = partOfHands.split(SPLIT_HANDS_BY_LINE);
            for (String s : split) {
                if (s.length() > 30) {
                    handlist.add(process(s));
                }
            }
        }
        Map<Stake, CountResult> results = new HashMap<>();
        for (HandHistory handHistory : handlist) {
            Stake stake = handHistory.getStake();
            Player player = handHistory.getPlayer();
            if (results.containsKey(stake)) {
                CountResult countResult = results.get(stake);

                int numberOfHands = countResult.getNumberOfHands();
                countResult.setNumberOfHands(++numberOfHands);

                double profit = player.getProfit();
                double profit1 = countResult.getProfit();
                countResult.setProfit(profit + profit1);

                double ggRake = player.getGgRake();
                double generalRake = countResult.getGeneralRake();
                countResult.setGeneralRake(ggRake + generalRake);

                double jpRake = player.getJpRake();
                double jackpotRake = countResult.getJackpotRake();
                countResult.setJackpotRake(jpRake + jackpotRake);

                if (player.isJackpot()) {
                    double jpCount = countResult.getJPCount();
                    countResult.setJPCount(++jpCount);
                }
            } else {
                CountResult countResult = new CountResult();
                countResult.setNumberOfHands(1);
                countResult.setGeneralRake(player.getGgRake());
                countResult.setJackpotRake(player.getJpRake());
                countResult.setProfit(player.getProfit());
                results.put(stake, countResult);
            }
        }
        handlist.clear();
        return results;
    }


    private HandHistory process(String handSample) {
        HandHistory handHistory = new HandHistory();
        Player player = new Player();

        handHistory.setPlayer(player);
        handHistory.setHandHistory(handSample);
        Matcher matcher = Pattern.compile("Hero.+with Royal Flush").matcher(handSample);
        player.setJackpot(matcher.find());

        String handNumber = getHandNumber(handSample);
        handHistory.setHandID(handNumber);

        Stake stake = parseHandHistoryStakeLevel(handSample);
        handHistory.setStake(stake);

        int playersNumber = countPlayers(handSample);
        handHistory.setNumberOfPlayers(playersNumber);

        String date = getDate(handSample);
        handHistory.setDate(date);

        Preflop preflop = preflopParser.parse(handHistory);
        handHistory.setPreflop(preflop);

        Flop flop = flopParser.parse(handHistory);
        handHistory.setFlop(flop);

        Turn turn = turnParser.parse(handHistory);
        handHistory.setTurn(turn);

        River river = riverParser.parse(handHistory);
        handHistory.setRiver(river);

        Showdown showdown = showdownParser.parse(handHistory);
        handHistory.setShowdown(showdown);

        double totalPot = getTotalPot(handSample);
        handHistory.setTotalPot(totalPot);
        double ggRake = getGGRake(handSample);
        handHistory.setGgRake(ggRake);
        double jpRake = getJPRake(handSample);
        handHistory.setJpRake(jpRake);

        handHistory.setPlayer(playerParser.parse(handHistory));
//        JackpotChecker jackpotChecker = new JackpotChecker();
//        jackpotChecker.process(handHistory);

        //TODO builder ^^^^^
        return handHistory;
    }

    private double getTotalPot(String hand) {
        Matcher matcher = Pattern.compile("Total pot " + CURRENCY).matcher(hand);
        double totalPot = 0;
        if (matcher.find()) {
            String group = matcher.group(1);
            String checked = check(group);
            totalPot = Double.parseDouble(checked);
        }
        return totalPot;
    }

    private String check(String group) {
        if (group.contains(",")) {
            return group.replaceAll(",", "");
        }
        return group;
    }

    private double getGGRake(String hand) {
        Matcher matcher = Pattern.compile("Rake " + CURRENCY).matcher(hand);
        double ggRake = 0;
        if (matcher.find()) {
            String rake = matcher.group(1);
            String checkedRake = check(rake);
            ggRake = Double.parseDouble(checkedRake);
        }
        return ggRake;
    }

    private double getJPRake(String hand) {
        Matcher matcher = Pattern.compile("Jackpot " + CURRENCY).matcher(hand);
        double jpRake = 0;
        if (matcher.find()) {
            String rake = matcher.group(1);
            String checked = check(rake);
            jpRake = Double.parseDouble(checked);
        }
        return jpRake;
    }

    private String getHandNumber(String basicInfo) {
        String number = "";
        Pattern numberStakeDate = Pattern.compile(basicInfoRegex);
        Matcher matcher = numberStakeDate.matcher(basicInfo);
        if (matcher.find()) {
            number = matcher.group(1);
        }
        return number;
    }


    private Stake parseHandHistoryStakeLevel(String hand) {
        Pattern stakePattern = Pattern.compile("posts the ante " + CURRENCY);
        Matcher stakeMatcher = stakePattern.matcher(hand);
        Stake stake = Stake.UNK;
        if (stakeMatcher.find()) {
            String parsedStake = stakeMatcher.group(1);
            stake = Stake.getStakeByAnte(parsedStake);
        }
        return stake;
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

    private String getDate(String hand) {
        Matcher matcher = Pattern.compile("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}").matcher(hand);
        String date = "";
        if (matcher.find()) {
            date = matcher.group();
        }
        return date;
    }


//    public void count2(String hand) {
//        int winnersCount = 0;
//        double gRakeFromHand = 0;
//        double jpRakeFromHand = 0;
//        double profit = 0;
////        Stake stake;
//        if (hand.contains("Dealt to Hero")) {
//            if (hand.contains("Hero collected")) {
//                String rakeRegex = "Rake " + CURRENCY;
//                String jackpotRegex = "Jackpot " + CURRENCY;
//                String collected = "collected";
//                Pattern pattern = Pattern.compile(rakeRegex);
//                Pattern patternJ = Pattern.compile(jackpotRegex);
//                Pattern patternWc = Pattern.compile(collected);
//                Matcher matcher = pattern.matcher(hand);
//                Matcher matcherJ = patternJ.matcher(hand);
//                Matcher matcherWc = patternWc.matcher(hand);
//                if (matcher.find()) {
//                    String rake = matcher.group(1);
//                    double parseDouble = Double.parseDouble(rake);
//                    gRakeFromHand += parseDouble;
//                }
//                if (matcherJ.find()) {
//                    String jackpot = matcherJ.group(1);
//                    double jackpotD = Double.parseDouble(jackpot);
//                    jpRakeFromHand += jackpotD;
//                }
//                while (matcherWc.find()) {
//                    winnersCount++;
//                }
//                jpRakeFromHand /= winnersCount;
//                gRakeFromHand /= winnersCount;
//            }
//            int countPlayers = countPlayers(hand);
//            Stake stake = parseHandHistoryStakeLevel(hand);
//            HeroActions heroActions = getHeroInvestments(hand);
//            double profitHand = heroActions.getProfit();
//            if (!results.containsKey(stake)) {
//                CountResult countResult = new CountResult();
//                addNewResult(gRakeFromHand, jpRakeFromHand, countPlayers, profitHand, countResult);
//                results.put(stake, countResult);
//            } else {
//                CountResult countResult = results.get(stake);
//                addNewResult(gRakeFromHand, jpRakeFromHand, countPlayers, profitHand, countResult);
//            }
//        }
//    }
//
//    private void addNewResult(double gRakeFromHand, double jpRakeFromHand, int countPlayers, double profit, CountResult countResult) {
//        if (countPlayers == 2) {
//            countResult.setGeneralRakeHU(gRakeFromHand + countResult.getGeneralRakeHU());
//            countResult.setJackpotRakeHU(jpRakeFromHand + countResult.getJackpotRakeHU());
//            countResult.setNumberOfHandsHU(countResult.getNumberOfHandsHU() + 1);
//            countResult.setProfitHu(countResult.getProfitHu() + profit);
//        } else {
//            countResult.setGeneralRake(gRakeFromHand + countResult.getGeneralRake());
//            countResult.setJackpotRake(jpRakeFromHand + countResult.getJackpotRake());
//            countResult.setNumberOfHands(countResult.getNumberOfHands() + 1);
//            countResult.setProfit(countResult.getProfit() + profit);
//        }
//    }
//
//
//    private void countProfit(HeroActions heroActions) {
//        double profit = 0;
//        String betsRegex = "Hero: bets " + CURRENCY;
//        String raisesRegex = "Hero: raises " + CURRENCY;
//        String callsRegex = "Hero: calls " + CURRENCY;
//        String returnedRegex = CURRENCY + "\\) returned to Hero";
//
//        Pattern patternBets = Pattern.compile(betsRegex);
//        Pattern patternRaises = Pattern.compile(raisesRegex);
//        Pattern patternCalls = Pattern.compile(callsRegex);
////        Matcher matcherBets = patternBets.matcher(hand);
////        Matcher matcherRaises = patternRaises.matcher(hand);
////        Matcher matcherCalls = patternCalls.matcher(hand);
////
////        while (matcherBets.find()) {
////            String bets = matcherBets.group(1);
////            double parsedBet = Double.parseDouble(bets);
////            profit += parsedBet;
////        }
////        while (matcherRaises.find()) {
////            String raises = matcherRaises.group(1);
////            double parsedRaise = Double.parseDouble(raises);
////            profit += parsedRaise;
////        }
////        while (matcherCalls.find()) {
////            String raises = matcherRaises.group(1);
////            double parsedRaise = Double.parseDouble(raises);
////            profit += parsedRaise;
////        }
//
//
//    }
//
//    private int countPlayers(String hand) {
//        int numberOfPlayers = 0;
//        Pattern playerPattern = Pattern.compile("Seat (\\d): (\\w+) \\(\\$(\\d+\\.?\\d*) in chips\\)");
//        Matcher playerMatcher = playerPattern.matcher(hand);
//        while (playerMatcher.find()) {
//            numberOfPlayers++;
//        }
//        return numberOfPlayers;
//    }
//
//    private Stake parseHandHistoryStakeLevel(String hand) {
//        Pattern stakePattern = Pattern.compile("posts the ante " + CURRENCY);
//        Matcher stakeMatcher = stakePattern.matcher(hand);
//        Stake stake1 = Stake.UNK;
//        if (stakeMatcher.find()) {
//            String stake = stakeMatcher.group(1);
//            if (stake.equals("5")) {
//                stake1 = Stake.S_500;
//            }
//            if (stake.equals("10")) {
//                stake1 = Stake.S_1000;
//            }
//            if (stake.equals("1")) {
//                stake1 = Stake.S_100;
//            }
//            if (stake.equals("2")) {
//                stake1 = Stake.S_200;
//            }
//            if (stake.equals("0.5")) {
//                stake1 = Stake.S_50;
//            }
//            if (stake.equals("0.25")) {
//                stake1 = Stake.S_25;
//            }
//            if (stake.equals("0.1")) {
//                stake1 = Stake.S_10;
//            }
//        }
//        return stake1;
//    }
//
//    //    private double getProfit(String hand) {
////        double invested = 0;
////        double collected = 0;
//////        Pattern betsPattern = Pattern.compile("Hero: bets " + CURRENCY);
//////        Pattern callsPattern = Pattern.compile("Hero: calls " + CURRENCY);
//////        Pattern raisesPattern = Pattern.compile("Hero: raises " + CURRENCY);
//////        Pattern collectedPattern = Pattern.compile("Hero collected " + CURRENCY);
//////        Pattern uncalledPattern = Pattern.compile("Hero collected " + CURRENCY);
//////        Matcher betsMatcher = betsPattern.matcher(hand);
//////        Matcher callsMatcher = callsPattern.matcher(hand);
//////        Matcher raisesMatcher = raisesPattern.matcher(hand);
//////        Matcher collectedMatcher = collectedPattern.matcher(hand);
//////        Matcher uncalledMatcher = collectedPattern.matcher(hand);
////
////    }
//    private HeroActions getHeroInvestments(String hand) {
//        String preflopActions = getPreflopActions(hand);
//        String flopActions = getFlopActions(hand);
//        String turnActions = getTurnActions(hand);
//        String riverActions = getRiverActions(hand);
//        HeroActions heroActions = new HeroActions();
//        double antes = 0;
//        Pattern antePattern = Pattern.compile("Hero: posts the ante " + CURRENCY);
//        Matcher matcher = antePattern.matcher(hand);
//        if (matcher.find()) {
//            double v = Double.parseDouble(matcher.group(1));
//            antes += v;
//        }
//        Pattern anteBuPattern = Pattern.compile("Hero: posts button blind " + CURRENCY);
//        Matcher matcherBU = anteBuPattern.matcher(hand);
//        if (matcherBU.find()) {
//            double v = Double.parseDouble(matcherBU.group(1));
//            antes += v;
//        }
//        heroActions.setUncalled(getUncalled(hand));
//        heroActions.setCollected(getCollected(hand));
//        heroActions.setAntes(antes);
//        heroActions.setPreActions(Arrays.asList(preflopActions.split("\n")));
//        heroActions.setFlopActions(Arrays.asList(flopActions.split("\n")));
//        heroActions.setTurnActions(Arrays.asList(turnActions.split("\n")));
//        heroActions.setRiverActions(Arrays.asList(riverActions.split("\n")));
//        setInvestments(heroActions);
//        heroActions.setProfit(getProfit(heroActions));
//
//
//        return heroActions;
//    }
//
//    private double getProfit(HeroActions heroActions) {
//        double antes = heroActions.getAntes();
//        double collected = heroActions.getCollected();
//        double preInvestments = heroActions.getPreInvestments();
//        double flopInvestments = heroActions.getFlopInvestments();
//        double turnInvestments = heroActions.getTurnInvestments();
//        double riverInvestments = heroActions.getRiverInvestments();
//        double uncalled = heroActions.getUncalled();
//        return collected - antes - preInvestments - flopInvestments - turnInvestments - riverInvestments - uncalled;
//    }
//
//    private double getCollected(String hand) {
//        double collected = 0;
//        Pattern collectedPattern = Pattern.compile("Hero collected " + CURRENCY);
//        Matcher matcher = collectedPattern.matcher(hand);
//        while (matcher.find()) {
//            double v = Double.parseDouble(matcher.group(1));
//            collected += v;
//        }
//        return collected;
//    }
//
//    private double getUncalled(String hand) {
//        double uncalled = 0;
//        Pattern uncalledPattern = Pattern.compile("Uncalled bet \\(" + CURRENCY + "\\) returned to Hero");
//        Matcher matcher = uncalledPattern.matcher(hand);
//        if (matcher.find()) {
//            double v = Double.parseDouble(matcher.group(1));
//            uncalled += v;
//        }
//        return uncalled;
//    }
//
//    private String getPreflopActions(String hand) {
//        String preflopActions = "";
//        if (hand.contains(flopPointer)) {
//            preflopActions = hand.substring(hand.indexOf(preflopPointer), hand.indexOf(flopPointer));
//        } else {
//            preflopActions = hand.substring(hand.indexOf(preflopPointer), hand.indexOf(showdownPointer));
//        }
//        return preflopActions;
//    }
//
//    private String getFlopActions(String hand) {
//        String preflopActions = "";
//        if (hand.contains(turnPointer)) {
//            preflopActions = hand.substring(hand.indexOf(flopPointer), hand.indexOf(turnPointer));
//        } else if (hand.contains(flopPointer)) {
//            preflopActions = hand.substring(hand.indexOf(flopPointer), hand.indexOf(showdownPointer));
//        }
//        return preflopActions;
//    }
//
//    private String getTurnActions(String hand) {
//        String preflopActions = "";
//        if (hand.contains(riverPointer)) {
//            preflopActions = hand.substring(hand.indexOf(turnPointer), hand.indexOf(riverPointer));
//        } else if (hand.contains(turnPointer)) {
//            preflopActions = hand.substring(hand.indexOf(turnPointer), hand.indexOf(showdownPointer));
//        }
//        return preflopActions;
//    }
//
//    private String getRiverActions(String hand) {
//        String preflopActions = "";
//        if (hand.contains(riverPointer)) {
//            preflopActions = hand.substring(hand.indexOf(riverPointer), hand.indexOf(showdownPointer));
//        }
//        return preflopActions;
//    }
//
//    private HeroActions setInvestments(HeroActions heroActions) {
//        List<String> preActions = heroActions.getPreActions();
//        List<String> flopActions = heroActions.getFlopActions();
//        List<String> turnActions = heroActions.getTurnActions();
//        List<String> riverActions = heroActions.getRiverActions();
//        heroActions.setPreInvestments(getInvestments(preActions, heroActions));
//        heroActions.setFlopInvestments(getInvestments(flopActions, heroActions));
//        heroActions.setTurnInvestments(getInvestments(turnActions, heroActions));
//        heroActions.setRiverInvestments(getInvestments(riverActions, heroActions));
//        return heroActions;
//    }
//
//    private double getInvestments(List<String> actions, HeroActions heroActions) {
//        double investment = 0;
//        Collections.reverse(actions);
//        for (String action : actions) {
//            Pattern pattern = Pattern.compile("Hero: (?:raises|calls|bets) " + CURRENCY + "(?: to " + CURRENCY + ")?");
//            Matcher matcher = pattern.matcher(action);
//            if (matcher.find()) {
//                if (matcher.group(2) == null) {
//                    investment = Double.parseDouble(matcher.group(1));
//                    double investment2 = Double.parseDouble(matcher.group(1));
//                    if (heroActions.getUncalled() == investment) {
//                        heroActions.setUncalled(0);
//                        investment = 0;
//                    }
//                } else {
//                    investment = Double.parseDouble(matcher.group(2));
//                    double investmentC = Double.parseDouble(matcher.group(1));
//                    if (heroActions.getUncalled() == investmentC) {
//                        heroActions.setUncalled(0);
//                        investment = 0;
//                    }
//                }
//            }
//        }
//        return investment;
//    }
//    private boolean checkFlush(HandHistory handHistory) {
//        //ввести model Hand    cardsList  category isSuit isPocketPair
//        List<Card> cards = handHistory.getPlayer().getCards();
//        if (cards.get(0).getSuit() == cards.get(1).getSuit()) {
//            Suit suit = cards.get(0).getSuit();
//            Flop flop = handHistory.getFlop();
//            if (flop != null) {
//                int count = 0;
//                List<Card> cards1 = flop.getCards();
//                for (Card card : cards1) {
//                    if (card.getSuit().equals(suit)) {
//                        count++;
//                    }
//                }
//                if (count == 3) {
//                    return true;
//                } else if (count > 0) {
//                    Turn turn = handHistory.getTurn();
//                    if (turn != null) {
//                        if (turn.getCard().getSuit().equals(suit)) {
//                            count++;
//                        }
//                    }
//                    if (count == 3) {
//                        return true;
//                    } else if (count == 2) {
//                        River river = handHistory.getRiver();
//                        if (river != null) {
//                            if (river.getCard().getSuit().equals(suit)) {
//                                count++;
//                            }
//                        }
//                        return count == 3;
//                    }
//                }
//            }
//        }
//        return false;
//    }
}


