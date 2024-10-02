import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class User {
    private final String username;
    private final String password;
    private int coins;
    private List<Card> stack;
    private List<Card> deck;
    private int elo;
    private int gamesPlayed;//user stats
    private int gamesWon;//user stats

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.coins = 20;
        this.stack = new ArrayList<>();
        this.deck = new ArrayList<>();
        this.elo = 100; //starting value
        this.gamesPlayed = 0;
        this.gamesWon = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getCoins() {
        return coins;
    }

    public List<Card> getStack() {
        return stack;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public int getElo() {
        return elo;
    }

    public void updateElo(boolean hasWon) {
        if (hasWon) {
            elo += 3;   // +3 points for win
            gamesWon++;
        } else {
            elo -= 5;   // -5 for loss
        }
        gamesPlayed++; //for stats
        System.out.println(username + "'s new ELO: " + elo);
    }

    public boolean acquireCards(List<Card> newCards) { //to add a new package
        if (coins >= 5) {
            coins -= 5;
            stack.addAll(newCards); // adding newCards which is a new package to the users stack of cards
            System.out.println(username + " has acquired a new package of cards.");
            return true;
        } else {
            System.out.println("Not enough coins.");
            return false;
        }
    }

    public boolean setDeck(List<Card> selectedCards) {
        if (selectedCards.size() != 4) {
            System.out.println("Deck must contain exactly 4 cards!");
            return false;
        }
        deck.clear();
        deck.addAll(selectedCards);
        System.out.println(username + " has defined their deck.");
        return true;
    }
    public boolean battleLogic(Card userCard, Card opponentCard){

    }
    public boolean battle(User opponent) {
        System.out.println(username + " is battling " + opponent.getUsername() + "!");
        Random random = new Random();
        int randomCardUser= random.nextInt(4);
        int randomCardOpponent= random.nextInt(4);
        boolean result = battleLogic(deck.get(randomCardUser), opponent.deck.get(randomCardOpponent) ); //battle logic needed
        if (result) {
            updateElo(true);  // Update this user's ELO
            opponent.updateElo(false);  // Update opponent's ELO
            System.out.println(username + " has won the battle!");
        } else {
            updateElo(false);
            opponent.updateElo(true);
            System.out.println(username + " has lost the battle!");
        }

        return result;
    }
    public boolean tradeCard(Card offerCard, Card requestCard, User otherUser) {

        //trade logic needed
        return true;
    }

    public boolean register() {
        // Registration logic
        return true; // Registration successful
    }

    public boolean login(String username, String password) {
        //LogIn Logic - check w PostgreSQL database
        if (this.username.equals(username) && this.password.equals(password)) {
            System.out.println(username + " has logged in successfully.");
            return true;
        }
        System.out.println("Invalid credentials!");
        return false;
    }

}
