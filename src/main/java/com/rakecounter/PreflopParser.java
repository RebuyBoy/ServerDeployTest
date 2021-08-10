package com.rakecounter;

import com.rakecounter.models.HandHistory;
import com.rakecounter.models.Preflop;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
@Component
public class PreflopParser {
    private final String flopPointer = "*** FLOP ***";
    private final String flopPointerRIO = "*** FIRST FLOP ***";
    private final String showdownPointer = "*** SHOWDOWN ***";
    private final String showdownPointerRIO = "*** FIRST SHOWDOWN ***";
    private String preflopPointer = "*** HOLE CARDS ***";
    private String flopPointerRegex = "\\*\\*\\*.?(?:FIRST)? FLOP \\*\\*\\*";

    public Preflop parse(HandHistory handHistory) {
        Preflop preflop = new Preflop();
        preflop.setActions(getActions(handHistory.getHandHistory()));
        preflop.setFlopDealt(isFlopDealt(handHistory.getHandHistory()));
        return preflop;
    }

    private String getActions(String hand) {
        int index = 0;
        int index2 = hand.indexOf(flopPointer);
        if (index2 < 0) {
            index2 = hand.indexOf(flopPointerRIO);
        }
        if (index2 < 0) {

            index2 = hand.indexOf(showdownPointer);
        }
        if (index2 < 0) {
            index2 = hand.indexOf(showdownPointerRIO);
        }
        return hand.substring(index, index2);
    }

    private boolean isFlopDealt(String hand) {
        return Pattern.compile(flopPointerRegex).matcher(hand).find();
    }
}
