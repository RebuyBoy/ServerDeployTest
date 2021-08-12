//package com.rakecounter;
//
//import com.rakecounter.models.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class JackpotChecker {
//
//    public boolean process(HandHistory handHistory) {
//        Player player = handHistory.getPlayer();
//        Hand hand = player.getHand();
//        if (hand.isSuit()) {
//            boolean isPossible = checkFlushJackPot(handHistory);
//            if (isPossible) {
//                System.out.println(handHistory.getHandHistory());
//            }
//        }
//        if (hand.isPocketPair()) {
//            if(checkQuad(handHistory)){
//                System.out.println(handHistory.getHandHistory());
//            }
//        }
//        return true;
//    }
//
//    private boolean checkFlushJackPot(HandHistory handHistory) {
//        Player player = handHistory.getPlayer();
//        List<Card> flushCards = new ArrayList<>(player.getHand().getCards());
//        //TODO зачем добавлять свои карты в лист?
//        Stake stake = handHistory.getStake();
//        Suit suit = handHistory.getPlayer().getHand().getCards().get(0).getSuit();
//        int flushValue = 0;
//        int sameSuitCount = 0;
//        double invested = 0;
//        if (player.isSawFlop()) {
//            List<Card> cards = handHistory.getFlop().getCards();
//            if (cards == null) {
//                return false;
//            }
//            for (Card card : cards) {
//                if (isSameSuit(suit, card)) {
//                    flushCards.add(card);
//                }
//            }
//            if (flushCards.size() == 5) {
//                double ante = player.getAnte();
//                double preInvestments = player.getPreInvestments();
//                invested = ante + preInvestments;
//                if (isInvested(stake, invested)) {
//                    return true;
//                }
//            }
//            if (flushCards.size() < 3) {
//                return false;
//            }
//        }
//        if (player.isSawTurn()) {
//            Card turnCard = handHistory.getTurn().getCard();
//            int previousSuitCount = flushCards.size();
//            if (isSameSuit(suit, turnCard)) {
//                flushCards.add(turnCard);
//            }
//            invested += player.getFlopInvestments();
//            if (flushCards.size() > previousSuitCount
//                    && flushCards.size() >= 5
//                    && isInvested(stake, invested)) {
//                return true;
//            }
//            if (flushCards.size() < 4) {
//                return false;
//            }
//        }
//        if (player.isSawRiver()) {
//            Card riverCard = handHistory.getRiver().getCard();
//            int previousSuitCount = flushCards.size();
//            if (isSameSuit(suit, riverCard)) {
//                flushCards.add(riverCard);
//            }
//            invested += player.getTurnInvestments();
//            if (flushCards.size() > previousSuitCount
//                    && flushCards.size() >= 5
//                    && isInvested(stake, invested)) {
//                return true;
//            }
//        }
//        return false;
//
//    }
//
//    private boolean isSameSuit(Suit suit, Card card) {
//        if (card == null) {
//            return false;
//        }
//        return suit.equals(card.getSuit());
//    }
//
//    private boolean isInvested(Stake stake, double invested) {
//        double anteSize = Stake.getStakeAnte(stake);
//        return (invested / anteSize) >= 15;
//    }
//
//    private int countValue(List<Card> cards) {
//
//        return 0;
//    }
//
//
//    private boolean checkQuad(HandHistory handHistory) {
//        List<Card> sameCards = new ArrayList<>();
//        Player player = handHistory.getPlayer();
//        Rank rank = player.getHand().getCards().get(0).getRank();
//        Stake stake = handHistory.getStake();
//        double invested = 0;
//        if (player.isSawFlop()) {
//            List<Card> cards = handHistory.getFlop().getCards();
//            if (cards == null) {
//                return false;
//            }
//            for (Card card : cards) {
//                if (isSameRank(card, rank)) {
//                    sameCards.add(card);
//                }
//            }
//            if (sameCards.size() == 2) {
//                double ante = player.getAnte();
//                double preInvestments = player.getPreInvestments();
//                invested = ante + preInvestments;
//                if (isInvested(stake, invested)) {
//                    return true;
//                }
//            }
//        }
//        if (player.isSawTurn()) {
//            Card turnCard = handHistory.getTurn().getCard();
//            if (isSameRank(turnCard, rank)) {
//                sameCards.add(turnCard);
//            }
//            invested += player.getFlopInvestments();
//            if (sameCards.size() == 2
//                    && isInvested(stake, invested)) {
//                return true;
//            }
//            if (sameCards.size() == 0) {
//                return false;
//            }
//        }
//        if (player.isSawRiver()) {
//            Card riverCard = handHistory.getRiver().getCard();
//            if (isSameRank(riverCard, rank)) {
//                sameCards.add(riverCard);
//            }
//            invested += player.getTurnInvestments();
//            if (sameCards.size() == 2
//                    && isInvested(stake, invested)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean isSameRank(Card card, Rank rank) {
//        if (card == null) {
//            return false;
//        }
//        return card.getRank().equals(rank);
//    }
//}
