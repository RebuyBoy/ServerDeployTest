package com.rakecounter;

import com.rakecounter.models.*;

import java.util.List;

public class JackpotChecker {

    public boolean process(HandHistory handHistory) {
        Player player = handHistory.getPlayer();
        Hand hand = player.getHand();
        if (hand.isSuit()) {
            boolean isPossible = checkFlushJackPot(handHistory);
            if (isPossible) {
                System.out.println(handHistory.getHandHistory());
            }
        }
        return true;
    }

    private boolean checkFlushJackPot(HandHistory handHistory) {
        Player player = handHistory.getPlayer();
        Stake stake = handHistory.getStake();
        Suit suit = handHistory.getPlayer().getHand().getCards().get(0).getSuit();
        int sameSuitCount = 0;
        double invested = 0;
        if (player.isSawFlop()) {
            List<Card> cards = handHistory.getFlop().getCards();
            sameSuitCount += sameSuit(suit, cards);
            if (sameSuitCount == 3) {
                double ante = player.getAnte();
                double preInvestments = player.getPreInvestments();
                invested = ante + preInvestments;
                if (isInvested(stake, invested)) {
                    return true;
                }
            }
            if (sameSuitCount < 1) {
                return false;
            }
        }
        if (player.isSawTurn()) {
            Card turnCard = handHistory.getTurn().getCard();
            double previousSuitCount = sameSuitCount;
            sameSuitCount += sameSuit(suit, turnCard);
            invested += player.getFlopInvestments();
            if (sameSuitCount > previousSuitCount
                    && sameSuitCount >= 3
                    && isInvested(stake, invested)) {
                return true;
            }
            if (sameSuitCount < 2) {
                return false;
            }

        }
        if (player.isSawRiver()) {
            Card riverCard = handHistory.getRiver().getCard();
            double previousSuitCount = sameSuitCount;
            sameSuitCount += sameSuit(suit, riverCard);
            invested += player.getTurnInvestments();
            if (previousSuitCount < sameSuitCount && sameSuitCount >= 3 && isInvested(stake, invested)) {
                return true;
            }
        }
        return false;

    }

    private int sameSuit(Suit suit, List<Card> flop) {
        if (flop == null) {
            return 0;
        }
        int count = 0;
        for (Card card : flop) {
            if (card.getSuit().equals(suit))
                count++;
        }
        return count;
    }

    private int sameSuit(Suit suit, Card card) {
        if (card == null) {
            return 0;
        }
        if (suit.equals(card.getSuit())) {
            return 1;
        }
        return 0;
    }

    private boolean isInvested(Stake stake, double invested) {
        double anteSize = Stake.getStakeAnte(stake);
        return (invested / anteSize) >= 15;
    }
}
