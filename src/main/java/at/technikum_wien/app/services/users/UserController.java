package at.technikum_wien.app.services.users;

import at.technikum_wien.app.controller.Controller;
import at.technikum_wien.app.dal.DataAccessException;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.app.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.*;

//to handle prepared statements and return responses

public class UserController extends Controller {
    private UserRepository userRepository;

    public UserController() {
        userRepository = new UserRepository();
    }

    private boolean userExists(String username) {
        try {
            return userRepository.searchUser(username);
        } catch (DataAccessException e) {
            System.err.println("Error occurred while checking user existence: " + e.getMessage());
            e.printStackTrace(); // Optional: Log the full stack trace for debugging
            return false;
        }
    }

    private boolean authorize(String username, Request request) {
        User userToAuthorize = this.userRepository.getByUsername(username);
        if (userToAuthorize.getUuid() != "") {
            return Objects.equals(userToAuthorize.getToken(), request.getHeaderMap().getHeader("Authorization").substring(7));
        }
        return false;
    }

    //POST user
    public Response registerUser(Request request){
        try {
            User postUser = this.getObjectMapper().readValue(request.getBody(), User.class);
            if (userExists(postUser.getUsername())) {
                // User already exists
                return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "User already exists");
            }

            User newUser = new User(postUser.getUsername(), postUser.getPassword());
            boolean add = userRepository.insertUser(newUser);
            if (add) {
                return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "User created");
            }
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "User cannot be created");
        }catch(JsonProcessingException e) {
            e.printStackTrace();
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "Json processing error");
        } catch (SQLException e) {
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "SQL error");
        }
    }

    //GET users
    public Response getUsers() {
        try {
            Collection<User> allUsers = userRepository.getAllUsers();
            String users = "";
            for (User user : allUsers) {
                users += this.getObjectMapper().writeValueAsString(user) + "\r\n";
            }
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    users
            );
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        } catch (JsonProcessingException e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    "Issue processing Json"
            );
        }
    }

    public Response getUserByUsername(Request request, String username) {
        try{
            if (userExists(username)) {
                    User getUser = this.userRepository.getByUsername(username);
                    if(Objects.equals(getUser.getToken(), request.getHeaderMap().getHeader("Authorization").substring(7))){
                        return new Response(HttpStatus.OK, ContentType.JSON, this.getObjectMapper().writeValueAsString(getUser)); //maybe display password w *
                    }
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Attempted unauthorized access");
            }
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User not found");

        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{ \"message\" : \"Internal Server Error\" }");

        }catch (JsonProcessingException e) {

            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Issue processing Json");
        }
    }

    public Response editUserData(String username, Request request) {
        try {
            if (authorize(username, request)) {
                User userToUpdate = this.getObjectMapper().readValue(request.getBody(), User.class);
                userToUpdate.setUsername(username);
                boolean update = this.userRepository.updateNameBioImage(userToUpdate);
                if (update) {
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "User updated");
                }
                return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "User cannot be updated");

            }
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Not authorized");
        } catch (JsonProcessingException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Issue processing Json " );
        } catch (RuntimeException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "SQL Error");
        }
    }

    public Response loginUser(Request request) {
        try{
            User toVerify= this.getObjectMapper().readValue(request.getBody(),User.class);
            // Verify the user exists and the password is correct
            User verify = this.userRepository.getByUsername(toVerify.getUsername());

            if (Objects.equals(verify.getPassword(), toVerify.getPassword())) {

                if(Objects.equals(verify.getToken(), null)){

                    // Generate a token in the format: {username}-mtcgToken
                    String token = toVerify.getUsername() + "-mtcgToken";
                    boolean addTokenToUser = userRepository.updateToken(toVerify, token);

                    if (addTokenToUser) {
                        return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, token);
                    }

                }else{
                    return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "Already logged in");
                }
            }
            // Return unauthorized if the credentials are incorrect
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Invalid credentials for Login");
        }catch(JsonProcessingException e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Issue processing Json");
        } catch (RuntimeException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "SQL Error");
        }
    }
}


