package com.rakecounter;

import com.rakecounter.models.Card;
import com.rakecounter.models.Flop;
import com.rakecounter.models.HandHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FlopParser {
    private final String flopPointer = "*** FLOP ***";
    private final String flopPointerRIO = "*** FIRST FLOP ***";
    private final String showdownPointer = "*** SHOWDOWN ***";
    private final String showdownPointerRIO = "*** FIRST SHOWDOWN ***";
    private final String flopCardsRegex = "\\*\\*\\*.?(?:FIRST)? FLOP \\*\\*\\* \\[(.+)]";
    private String turnPointer = "*** TURN ***";
    private String turnPointerRIO = "*** FIRST TURN ***";
    private String turnPointerRegex = "\\*\\*\\*.?(?:FIRST)? TURN \\*\\*\\*";
    private CardsMapper cardsMapper;

    public FlopParser(CardsMapper cardsMapper) {
        this.cardsMapper = cardsMapper;
    }

    public Flop parse(HandHistory handHistory) {
        Flop flop = new Flop();
        if (handHistory.getPreflop().isFlopDealt()) {
            String hand = handHistory.getHandHistory();
            flop.setActions(getActions(hand));
            flop.setTurnDealt(isTurnDealt(hand));
            flop.setCards(getFlopCards(hand));
        }
        return flop;
    }

    private String getActions(String hand) {
        int index = hand.indexOf(flopPointer) + flopPointer.length() + 12;
        if (index < 24) {
            index = hand.indexOf(flopPointerRIO) + flopPointerRIO.length() + 12;
        }
        int index2 = hand.indexOf(turnPointer);
        if (index2 < 0) {
            index2 = hand.indexOf(turnPointerRIO);
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
        return Pattern.compile(turnPointerRegex).matcher(hand).find();
    }

    private List<Card> getFlopCards(String hand) {
        Matcher matcher = Pattern.compile(flopCardsRegex).matcher(hand);
        if (matcher.find()) {
            String flopCards = matcher.group(1);
            return cardsMapper.getCards(flopCards);
        }
        throw new IllegalArgumentException("Wrong hero cards source: " + hand);
    }
}
