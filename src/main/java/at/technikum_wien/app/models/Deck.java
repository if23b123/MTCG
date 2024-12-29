package at.technikum_wien.app.models;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter

public class Deck {
    @JsonAlias({"Token"})
    String token;
    Collection<Card> cards = new ArrayList<>(4);

    public Deck(){}
    public Deck(String token) {
        this.token = token;
    }
    public void setCards(ArrayList<String> cardIds) throws Exception {
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

    public ArrayList<String> getCardIds() {
        ArrayList<String> cardIds = new ArrayList<>();
        for (Card card : cards) {
            cardIds.add(card.getId());
        }
        return cardIds;
    }


}
