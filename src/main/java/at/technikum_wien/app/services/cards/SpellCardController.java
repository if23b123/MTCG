package at.technikum_wien.app.services.cards;

import at.technikum_wien.app.models.Card;

public class SpellCardController extends CardController {
    public SpellCardController(String name, int damage, String element) {
        super(name, damage, element);
    }
    @Override
    public boolean calcEffectiveness(Card opponentCard){
        return true;
    }
}
