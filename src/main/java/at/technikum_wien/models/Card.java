package at.technikum_wien.models;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public abstract class Card {
    private String cardName;
    private int damage;
    private String element;

    public Card(String name, int damage, String element) {
        this.cardName = name;
        this.damage = damage;
        this.element = element;
    }

}