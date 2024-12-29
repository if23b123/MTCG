package at.technikum_wien.app.services.scoreboard;

import at.technikum_wien.app.controller.Controller;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.User;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreboardController extends Controller {
    UserRepository repo;
    public ScoreboardController() {
        repo = new UserRepository();
    }

    public Response getScoreBoard(Request request) {
        try{
            if(this.repo.searchToken(request.getHeaderMap().getHeader("Authorization").substring(7))){
                ArrayList<User> allUsers = repo.getAllUsers();
                allUsers.sort((user1, user2) -> Integer.compare(user2.getElo(), user1.getElo()));
                String scoreBoard = "Scoreboard: \r\n";
                for(User user : allUsers){
                    if(user.getToken() != null){
                        scoreBoard += user.getUsername() + " - " + user.getElo() + "\r\n";
                    }
                }

                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, scoreBoard);
            }
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User not logged in");

        } catch (RuntimeException e) {
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "SQL Error");
        }
    }


}
