package at.technikum_wien.app.services.stats;

import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class StatsService implements Service {
    private StatsController statsController;

    public StatsService(StatsController statsController) {
        this.statsController = statsController;
    }

    @Override
    public Response handleRequest(Request request){
        if(Objects.equals(request.getMethod(), Method.GET)){

        }
        return null;
    }
}
