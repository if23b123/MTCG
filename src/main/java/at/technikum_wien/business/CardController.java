package at.technikum_wien.business;

import at.technikum_wien.models.Card;

public abstract class CardController extends Card{

    public CardController(String name, int damage, String element) {
        super(name, damage, element);
    }
    public abstract boolean calcEffectiveness(Card opponentCard);

    public boolean calcDamage(Card userCard, Card opponentCard) {
        if(opponentCard.getDamage()< userCard.getDamage()){
            return true;
        }
        return false;
    }
    public boolean calculateElementEffectiveness(Card userCard,Card opponentCard) {
        if (userCard.getElement().equals("Water")) {
            if (opponentCard.getElement().equals("Fire")) {
                return true;
            } else if (opponentCard.getElement().equals("Normal")) {
                return false;
            }
        } else if (userCard.getElement().equals("Fire")) {
            if (opponentCard.getElement().equals("Water")) {
                return false;
            } else if (opponentCard.getElement().equals("Normal")) {
                return true;
            }
        } else if (userCard.getElement().equals("Normal")) {
            if (opponentCard.getElement().equals("Water")) {
                return true;
            } else if (opponentCard.getElement().equals("Fire")) {
                return false;
            }
        }
        return calcDamage(userCard,opponentCard);
    }
}
