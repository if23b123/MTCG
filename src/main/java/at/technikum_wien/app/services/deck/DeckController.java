package at.technikum_wien.app.services.deck;

import at.technikum_wien.app.controller.Controller;
import at.technikum_wien.app.dal.repository.CardRepository;
import at.technikum_wien.app.dal.repository.DeckRepository;
import at.technikum_wien.app.dal.repository.PackageRepository;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.Deck;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.ArrayType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DeckController extends Controller {
    private DeckRepository deckRepository;
    private UserRepository userRepository;
    private CardRepository cardRepository;
    private PackageRepository packageRepository;

    public DeckController() {
        deckRepository = new DeckRepository();
        userRepository = new UserRepository();
        cardRepository = new CardRepository();
        packageRepository = new PackageRepository();
    }

    public Response configureDeck(Request request){
        try{
            //Setting card Ids of deck
            Deck deck = new Deck();
            deck.setCardsFromId(this.getObjectMapper().readValue(request.getBody(), new TypeReference<>(){}));

            //See if user is logged in (token exists in db)
            String authorizationToken = request.getHeaderMap().getHeader("Authorization").substring(7);
            if(this.userRepository.searchToken(authorizationToken)){

                //Get the packages the cards are in
                ArrayList<Integer> cardPackages = new ArrayList<>();
                ArrayList <Card> cards = this.cardRepository.getCardsById(deck.getCardIds());
                for(Card card : cards){
                    cardPackages.add(card.getPackage_id());
                }

                //Check if the Packages are acquired of the token, if the token has the cards on its stack
                Integer notTokensCard = this.packageRepository.allPackagesOfToken(cardPackages, authorizationToken);
                if(notTokensCard==null){

                    //Insert Cards into users deck
                    if(this.deckRepository.insertDeck(authorizationToken,deck)){
                        return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Deck configured");
                    }
                    return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "Error when configuring Deck");
                }
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Card from Package (" + notTokensCard.toString() + ") not on stack");
            }
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT,"User not logged in (No such token found)");
        }catch(JsonProcessingException e){
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Unable to configure deck");
        } catch (RuntimeException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "SQL Error");
        }catch(Exception e){
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, e.getMessage());
        }
    }

    public Response getDeck(Request request){
        String token = request.getHeaderMap().getHeader("Authorization").substring(7);
            if(this.userRepository.searchToken(token)){
                Collection<String> cardIds = this.deckRepository.getCardIds(token);
                Collection<Card> deckCards = new ArrayList<>();
                if(!cardIds.isEmpty()){
                    deckCards = this.cardRepository.getCardsById(cardIds);
                }
                try{
                    String responseObject = this.getObjectMapper().writeValueAsString(deckCards);
                    return new Response(HttpStatus.OK, ContentType.JSON, responseObject);
                } catch (JsonProcessingException e) {
                    return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, e.getMessage());
                }
            }
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT,"User not logged in (No such token found)");

    }

    public Response getPlainDeck(Request request){
        try{
            Response response = this.getDeck(request);
            if(response.getStatus()==200){
                List<Map<String, Object>> cards = this.getObjectMapper().readValue(response.getContent(), new TypeReference<>() {});

                StringBuilder table = new StringBuilder();
                table.append(String.format("%-40s %-20s %-10s %-15s %-10s %-10s%n", "ID", "Name", "Element", "Type", "Damage", "Package ID"));
                table.append("----------------------------------------------------------------------------------------------------------------\n");
                for (Map<String, Object> card : cards) {
                    table.append(String.format("%-40s %-20s %-10s %-15s %-10.1f %-10d%n",
                            card.get("id"),
                            card.get("Name"),
                            card.get("element"),
                            card.get("type"),
                            card.get("damage"),
                            card.get("package_id")));
                }
                return new Response(HttpStatus.OK, ContentType.JSON, table.toString());
            }
            return response;

        }catch(JsonProcessingException | RuntimeException e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, e.getMessage());
        }


    }

}
