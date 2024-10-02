public class SpellCard extends Card {
    public SpellCard(String name, int damage, String element) {
        super(name, damage, element);
    }
    @Override
    public boolean calcEffectiveness(Card opponentCard){
        return true;
    }
}
