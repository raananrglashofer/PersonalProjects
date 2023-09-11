import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class Deck {
    public class Card {
        enum Suits {Diamonds, Hearts, Spades, Clubs};
        // Each card has a number and a suit
        private int number;
        private Suits suit;
        public Card(Suits suit, int num) {
            this.suit = suit;
            this.number = num;
        }
        public void getCardPrinted() {
            System.out.println(number + " of " + suit.toString());
        }
        public int getNumber(){
            return this.number;
        }
    }
    // Each deck is an array of cards
    private Set<Card> deckSet;
    //Our constructor fills a 52 index array with each card
    public Deck () {
        deckSet = new HashSet<Card>();
        for (Deck.Card.Suits suit : Card.Suits.values()) {
            for (int i = 2; i < 15; i++) {
                deckSet.add(new Card(suit, i));
            }
        }
    }
    //pull a random card from the deck
    //need to make sure we can't pull the same card twice
    public Card pickRandomCard () {
        int size = deckSet.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for(Card card : deckSet)
        {
            if (i == item) {
                deckSet.remove(card);
                return card;
            }
            i++;
        }
        throw new IllegalArgumentException("no cards left to pull");
    }

    //return the current deck
    public Set<Card> getDeck () {
        return this.deckSet;
    }
}