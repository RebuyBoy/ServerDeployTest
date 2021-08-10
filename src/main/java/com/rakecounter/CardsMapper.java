package com.rakecounter;

import com.rakecounter.models.Card;
import com.rakecounter.models.Rank;
import com.rakecounter.models.Suit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CardsMapper {
    private Map<String, Card> cards;

    public CardsMapper() {
        cards = getCards();
    }

    private Map<String, Card> getCards() {
        Map<String, Card> cards = new HashMap<>();

        cards.put("6c", new Card(Rank.SIX, Suit.CLUB));
        cards.put("6h", new Card(Rank.SIX, Suit.HEART));
        cards.put("6d", new Card(Rank.SIX, Suit.DIAMOND));
        cards.put("6s", new Card(Rank.SIX, Suit.SPADE));

        cards.put("7c", new Card(Rank.SEVEN, Suit.CLUB));
        cards.put("7h", new Card(Rank.SEVEN, Suit.HEART));
        cards.put("7d", new Card(Rank.SEVEN, Suit.DIAMOND));
        cards.put("7s", new Card(Rank.SEVEN, Suit.SPADE));

        cards.put("8c", new Card(Rank.EIGHT, Suit.CLUB));
        cards.put("8h", new Card(Rank.EIGHT, Suit.HEART));
        cards.put("8d", new Card(Rank.EIGHT, Suit.DIAMOND));
        cards.put("8s", new Card(Rank.EIGHT, Suit.SPADE));

        cards.put("9c", new Card(Rank.NINE, Suit.CLUB));
        cards.put("9h", new Card(Rank.NINE, Suit.HEART));
        cards.put("9d", new Card(Rank.NINE, Suit.DIAMOND));
        cards.put("9s", new Card(Rank.NINE, Suit.SPADE));

        cards.put("Tc", new Card(Rank.TEN, Suit.CLUB));
        cards.put("Th", new Card(Rank.TEN, Suit.HEART));
        cards.put("Td", new Card(Rank.TEN, Suit.DIAMOND));
        cards.put("Ts", new Card(Rank.TEN, Suit.SPADE));

        cards.put("Jc", new Card(Rank.JACK, Suit.CLUB));
        cards.put("Jh", new Card(Rank.JACK, Suit.HEART));
        cards.put("Jd", new Card(Rank.JACK, Suit.DIAMOND));
        cards.put("Js", new Card(Rank.JACK, Suit.SPADE));

        cards.put("Qc", new Card(Rank.QUEEN, Suit.CLUB));
        cards.put("Qh", new Card(Rank.QUEEN, Suit.HEART));
        cards.put("Qd", new Card(Rank.QUEEN, Suit.DIAMOND));
        cards.put("Qs", new Card(Rank.QUEEN, Suit.SPADE));

        cards.put("Kc", new Card(Rank.KING, Suit.CLUB));
        cards.put("Kh", new Card(Rank.KING, Suit.HEART));
        cards.put("Kd", new Card(Rank.KING, Suit.DIAMOND));
        cards.put("Ks", new Card(Rank.KING, Suit.SPADE));

        cards.put("Ac", new Card(Rank.ACE, Suit.CLUB));
        cards.put("Ah", new Card(Rank.ACE, Suit.HEART));
        cards.put("Ad", new Card(Rank.ACE, Suit.DIAMOND));
        cards.put("As", new Card(Rank.ACE, Suit.SPADE));

        return cards;
    }

    public Card getCard(String card) {
        if (cards.containsKey(card)) {
            return cards.get(card);
        }
        throw new IllegalArgumentException("unknown card string: " + card);
    }

    public List<Card> getCards(String cardsSource) {
        List<Card> cards = new ArrayList<>();
        String[] s = cardsSource.split(" ");
        for (String s1 : s) {
            Card card = getCard(s1);
            cards.add(card);
        }
        return cards;
    }
}
