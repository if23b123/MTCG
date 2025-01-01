package at.technikum_wien.app.services.tradings;

import at.technikum_wien.app.controller.Controller;
import at.technikum_wien.app.dal.DataAccessException;
import at.technikum_wien.app.dal.repository.*;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.Trade;
import at.technikum_wien.app.models.User;
import at.technikum_wien.app.services.cards.CardController;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.Objects;

public class TradingsController extends Controller {
    private TradingRepository repository;
    private UserRepository userRepository;
    private CardRepository cardRepository;
    private PackageRepository packageRepository;
    private DeckRepository deckRepository;
    public TradingsController() {
        repository = new TradingRepository();
        userRepository = new UserRepository();
        cardRepository = new CardRepository();
        packageRepository = new PackageRepository();
        deckRepository = new DeckRepository();
    }

    public Response getTradingDeals(Request request) {
        try{
            if(this.userRepository.searchToken(request.getHeaderMap().getHeader("Authorization").substring(7))){
                ArrayList<Trade> deals = this.repository.selectAll();
                return new Response(HttpStatus.OK, ContentType.JSON, this.getObjectMapper().writeValueAsString(deals));
            }
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User not logged in");
        }catch(JsonProcessingException | RuntimeException e){
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, e.getMessage());
        }

    }

    public Response createDeal(Request request) {
        try{
            User user = this.userRepository.getUserByToken(request.getHeaderMap().getHeader("Authorization").substring(7));
            Trade newTrade = this.getObjectMapper().readValue(request.getBody(), Trade.class);
            newTrade.setDealCreatorUsername(user.getUsername());
            if(checkCard(newTrade.getCardIdToTrade(), user.getToken())){
                if(this.repository.createDeal(newTrade)){
                    return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Deal created");
                }
            }else{
                return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Card is not viable for trade");
            }

            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Deal could not be created");

        }catch(JsonProcessingException | RuntimeException e){
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, e.getMessage());
        }
    }

    public Response deleteDeal(Request request){
        try{
            String token = request.getHeaderMap().getHeader("Authorization").substring(7);
            User user = this.userRepository.getUserByToken(token);
            String dealId = request.getPathParts().get(1);
            if(this.repository.deleteDealTokenId(dealId, user.getUsername())){
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Deal deleted");
            }
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Deal could not be deleted");
        }catch( RuntimeException e){
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, e.getMessage());
        }

    }

    public Response trade(Request request) {
        try{
            String token = request.getHeaderMap().getHeader("Authorization").substring(7);
            User user = this.userRepository.getUserByToken(token);
            String dealId = request.getPathParts().get(1);
            String cardId = this.getObjectMapper().readValue(request.getBody(), String.class);
            String cardIdOfferedCard = this.repository.getCardIdOfTrade(dealId);
            Card offered;
            Card tradeWith = this.cardRepository.getCardById(cardId);
            Trade trade = this.repository.getDealById(dealId);
            String type="";
            if(tradeWith.getType().contains("monster"))
                type = "monster";
            else if(tradeWith.getType().contains("spell"))
                type = "spell";
            if(checkCard(cardId,token)){
                if(Objects.equals(trade.getWantedType(), type) && trade.getMinDamage()<= tradeWith.getDamage() ){
                    if(this.repository.insertTrader(dealId, cardId, user)){
                        if(cardIdOfferedCard == null){
                            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Issue with retrieving card (offer)");
                        }
                        offered = this.cardRepository.getCardById(cardIdOfferedCard);
                        tradeWith = this.cardRepository.getCardById(cardId);

                        if(this.cardRepository.updatePackageId(offered.getId(), tradeWith.getPackage_id()) && this.cardRepository.updatePackageId(tradeWith.getId(), offered.getPackage_id())){
                            return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Trade finished");
                        }
                        return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Issue with switching package ids");

                    }else{
                        return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "User cannot trade by themselves");
                    }
                }else{
                    return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Card doesn't meet the trade's requirements");
                }
            }else{
                return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Card is not viable for trade" );
            }
        }catch(JsonProcessingException | RuntimeException e){
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, e.getMessage());
        }

    }
    public boolean checkCard(String cardId, String token){
        Card card = this.cardRepository.getCardById(cardId);
        if(Objects.equals(token, this.packageRepository.getAcquiredByById(card.getPackage_id()))){
            if(this.deckRepository.isCardOnTokensDeck(token, cardId)){
                return false;
            }
            return true;
        }
        return false;
    }
}
