package at.technikum_wien.app.services.deck;

import at.technikum_wien.app.models.Deck;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class DeckService implements Service {

    private DeckController controller;
    public DeckService(DeckController controller) {
        this.controller = controller;
    }

    @Override
    public Response handleRequest(Request request){
        if(request.getMethod().equals(Method.PUT)){
            return this.controller.configureDeck(request);
        }else if(request.getMethod().equals(Method.GET)){
            if(Objects.equals(request.getParams(), "format=plain")){
                return this.controller.getPlainDeck(request);
            }
            return this.controller.getDeck(request);
        }
        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.PLAIN_TEXT, "Method Not Allowed");

    }
}
