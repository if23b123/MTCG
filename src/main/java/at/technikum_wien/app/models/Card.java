package at.technikum_wien.app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class Card {
    @JsonAlias({"Id","id"})
    private String id;
    @JsonAlias({"Name", "name"})
    private String cardName;
    @JsonAlias({"Damage", "damage"})
    private double damage;
    @JsonAlias({"Element", "element"})
    private String element;
    @JsonAlias({"Type", "type"})
    private String type;


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


}