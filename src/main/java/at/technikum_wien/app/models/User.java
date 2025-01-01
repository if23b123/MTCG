package at.technikum_wien.app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.Stack;

import com.fasterxml.jackson.annotation.JsonAlias;

@Getter
@Setter

public class User {
    private String uuid;
    @JsonAlias({"Username"})
    private String username;
    @JsonAlias({"Password"})
    private String password;
    private String token;
    @JsonAlias({"Name"})
    private String name;
    @JsonAlias({"Bio"})
    private String bio;
    @JsonAlias({"Image"})
    private String image;
    @JsonAlias({"Coins"})
    private Integer coins;
    private List<Card> stack;
    private List<Card> deck;
    private Integer elo;
    private Integer gamesPlayed;//user stats
    private Integer gamesWon;//user stats

    public User() {
        this.coins=20;
    } //for JSON

    public User(String username, String password) {
        this.uuid = "";
        this.username = username;
        this.password = password;
        this.name = "";
        this.bio = "";
        this.image = "";
        this.coins = 20;
        this.stack = new Stack<>();
        this.deck = new Vector<Card>() ;
        this.elo = 100; //starting value
        this.gamesPlayed = 0;
        this.gamesWon = 0;
    }




}
