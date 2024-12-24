package at.technikum_wien.app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
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
    private Integer coins;
    private List<Card> stack;
    private List<Card> deck;
    private Integer elo;
    private Integer gamesPlayed;//user stats
    private Integer gamesWon;//user stats

    public User() {} //for JSON

    public User(String username, String password) {
        this.uuid = "";
        this.username = username;
        this.password = password;
        this.name = "";
        this.bio = "";
        this.image = "";
        this.coins = 20;
        this.stack = new ArrayList<>();
        this.deck = new ArrayList<>();
        this.elo = 100; //starting value
        this.gamesPlayed = 0;
        this.gamesWon = 0;
    }

    public void updateElo(boolean hasWon) {
        if (hasWon) {
            elo += 3;   // +3 points for win
            gamesWon++;
        } else {
            elo -= 5;   // -5 for loss
        }
        gamesPlayed++; //for stats
    }

    public boolean acquireCards(List<Card> newCards) { //to add a new package
        if (coins >= 5) {
            coins -= 5;
            stack.addAll(newCards); // adding newCards which is a new package to the users stack of cards
            return true;
        } else {
            return false;
        }
    }

    public boolean setDeck(List<Card> selectedCards) {
        if (selectedCards.size() != 4) {
            return false;
        }
        deck.clear();
        deck.addAll(selectedCards);
        return true;
    }
    public boolean battleLogic(Card userCard, Card opponentCard){
        return true;
    }
    public boolean battle(User opponent) {
        Random random = new Random();
        int randomCardUser= random.nextInt(4);
        int randomCardOpponent= random.nextInt(4);
        boolean result = battleLogic(deck.get(randomCardUser), opponent.deck.get(randomCardOpponent) ); //battle logic needed
        if (result) {
            updateElo(true);  // Update this user's ELO
            opponent.updateElo(false);  // Update opponent's ELO
        } else {
            updateElo(false);
            opponent.updateElo(true);
        }

        return result;
    }
    public boolean tradeCard(Card offerCard, Card requestCard, User otherUser) {

        //trade logic needed
        return true;
    }



}
