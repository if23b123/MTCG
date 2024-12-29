package at.technikum_wien.app.services.scoreboard;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;

public class ScoreboardService implements Service {
    private ScoreboardController controller;
    public ScoreboardService(ScoreboardController controller) {
        this.controller = controller ;
    }
    @Override
    public Response handleRequest(Request request){
        if(request.getMethod().equals(Method.GET)){
            return this.controller.getScoreBoard(request);
        }
        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.PLAIN_TEXT, "Method Not Allowed");
    }
}
