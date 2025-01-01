package at.technikum_wien.app.services.battles;

import at.technikum_wien.app.business.BattleLogic;
import at.technikum_wien.app.dal.DataAccessException;
import at.technikum_wien.app.dal.repository.*;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.Deck;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BattleController {
    private BattleRepository battleRepo;
    private UserRepository userRepo;
    private DeckRepository deckRepo;
    private CardRepository cardRepo;
    private PackageRepository packageRepo;
    private BattleLogic logic;
    private ConcurrentHashMap<String, Deck> usersToBattle = new ConcurrentHashMap<>();
    private Integer player1RoundWins=0;
    private Integer player2RoundWins=0;

    public BattleController() {
        battleRepo = new BattleRepository();
        userRepo = new UserRepository();
        deckRepo = new DeckRepository();
        cardRepo = new CardRepository();
        packageRepo = new PackageRepository();
        logic = new BattleLogic();
    }

    public Response startBattle(Request request) {
        String token = request.getHeaderMap().getHeader("Authorization").substring(7);
        try {
            if (!this.userRepo.searchToken(token)) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User not logged in");
            }
            if (usersToBattle.containsKey(token)) {
                return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "User already has a battle");
            }

            Collection<String> cardIds = this.deckRepo.getCardIds(token);
            ArrayList<Card> cardsOfDeck = this.cardRepo.getCardsById(cardIds);
            if (cardsOfDeck.isEmpty()) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "No cards in deck for token: " + token);
            }
            Deck userDeck = new Deck(token);
            userDeck.setCardsFromCards(cardsOfDeck);
            usersToBattle.put(token, userDeck);

            if (usersToBattle.size() < 2) {
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Waiting for an opponent...");
            }

            List<Deck> decks = new ArrayList<>(usersToBattle.values());
            Deck deckPlayer1 = decks.get(0);
            Deck deckPlayer2 = decks.get(1);
            User player1 = this.userRepo.getUserByToken(deckPlayer1.getToken());
            User player2 = this.userRepo.getUserByToken(deckPlayer2.getToken());

            if (player1.getElo() <= 0) {
                return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, player1.getUsername() + " has no ELO points");
            }
            if (player2.getElo() <= 0) {
                return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, player2.getUsername() + " has no ELO points");
            }

            try {
                int rounds = 0;
                for (int i = 0; i < 100; i++) {
                    int cardsPlayer1 = this.deckRepo.countDeckByToken(deckPlayer1.getToken());
                    int cardsPlayer2 = this.deckRepo.countDeckByToken(deckPlayer2.getToken());

                    if (cardsPlayer1 > 0 && cardsPlayer2 > 0 && player1.getElo() > 0 && player2.getElo() > 0) {
                        round(deckPlayer1, deckPlayer2);
                        rounds++;
                    } else {
                        break;
                    }
                }

                usersToBattle.clear();
                String resultMessage = this.logic.getBattleLog();

                if (player1RoundWins > player2RoundWins) {
                    this.userRepo.updateLosses(player2.getElo(), player2.getToken());
                    this.userRepo.updateWins(player1.getElo(), player1.getToken());
                    resultMessage += "\nWinner: " + player1.getUsername() + " (won " + player1RoundWins + " and lost " + player2RoundWins+" out of " + rounds + " rounds)";
                    this.battleRepo.insertBattleStats(player1, player2, player1.getToken(), resultMessage);
                } else if (player2RoundWins > player1RoundWins) {
                    this.userRepo.updateLosses(player1.getElo(), player1.getToken());
                    this.userRepo.updateWins(player2.getElo(), player2.getToken());
                    resultMessage += "\nWinner: " + player2.getUsername() + " (won " + player2RoundWins + " and lost " + player1RoundWins+" out of " + rounds + " rounds)";
                    this.battleRepo.insertBattleStats(player1, player2, player2.getToken(), resultMessage);
                } else {
                    resultMessage += "\nBattle resulted in a draw!";
                    this.battleRepo.insertBattleStatsDraw(player1, player2, resultMessage);
                }

                player1RoundWins = 0;
                player2RoundWins = 0;

                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, resultMessage);

            } catch (DataAccessException e) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, e.getMessage());
            } catch (RuntimeException e) {
                return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "SQL Error - " + e.getMessage());
            }

        } catch (RuntimeException e) {
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, e.getMessage());
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, e.getMessage());
        }
    }

    public void round(Deck deckPlayer1, Deck deckPlayer2){
        Card cardPlayer1 = deckPlayer1.getRandomCard();
        Card cardPlayer2 = deckPlayer2.getRandomCard();
        Random random = new Random();
        Integer randomNumber = random.nextInt(10);

        if(randomNumber == 1){
            cardPlayer1.applyPowerSurge();
            this.logic.addToBattleLog("\r\n"+ cardPlayer1.getCardName() + "got their power boost! Damage is at " + cardPlayer1.getDamage());
            //unique feature implemented - power boost for each round with a probability of 1/10 - both players cannot have power boost in the same round
        } else if(randomNumber == 2){
            cardPlayer2.applyPowerSurge();
            this.logic.addToBattleLog("\r\n"+ cardPlayer2.getCardName() + "got their power boost! Damage is at " + cardPlayer2.getDamage());
        }

        Card winnerCard = null;

        if(Objects.equals(cardPlayer1.getType(),"monstercard") && Objects.equals(cardPlayer2.getType(),"monstercard")){
            winnerCard = this.logic.pureMonsterCardFight(cardPlayer1, cardPlayer2);
        }else if(Objects.equals(cardPlayer1.getType(), "spellcard") && Objects.equals(cardPlayer2.getType(),"spellcard")){
            winnerCard = this.logic.pureSpellCardFight(cardPlayer1,cardPlayer2);
        }else{
            winnerCard=this.logic.mixTypeFight(cardPlayer1,cardPlayer2);
        }
        boolean updated = false;
        if(winnerCard != null){
            if(Objects.equals(winnerCard.getPackage_id(), cardPlayer1.getPackage_id())){
                updated=updateDecks(deckPlayer1.getToken(), deckPlayer2.getToken(),cardPlayer2);
                ++player1RoundWins;

            }else if(Objects.equals(winnerCard.getPackage_id(), cardPlayer2.getPackage_id())){
                updated=updateDecks(deckPlayer2.getToken(), deckPlayer1.getToken(), cardPlayer1);
                ++player2RoundWins;
            }
        }
        if(!updated){
            throw new RuntimeException("Error when updating deck");
        }

    }

    public boolean updateDecks(String winnerToken, String loserToken, Card loserCard){

        Integer maxPackageId = this.packageRepo.getMaxPackageByToken(winnerToken);
        if(this.cardRepo.updatePackageId(loserCard.getId(), maxPackageId)){
            if(this.deckRepo.updateTokenPosition(winnerToken, loserCard.getId())){
                if(this.deckRepo.updatePosition(loserToken)){
                    return true;
                }
            }
        }
        return false;

    }
}


