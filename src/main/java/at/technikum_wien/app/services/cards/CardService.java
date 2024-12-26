package at.technikum_wien.app.services.cards;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;

public class CardService implements Service {
    private CardController controller;
    public CardService() {
        controller = new CardController();
    }
    public CardService(CardController controller) {
        this.controller = controller;
    }

    public Response handleRequest(Request request) {
        if(request.getMethod()== Method.GET){
            return this.controller.getAcquiredCards(request);
        }
        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.PLAIN_TEXT, "Method not implemented");
    }
}
