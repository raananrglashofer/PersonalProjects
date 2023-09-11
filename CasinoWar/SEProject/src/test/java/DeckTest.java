import org.junit.Assert.*;
import org.junit.Test;
import java.util.Set;
import java.util.HashSet;
import java.util.HashSet;

import static org.junit.Assert.*;

public class DeckTest {
    @Test
    public void checkDeck() {
        Deck deck = new Deck();
        Set<Deck.Card> cards = new HashSet<Deck.Card>();
        for (Deck.Card card : deck.getDeck()) {
            cards.add(card);
        }
        assertEquals(52, cards.size());
        Deck.Card x = deck.pickRandomCard();
        Deck.Card y = deck.pickRandomCard();
        Deck.Card z = deck.pickRandomCard();
        x.getCardPrinted();
        y.getCardPrinted();
        z.getCardPrinted();
        assertEquals(49, deck.getDeck().size());
    }

    @Test
    public void checkPullRandom() {
        Deck deck = new Deck();
        Set<Deck.Card> pulledCards = new HashSet<Deck.Card>();
        for (int i = 0; i < 52; i++) {
            assertTrue(pulledCards.add(deck.pickRandomCard()));
        }
        assertEquals(52, pulledCards.size());
    }
}