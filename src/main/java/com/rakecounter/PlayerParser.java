package com.rakecounter;

import com.rakecounter.models.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PlayerParser {
    private final String CURRENCY = "\\$(\\d+(?:.\\d+)?)";
    private final String postBigBlindRegex = "Hero: posts button blind " + CURRENCY;
    private final String buttonSeatNumberRegex = "5-max Seat #(\\w) is the button";
    private final String HeroCardsRegex = "Dealt to Hero \\[(.+)]";


    private final String collectedRegex = "Hero collected " + CURRENCY + " from pot";
    private final String collectedAnyRegex = "(?![Hero])\\w+ collected " + CURRENCY + " from pot";
    private final String totalPotRegex = "Total pot " + CURRENCY;
    private final String stackSizeHeroRegex = "Hero \\(" + CURRENCY + " in chips\\)";

    private CardsMapper cardsMapper;
    private PlayerInvestments playerInvestments;

    public PlayerParser(CardsMapper cardsMapper, PlayerInvestments playerInvestments) {
        this.cardsMapper = cardsMapper;
        this.playerInvestments = playerInvestments;
    }

    public Player parse(HandHistory handHistory) {
        Player player = handHistory.getPlayer();
        String handSample = handHistory.getHandHistory();
        player.setName("Hero");
        player.setPosition(postBigBlind(handSample));

        List<Card> heroCards = getHeroCards(handSample);
        Hand hand = new Hand();
        hand.setCards(heroCards);
        hand.setSuit(heroCardsIsSuit(heroCards));
        hand.setPocketPair(heroCardsIsPocketPair(heroCards));
        player.setHand(hand);

        player.setStackSize(getStackSize(handSample));
        player.setAnte(getAnte(handSample));
        player.setPreInvestments(playerInvestments.countInvestments(handHistory.getPreflop().getActions()));
        player.setFlopInvestments(playerInvestments.countInvestments(handHistory.getFlop().getActions()));
        player.setTurnInvestments(playerInvestments.countInvestments(handHistory.getTurn().getActions()));
        player.setRiverInvestments(playerInvestments.countInvestments(handHistory.getRiver().getActions()));
        player.setGgRake(playerInvestments.countGGRake(handHistory));
        player.setJpRake(playerInvestments.countJpRake(handHistory));
        player.setProfit(playerInvestments.countProfit(handHistory));
        player.setvPip(isVpip(handHistory));

        boolean sawFlop = isFold(handHistory.getPreflop().getActions());
        player.setSawFlop(sawFlop);
        if (sawFlop) {
            boolean sawTurn = isFold(handHistory.getFlop().getActions());
            player.setSawTurn(sawTurn);
            if (sawTurn) {
                player.setSawRiver(isFold(handHistory.getTurn().getActions()));
            }
        }


//TODO builder
        return player;
    }

    //    private Position getPosition(String hand) {
//        Matcher matcher = Pattern.compile(buttonSeatNumberRegex).matcher(hand);
//        int buttonSeatNumber = 0;
//        if (matcher.find()) {
//            buttonSeatNumber = Integer.parseInt(matcher.group(1));
//        }
//
//
//
//    }
    private double getAnte(String hand) {
        Pattern stakePattern = Pattern.compile("posts the ante " + CURRENCY);
        Matcher anteMatcher = stakePattern.matcher(hand);
        double ante = 0;
        if (anteMatcher.find()) {
            ante = Double.parseDouble(anteMatcher.group(1));
        }
        return ante;
    }

    private Position postBigBlind(String hand) {
        //TODO ПОЛНАЯ ШЛЯПА ПЕРЕДЕЛАТЬ НА НОРМАЛЬНЫЕ ПОЗИЦИИ
        Matcher postBigBlindMatcher = Pattern.compile(postBigBlindRegex).matcher(hand);
        double bigBlind = 0;
        if (postBigBlindMatcher.find()) {
            bigBlind = Double.parseDouble(postBigBlindMatcher.group(1));
        }
        if (bigBlind > 0) {
            return Position.BU;
        }
        return Position.UTG;
    }


    private List<Card> getHeroCards(String hand) {
        Matcher matcher = Pattern.compile(HeroCardsRegex).matcher(hand);
        if (matcher.find()) {
            String heroCards = matcher.group(1);
            return cardsMapper.getCards(heroCards);
        }
        throw new IllegalArgumentException("Wrong hero cards source: " + hand);
    }

    private double getStackSize(String hand) {
        Matcher matcher = Pattern.compile(stackSizeHeroRegex).matcher(hand);
        double stackSize = 0;
        if (matcher.find()) {
            String group = matcher.group(1);
            String checked = check(group);
            stackSize = Double.parseDouble(checked);
        }
        return stackSize;
    }

    private boolean heroCardsIsSuit(List<Card> cards) {
        return cards.get(0).getSuit().equals(cards.get(1).getSuit());
    }

    private boolean heroCardsIsPocketPair(List<Card> cards) {
        return cards.get(0).getRank().equals(cards.get(1).getRank());
    }

    private boolean isFold(String hand) {
        //TODO напутано пипец
        if (hand == null || hand.length() < 2) {
            return true;
        }
        return !Pattern.compile("Hero: folds").matcher(hand).find();
    }

    private String check(String group) {
        if (group.contains(",")) {
            return group.replaceAll(",", "");
        }
        return group;
    }

    private boolean allFold(HandHistory handHistory) {
        String actions = handHistory.getPreflop().getActions();
        int numberOfPlayers = handHistory.getNumberOfPlayers();
        int foldsCount = 0;
        Matcher matcher = Pattern.compile("\\w+: folds").matcher(actions);
        if (matcher.find()) {
            foldsCount++;
        }
        return foldsCount == numberOfPlayers - 1;
    }

    private int isVpip(HandHistory handHistory) {
        Position position = handHistory.getPlayer().getPosition();
        double preInvestments = handHistory.getPlayer().getPreInvestments();
        double ante = handHistory.getPlayer().getAnte();
        preInvestments /= ante;
        if (position.equals(Position.BU)) {
            if (allFold(handHistory)) {
                return -1;
            }
            if (preInvestments > 1) {
                return 1;
            }
        } else if (preInvestments > 0) {
            return 1;
        }
        return 0;
    }


}
