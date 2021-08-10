package com.rakecounter;

import com.rakecounter.models.HandHistory;
import com.rakecounter.models.Showdown;
import org.springframework.stereotype.Component;

@Component
public class ShowdownParser {
    private final String showdownPointer = "*** SHOWDOWN ***";
    private final String showdownPointerRIO = "*** FIRST SHOWDOWN ***";

    public Showdown parse(HandHistory handHistory) {
        Showdown showdown = new Showdown();
        showdown.setShowdown(getShowdownActions(handHistory.getHandHistory()));
        return showdown;
    }


    private String getShowdownActions(String hand) {
        int index = hand.indexOf(showdownPointer) + showdownPointer.length() + 1;
        if (index < 0) {
            index = hand.indexOf(showdownPointerRIO);
        }
        return hand.substring(index);
    }
}
