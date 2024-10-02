import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Deck {

    private List<Card> cards;

    public List<Card> getCards() {
        return cards;
    }

    public int size() {
        return cards.size();
    }

    public Deck(List<Card> selectedCards) {
        /*if (selectedCards.size() != 4) {
            throw new IllegalArgumentException("A deck must contain exactly 4 cards.");
        }*/
        this.cards = new ArrayList<>(selectedCards);  // Creating a new list to avoid modification from outside
    }
    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            System.out.println("No cards left in the deck.");
            return null;
        }
        return cards.removeFirst();  // Remove and return the first card (top of the deck)
    }
    public void printDeck() {
        for (Card card : cards) {
            System.out.println(card.getCardName() + " - " + card.getDamage() + " damage (" + card.getElement() + ")");
        }
    }

}
