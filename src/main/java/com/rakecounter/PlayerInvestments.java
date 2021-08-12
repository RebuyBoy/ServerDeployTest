package com.rakecounter;

import com.rakecounter.models.HandHistory;
import com.rakecounter.models.Player;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PlayerInvestments {
    private final String CURRENCY = "\\$(\\d+(?:.\\d+)?)";
    private final String collectedRegex = "Hero collected " + CURRENCY + " from pot";
    private final String collectedAnyRegex = "(?![Hero])\\w+ collected " + CURRENCY + " from pot";
    private final String totalPotRegex = "Total pot " + CURRENCY;
    private final String postBigBlindRegex = "Hero: posts button blind " + CURRENCY;
    private String uncalledBetRegex = "Uncalled bet \\(\\$(\\d+(?:.\\d+)?)\\) returned to Hero";
    private String callsRegex = "Hero: calls \\$(\\d+.?\\d*)";
    private String betsRegex = "Hero: bets \\$(\\d+.?\\d*)";
    private String raisesRegex = "Hero: raises \\$(\\d+(?:.\\d+)?)(?: to \\$(\\d*(?:.\\d+)?))?";
    //ПРОВЕРКА ВЫЧИТАТЬ ИЗ СТАРТОВГО СТЕКА

    public double countInvestments(String actionsSample) {
        if (actionsSample == null || actionsSample.length() == 0) {
            return 0;
        }
        String[] actions = actionsSample.split("\n");
        double calls = 0;
        double raises = 0;
        double bets = 0;
        double uncalled = 0;

        for (String action : actions) {
            if (action.contains("Hero: folds")) {
                break;
            }
            if (action.contains(("Hero: posts button blind"))) {
                Matcher matcher = Pattern.compile(postBigBlindRegex).matcher(action);
                if (matcher.find()) {
                    calls = Double.parseDouble(matcher.group(1));
                }
            }
            if (action.contains("Hero: calls")) {
                Matcher matcher = Pattern.compile(callsRegex).matcher(action);
                if (matcher.find()) {
                    calls += Double.parseDouble(matcher.group(1));
                }
            }
            if (action.contains("Hero: raises")) {
                Matcher matcher = Pattern.compile(raisesRegex).matcher(action);
                if (matcher.find()) {
                    if (matcher.group(2)==null) {
                        raises = Double.parseDouble(matcher.group(1));
                    } else {
                        raises = Double.parseDouble(matcher.group(2));
                        bets = 0;
                        calls = 0;
                    }
                }
            }
            if (action.contains("Hero: bets")) {
                Matcher matcher = Pattern.compile(betsRegex).matcher(action);
                if (matcher.find()) {
                    bets = Double.parseDouble(matcher.group(1));
                }
            }
            if (action.contains("returned to Hero")) {
                Matcher matcher = Pattern.compile(uncalledBetRegex).matcher(action);
                if (matcher.find()) {
                    uncalled = Double.parseDouble(matcher.group(1));
                }
            }
        }
        return calls + raises + bets - uncalled;
    }

    public double countCollected(String hand) {
        Matcher matcher = Pattern.compile(collectedRegex).matcher(hand);
        double collected = 0;
        while (matcher.find()) {
            try {
                collected += Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                System.out.println(hand);
            }
        }
        return collected;
    }

    public double countProfit(HandHistory handHistory) {
        Player player = handHistory.getPlayer();
        double preInvestments = player.getPreInvestments();
        double flopInvestments = player.getFlopInvestments();
        double turnInvestments = player.getTurnInvestments();
        double riverInvestments = player.getRiverInvestments();
        double collected = countCollected(handHistory.getShowdown().getShowdown());
        double ante = player.getAnte();

        return collected - preInvestments - flopInvestments - turnInvestments - riverInvestments - ante;
    }

    public double countGGRake(HandHistory handHistory) {
        double collected = countCollected(handHistory.getShowdown().getShowdown());
        if (collected == 0) {
            return 0;
        }
        double totalPot = handHistory.getTotalPot();
        double ggRake = handHistory.getGgRake();
        double jpRake = handHistory.getJpRake();
        double result = collected / (totalPot - ggRake - jpRake) * ggRake;
        if (Double.isNaN(result)) {
            return 0;
        }
        return result;
    }

    public double countJpRake(HandHistory handHistory) {
        double collected = countCollected(handHistory.getShowdown().getShowdown());
        if (collected == 0) {
            return 0;
        }
        double totalPot = handHistory.getTotalPot();
        double ggRake = handHistory.getGgRake();
        double jpRake = handHistory.getJpRake();
        double result = collected / (totalPot - ggRake - jpRake) * jpRake;
        if (Double.isNaN(result)) {
            return 0;
        }
        return result;
    }
}
