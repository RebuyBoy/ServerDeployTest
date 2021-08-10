package com.rakecounter;

import com.rakecounter.models.Card;
import com.rakecounter.models.HandHistory;
import com.rakecounter.models.River;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RiverParser {
    private final String riverCardRegex = "\\*\\*\\*.?(?:FIRST)? RIVER \\*\\*\\* \\[.+] \\[(..)]";
    private final String showdownPointer = "*** SHOWDOWN ***";
    private final String showdownPointerRIO = "*** FIRST SHOWDOWN ***";
    private String riverPointer = "*** RIVER ***";
    private String riverPointerRIO = "*** FIRST RIVER ***";
    private String riverPointerRegex = "\\*\\*\\*.?(?:FIRST)? RIVER \\*\\*\\*";
    private CardsMapper cardsMapper;

    public RiverParser(CardsMapper cardsMapper) {
        this.cardsMapper = cardsMapper;
    }

    public River parse(HandHistory handHistory) {
        River river = new River();
        if (handHistory.getTurn().isRiverDealt()) {
            String hand = handHistory.getHandHistory();
            river.setActions(getActions(hand));
            river.setCard(getRiverCard(hand));
        }
        return river;
    }

    private String getActions(String hand) {
        int index = hand.indexOf(riverPointer) + riverPointer.length() + 19;
        if (index < 32) {
            index = hand.indexOf(riverPointerRIO) + riverPointerRIO.length() + 19;
        }
        int index2 = hand.indexOf(showdownPointer);
        if (index2 < 0) {
            index2 = hand.indexOf(showdownPointerRIO);
        }
        return hand.substring(index, index2);
    }

    private Card getRiverCard(String hand) {
        Matcher matcher = Pattern.compile(riverCardRegex).matcher(hand);
        if (matcher.find()) {
            String turnCards = matcher.group(1);
            return cardsMapper.getCard(turnCards);
        }
        throw new IllegalArgumentException("Wrong hero cards source: " + hand);
    }
//TODO is showdown?

}
