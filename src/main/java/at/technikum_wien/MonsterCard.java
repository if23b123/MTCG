package at.technikum_wien;

public class MonsterCard extends Card {
    public MonsterCard(String name, int damage, String element) {
        super(name, damage, element);
    }

    @Override
    public boolean calcEffectiveness(Card opponentCard) {
        boolean result = false;

        if (opponentCard instanceof MonsterCard) {
            switch (getCardName()) {
                case "Dragon":
                    if (opponentCard.getCardName().equals("Goblin")) {
                        return true;  // Goblin loses to Dragon automatically
                    } else if (opponentCard.getCardName().equals("FireElf")) {
                        return false; // FireElf evades Dragon
                    }
                    break;
                case "Goblin":
                    if (opponentCard.getCardName().equals("Dragon")) {
                        return false; // Goblin afraid of Dragon
                    }
                    break;
                case "FireElf":
                    if (opponentCard.getCardName().equals("Dragon")) {
                        return true;  // FireElf evades Dragon
                    }
                    break;
                case "Ork":
                    if (opponentCard.getCardName().equals("Wizard")) {
                        return false; // Ork cannot attack Wizard
                    }
                    break;
                case "Wizard":
                    if (opponentCard.getCardName().equals("Ork")) {
                        return true;
                    }
                    break;
            }
            result = calcDamage(opponentCard);
        } else if (opponentCard instanceof SpellCard) {
            if (getCardName().equals("Knight") && opponentCard.getElement().equals("Water")) {
                return false;
            } else if (getCardName().equals("Kraken")) {
                return true;
            } else {
                result = calculateElementEffectiveness(opponentCard);
            }
        }
        return result;
    }

}
