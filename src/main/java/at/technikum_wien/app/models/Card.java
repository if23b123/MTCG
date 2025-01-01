package at.technikum_wien.app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Setter;
import lombok.Getter;

import java.util.Objects;

@Getter
@Setter
public class Card {
    @JsonAlias({"Id","id"})
    private String id;
    @JsonAlias({"Name", "name"})
    private String cardName;
    @JsonAlias({"Damage", "damage"})
    private double damage;
    private String element;
    private String type;
    private Integer package_id;



    public Card(){}

    public Card(String name, int damage) {
        this.cardName = name;
        this.damage = damage;
    }

    public Card(String name, int damage, String element) {
        this.cardName = name;
        this.damage = damage;
        this.element = element;
    }

    @JsonSetter("Name")
    public void setCardName(String cardName) {
        this.cardName = cardName;
        deriveElementAndType();
    }

    private void deriveElementAndType() {
        if (cardName == null) {
            return;
        }

        if (cardName.toLowerCase().contains("spell")) {
            this.type = "spellcard";
            if (cardName.toLowerCase().contains("water")) {
                this.element = "water";
            } else if (cardName.toLowerCase().contains("fire")) {
                this.element = "fire";
            } else {
                this.element = "normal";
            }
        } else {
            this.type = "monstercard";
            if (cardName.toLowerCase().contains("water") || Objects.equals(getCardName(), "Kraken")) {
                this.element = "water";
            } else if (cardName.toLowerCase().contains("fire") || Objects.equals(getCardName(), "Dragon")) {
                this.element = "fire";
            } else {
                this.element = "normal";
            }
        }
    }

    public void applyPowerSurge() {
        this.damage *= 1.5;
    }


}