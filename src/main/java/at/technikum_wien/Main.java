package at.technikum_wien;

import at.technikum_wien.app.services.cards.CardController;
import at.technikum_wien.app.services.cards.CardService;
import at.technikum_wien.app.services.deck.DeckController;
import at.technikum_wien.app.services.deck.DeckService;
import at.technikum_wien.app.services.transactions.TransactionController;
import at.technikum_wien.app.services.transactions.TransactionService;
import at.technikum_wien.app.services.users.UserController;
import at.technikum_wien.app.services.packages.PackageController;
import at.technikum_wien.app.services.packages.PackageService;
import at.technikum_wien.app.services.users.SessionService;
import at.technikum_wien.app.services.users.UserService;
import at.technikum_wien.httpserver.server.Server;
import at.technikum_wien.httpserver.utils.Router;


public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try{
            server.start();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    private static Router configureRouter()
    {
        Router router = new Router();
        UserController userController = new UserController();
        PackageController packageController = new PackageController();
        TransactionController transactionController = new TransactionController();
        CardController cardController = new CardController();
        DeckController deckController = new DeckController();

        router.addService("/users", new UserService(userController));
        router.addService("/sessions", new SessionService(userController));
        router.addService("/packages", new PackageService(packageController));
        router.addService("/transactions", new TransactionService(transactionController));
        router.addService("/cards", new CardService(cardController));
        router.addService("/deck", new DeckService(deckController));
        return router;
    }
}


