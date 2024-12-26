package at.technikum_wien.app.services.cards;

import at.technikum_wien.app.controller.Controller;
import at.technikum_wien.app.dal.repository.CardRepository;
import at.technikum_wien.app.dal.repository.PackageRepository;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.Collection;

public class CardController extends Controller {
    private CardRepository cardRepo;
    private UserRepository userRepo;
    private PackageRepository packageRepo;

    public CardController() {
        cardRepo = new CardRepository();
        userRepo = new UserRepository();
        packageRepo = new PackageRepository();
    }

    public Response getAcquiredCards(Request request) {
        try {
            if(request.getHeaderMap().getHeader("Authorization") != null) {
                String token = request.getHeaderMap().getHeader("Authorization").substring(7);
                if (this.userRepo.searchToken(token)) {
                    Collection<Integer> packages = this.packageRepo.getPackageId(token);
                    if (packages.size() > 0) {
                        Collection<Card> acquiredCards = new ArrayList<>();
                        for (Integer packageId : packages) {
                            acquiredCards.addAll(this.cardRepo.selectAllAcquiredCards(packageId));
                        }
                        String cards = "";
                        for (Card card : acquiredCards) {
                            cards += this.getObjectMapper().writeValueAsString(card) + "\r\n";
                        }
                        return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, cards);
                    }
                    return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User has no packages acquired");
                }
                return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Token not found - User not logged in");

            }
            return new Response(HttpStatus.UNAUTHORIZED,ContentType.PLAIN_TEXT, "Unauthorized");
        }catch(JsonProcessingException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, e.getMessage());
        }
    }



    public boolean calcEffectiveness(Card userCard, Card opponentCard) {
        boolean result = false;

        if (opponentCard.getElement() == "monster") {
            switch (userCard.getCardName()) {
                case "Dragon":
                    if (opponentCard.getCardName().equals("Goblin")) {
                        return true;  // Goblin loses to Dragon automatically
                    } else if (opponentCard.getCardName().equals("FireElf")) {
                        return false; // FireElf evades Dragon
                    }
                    break;
                case "Goblin":
                    if (opponentCard.getCardName().equals("Dragon")) {
                        return false; // Goblin afraid of Dragon
                    }
                    break;
                case "FireElf":
                    if (opponentCard.getCardName().equals("Dragon")) {
                        return true;  // FireElf evades Dragon
                    }
                    break;
                case "Ork":
                    if (opponentCard.getCardName().equals("Wizard")) {
                        return false; // Ork cannot attack Wizard
                    }
                    break;
                case "Wizard":
                    if (opponentCard.getCardName().equals("Ork")) {
                        return true;
                    }
                    break;
            }
            result = calcDamage(userCard, opponentCard);
        } else if (opponentCard.getElement()=="spell") {
            if (userCard.getCardName().equals("Knight") && opponentCard.getElement().equals("Water")) {
                return false;
            } else if (userCard.getCardName().equals("Kraken")) {
                return true;
            } else {
                result = calculateElementEffectiveness(userCard, opponentCard);
            }
        }
        return result;
    }
    public boolean calcDamage(Card userCard, Card opponentCard) {
        if(opponentCard.getDamage()< userCard.getDamage()){
            return true;
        }
        return false;
    }
    public boolean calculateElementEffectiveness(Card userCard,Card opponentCard) {
        if (userCard.getElement().equals("Water")) {
            if (opponentCard.getElement().equals("Fire")) {
                return true;
            } else if (opponentCard.getElement().equals("Normal")) {
                return false;
            }
        } else if (userCard.getElement().equals("Fire")) {
            if (opponentCard.getElement().equals("Water")) {
                return false;
            } else if (opponentCard.getElement().equals("Normal")) {
                return true;
            }
        } else if (userCard.getElement().equals("Normal")) {
            if (opponentCard.getElement().equals("Water")) {
                return true;
            } else if (opponentCard.getElement().equals("Fire")) {
                return false;
            }
        }
        return calcDamage(userCard,opponentCard);
    }
}
