package at.technikum_wien.app.services.transactions;

import at.technikum_wien.app.controller.Controller;
import at.technikum_wien.app.dal.DataAccessException;
import at.technikum_wien.app.dal.repository.TransactionRepository;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;

public class TransactionController extends Controller {
    private TransactionRepository repository;
    private UserRepository userRepository;
    public TransactionController() {
        this.repository = new TransactionRepository();
        this.userRepository = new UserRepository();
    }

    public Response acquirePackage(Request request) {
        try{
            String requestToken = request.getHeaderMap().getHeader("Authorization").substring(7);
            if(this.userRepository.searchToken(requestToken)){
                int coins = this.userRepository.getCoins(requestToken);
                if(coins>=5){
                    if(this.repository.postAcquiredBy(requestToken)){
                        this.userRepository.updateCoins(requestToken, coins);
                        return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Acquired by user with token (" + requestToken + ")");
                    }
                    return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "No available packages");
                }
                return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "Not enough money");
            }
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Token not found");
        }catch(RuntimeException e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, e.getMessage());
        }
    }
}
