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
}


