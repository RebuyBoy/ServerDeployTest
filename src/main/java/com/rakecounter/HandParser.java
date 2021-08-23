package com.rakecounter;

import com.rakecounter.dtos.Sessions;
import com.rakecounter.models.*;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
                if(player.getvPip()==1){
                    int vpipCount = countResult.getvPip()+1;
                    countResult.setvPip(vpipCount);
                }else if(player.getvPip()==-1){
                    int allFoldsCount = countResult.getAllFolds() + 1;
                    countResult.setAllFolds(allFoldsCount);
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
        Map<Stake, Double> sessionsByStake = getHandsPerHourByStake();
        for (Map.Entry<Stake, Double> entry : sessionsByStake.entrySet()) {
            Stake stake = entry.getKey();
            if (results.containsKey(stake)) {
                CountResult countResult = results.get(stake);
                double handsPerHour = entry.getValue();
                countResult.setHandsPerHour(handsPerHour);
            }
        }
        double handsPerHour = getHandsPerHour();
        CountResult countResult = new CountResult();
        countResult.setHandsPerHour(handsPerHour);
        System.out.println(countResult);
        results.put(Stake.TOTAL, countResult);
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

        long date = getDate(handSample);
        handHistory.setTimestamp(date);

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

    //    private String getDate(String hand) {
//        Matcher matcher = Pattern.compile("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}").matcher(hand);
//        String date = "";
//        if (matcher.find()) {
//            date = matcher.group();
//        }
//        return date;
//    }
    private long getDate(String hand) {
        Matcher matcher = Pattern.compile("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}").matcher(hand);
        String date = "";
        if (matcher.find()) {
            date = matcher.group();
        }
        //TODO проверка формата?
        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.from(formatDateTime.parse(date));
        return Timestamp.valueOf(localDateTime).getTime();
    }

    private Map<Stake, Double> getHandsPerHourByStake() {
        handlist = handlist.stream()
                .sorted(Comparator.comparingLong(HandHistory::getTimestamp))
                .collect(Collectors.toList());

        Map<Stake, Sessions> stakeSessionsMap = new HashMap<>();
        for (HandHistory handHistory : handlist) {
            long current = handHistory.getTimestamp();
            Stake stake = handHistory.getStake();
            if (stakeSessionsMap.containsKey(stake)) {
                Sessions sessions = stakeSessionsMap.get(stake);
                Session session = sessions.getSession();
                long last = session.getLast();
                long start = session.getStart();
                if (start < 1) {
                    session.setStart(current);
                    session.setLast(current);
                    session.setHandCount(1);
                    sessions.getSessions().add(session);
                } else if (current - last > 200000) {
                    session.setEnd(last);
                    session.setDuration(last - start);
                    double l = (double) session.getHandCount() * 3600000 / (session.getDuration() + 1);
                    session.setHandsPerHour(l);
                    sessions.getSessions().add(session);
                    session = new Session();
                    sessions.setSession(session);
                } else {
                    session.setLast(current);
                    session.setHandCount(session.getHandCount() + 1);
                }
            } else {
                Sessions sessions = new Sessions();
                Session session = new Session();
                session.setStart(current);
                session.setLast(current);
                session.setHandCount(1);
                sessions.setSession(session);
                sessions.getSessions().add(session);
                stakeSessionsMap.put(stake, sessions);
            }
        }
        Map<Stake, Double> handsPerHourByStake = new HashMap<>();
        for (Map.Entry<Stake, Sessions> entry : stakeSessionsMap.entrySet()) {
            Stake key = entry.getKey();
            Sessions value = entry.getValue();
            if (!handsPerHourByStake.containsKey(key)) {
                List<Session> sessions = value.getSessions();
                double countHPH = 0;
                for (Session session : sessions) {
                    double handsPerHour = session.getHandsPerHour();
                    if (handsPerHour == 0) {
                        long last = session.getLast();
                        long start = session.getStart();
                        long duration = last - start;
                        handsPerHour = (double) session.getHandCount() * 3600000 / (duration + 1);
                    }
                    countHPH += handsPerHour;
                }
                countHPH /= sessions.size();
                handsPerHourByStake.put(key, countHPH);
            }
        }
        return handsPerHourByStake;
    }

    private double getHandsPerHour() {
        handlist = handlist.stream()
                .sorted(Comparator.comparingLong(HandHistory::getTimestamp))
                .collect(Collectors.toList());
        List<Session> sessions = new ArrayList<>();
        Session session = new Session();
        for (HandHistory handHistory : handlist) {
            long current = handHistory.getTimestamp();
            long start = session.getStart();
            long last = session.getLast();
            if (start < 1) {
                session.setStart(current);
                session.setLast(current);
                session.setHandCount(1);
            } else if (last - start > 200000) {
                long end = session.getLast();
                session.setEnd(end);
                long duration = end - start;
                session.setDuration(duration);
                int handCount = session.getHandCount();
                double handsPerHour = (double) handCount * 3600000 / (duration + 1);
                session.setHandsPerHour(handsPerHour);
                sessions.add(session);
                session = new Session();
            } else {
                session.setLast(current);
                int handCount = session.getHandCount() + 1;
                session.setHandCount(handCount);
            }
        }
        double countHPH = 0;
        for (Session session1 : sessions) {
            double handsPerHour = session1.getHandsPerHour();
            if (handsPerHour == 0) {
                long last = session1.getLast();
                long start = session1.getStart();
                long duration = last - start;
                handsPerHour = (double) session1.getHandCount() * 3600000 / (duration + 1);
            }
            countHPH += handsPerHour;
        }
        countHPH /= sessions.size();
        return countHPH;
    }
}


