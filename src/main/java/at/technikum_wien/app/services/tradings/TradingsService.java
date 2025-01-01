package at.technikum_wien.app.services.tradings;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class TradingsService implements Service {
    private TradingsController controller;
    public TradingsService(TradingsController controller) {
        this.controller = controller;
    }

    @Override
    public Response handleRequest(Request request) {
        if(Objects.equals(request.getMethod(), Method.GET)){
            return this.controller.getTradingDeals(request);
        }else if(Objects.equals(request.getMethod(), Method.POST)){
            if(request.getPathParts().size()>1){
                return this.controller.trade(request);
            }
            return this.controller.createDeal(request);
        }else if(Objects.equals(request.getMethod(), Method.DELETE)){
            if(request.getPathParts().size()>1){
                return this.controller.deleteDeal(request);
            }
            return new Response(HttpStatus.NOT_FOUND,ContentType.PLAIN_TEXT, "Card id to be deleted not set in path");
        }
        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.PLAIN_TEXT, "Method not implemented");
    }
}
