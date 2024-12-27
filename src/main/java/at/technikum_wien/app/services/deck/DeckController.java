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
            deck.setCards(this.getObjectMapper().readValue(request.getBody(), new TypeReference<ArrayList<String>>(){}));

            //See if user is logged in (token exists in db)
            String authorizationToken = request.getHeaderMap().getHeader("Authorization").substring(7);
            if(this.userRepository.searchToken(authorizationToken)){

                //Get the packages the cards are in
                ArrayList<Integer> cardPackages = this.cardRepository.getPackages(deck.getCards());

                //Check if the Packages are acquired of the token, if the token has the cards on its stack
                if(this.packageRepository.allPackagesOfToken(cardPackages, authorizationToken)){

                    //Insert Cards into users deck
                    if(this.deckRepository.insertDeck(authorizationToken,deck)){
                        return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Deck configured");
                    }
                    return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "Error when configuring Deck");
                }
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Card not on stack");
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

}
