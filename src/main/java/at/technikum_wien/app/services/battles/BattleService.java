package at.technikum_wien.app.services.battles;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;

import java.io.IOException;
import java.sql.SQLException;

public class BattleService implements Service {
    BattleController controller;
    public BattleService(BattleController battleController) {
        controller = battleController;
    }

    @Override
    public Response handleRequest(Request request){
        if(request.getMethod().equals(Method.POST)){
            return this.controller.startBattle(request);
        }
        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.PLAIN_TEXT, "Method Not Allowed");
    }
}
