package at.technikum_wien.app.services.stats;

import at.technikum_wien.app.controller.Controller;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;

public class StatsController extends Controller {
    UserRepository userRepository = new UserRepository();
    public StatsController() {
        userRepository = new UserRepository();
    }

    public Response getUserStats(Request request) {
        String token = request.getHeaderMap().getHeader("Authorization").substring(7);
        try{
            if(this.userRepository.searchToken(token)){
                User user = this.userRepository.getUserByToken(token);
                Integer elo = user.getElo();
                Integer gamesPlayed = user.getGamesPlayed();
                Integer wins = user.getGamesWon();
                Integer losses = user.getGamesPlayed() - user.getGamesWon();
                String responseText = "Elo: " + elo.toString() + "\r\n" +
                        "Games played: " + gamesPlayed.toString() + "\r\n" +
                        "Wins: " + wins.toString() + "\r\n" +
                        "Losses: " + losses.toString();
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, responseText);
            }
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User not logged in");
        } catch (RuntimeException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Internal server error");
        }

    }
}
