package com.rakecounter;

import com.rakecounter.models.Card;
import com.rakecounter.models.HandHistory;
import com.rakecounter.models.Turn;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TurnParser {
    private final String turnPointer = "*** TURN ***";
    private final String turnPointerRIO = "*** FIRST TURN ***";
    private final String showdownPointer = "*** SHOWDOWN ***";
    private final String showdownPointerRIO = "*** FIRST SHOWDOWN ***";
    private final String turnCardRegex = "\\*\\*\\*.?(?:FIRST)? TURN \\*\\*\\* \\[.+] \\[(..)]";
    private String riverPointer = "*** RIVER ***";
    private String riverPointerRIO = "*** FIRST RIVER ***";
    private String riverPointerRegex = "\\*\\*\\*.?(?:FIRST)? RIVER \\*\\*\\*";
    private CardsMapper cardsMapper;

    public TurnParser(CardsMapper cardsMapper) {
        this.cardsMapper = cardsMapper;
    }

    public Turn parse(HandHistory handHistory) {
        Turn turn = new Turn();
        if (handHistory.getFlop().isTurnDealt()) {
            String hand = handHistory.getHandHistory();
            turn.setActions(getActions(hand));
            turn.setRiverDealt(isTurnDealt(hand));
            turn.setCard(getTurnCard(hand));
        }
        return turn;
    }

    private String getActions(String hand) {
        int index = hand.indexOf(turnPointer) + turnPointer.length() + 17;
        if (index < 35) {
            index = hand.indexOf(turnPointerRIO) + turnPointerRIO.length() + 17;
        }
        int index2 = hand.indexOf(riverPointer);
        if (index2 < 0) {
            index2 = hand.indexOf(riverPointerRIO);
        }
        if (index2 < 0) {
            index2 = hand.indexOf(showdownPointer);
        }
        if (index2 < 0) {
            index2 = hand.indexOf(showdownPointerRIO);
        }
        return hand.substring(index, index2);
    }

    private boolean isTurnDealt(String hand) {
        return Pattern.compile(riverPointerRegex).matcher(hand).find();
    }

    private Card getTurnCard(String hand) {
        Matcher matcher = Pattern.compile(turnCardRegex).matcher(hand);
        if (matcher.find()) {
            String turnCards = matcher.group(1);
            return cardsMapper.getCard(turnCards);
        }
        throw new IllegalArgumentException("Wrong hero cards source: " + hand);
    }
}
