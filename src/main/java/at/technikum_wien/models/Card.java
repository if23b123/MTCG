package at.technikum_wien.models;

public abstract class Card {
    private String cardName;
    private int damage;
    private String element;

    public Card(String name, int damage, String element) {
        this.cardName = name;
        this.damage = damage;
        this.element = element;
    }

    public int getDamage() {
        return damage;
    }

    public String getCardName() {
        return cardName;
    }

    public String getElement() {
        return element;
    }

    public abstract boolean calcEffectiveness(Card opponentCard);
    public boolean calcDamage(Card opponentCard) {
        if(opponentCard.getDamage()<getDamage()){
            return true;
        }
        return false;
    }
    public boolean calculateElementEffectiveness(Card opponentCard) {
        if (getElement().equals("Water")) {
            if (opponentCard.getElement().equals("Fire")) {
                return true;
            } else if (opponentCard.getElement().equals("Normal")) {
                return false;
            }
        } else if (getElement().equals("Fire")) {
            if (opponentCard.getElement().equals("Water")) {
                return false;
            } else if (opponentCard.getElement().equals("Normal")) {
                return true;
            }
        } else if (getElement().equals("Normal")) {
            if (opponentCard.getElement().equals("Water")) {
                return true;
            } else if (opponentCard.getElement().equals("Fire")) {
                return false;
            }
        }
        return calcDamage(opponentCard);
    }
}