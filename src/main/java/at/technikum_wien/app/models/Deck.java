package at.technikum_wien.app.models;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

@Getter
@Setter

public class Deck {
    @JsonAlias({"Token"})
    String token;
    ArrayList<Card> cards = new ArrayList<>(4);

    public Deck(){}
    public Deck(String token) {
        this.token = token;
    }
    public void setCardsFromId(ArrayList<String> cardIds) throws Exception {
        if (cardIds == null) {
            throw new Exception("No card-ids appended");
        }

        if (cardIds.size() < 4) {
            throw new Exception("Not enough card-ids appended");
        }

        if (cardIds.size() > 4) {
            throw new Exception("Too many card-ids appended");
        }

        cards.clear();
        for (String cardId : cardIds) {
            Card card = new Card();
            card.setId(cardId);
            cards.add(card);
        }
    }
    public void setCardsFromCards(ArrayList<Card> cardsToDeck) throws Exception {
        cards.clear();
        cards.addAll(cardsToDeck);
    }
    public ArrayList<String> getCardIds() {
        ArrayList<String> cardIds = new ArrayList<>();
        for (Card card : cards) {
            cardIds.add(card.getId());
        }
        return cardIds;
    }

    public Card getRandomCard() {
        Random random = new Random();
        Integer index = random.nextInt(cards.size());
        return cards.get(index);
    }


}
