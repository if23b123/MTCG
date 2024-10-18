package at.technikum_wien.business;

import at.technikum_wien.data.http.HttpStatus;
import at.technikum_wien.data.http.ContentType;
import at.technikum_wien.data.server.Request;
import at.technikum_wien.data.server.Response;
import at.technikum_wien.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController{
    private Map<Integer, User> users;

    public UserController() {
        this.users = new HashMap<>();
    }
    private boolean userExists(String username) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    //POST user
    public Response registerUser(Request request) {
        // Parse the request body
        String requestBody = request.getBody();
        Map<String, String> userData = parseJson(requestBody);

        String username = userData.get("Username");
        String password = userData.get("Password");

        if (userExists(username)) {
            // User already exists
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "User already exists");
        }

        // Add new user
        int ID = users.size();
        users.put(ID, new User(ID, username, password));
        return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "User created");
    }

    private Map<String, String> parseJson(String requestBody) {
        Map<String, String> userData = new HashMap<>();
        String[] parts = requestBody.replaceAll("[{}\"]", "").split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            userData.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return userData;
    }
    //GET users
    public Response getUsers() {
        try {
            List<User> userList = new ArrayList<>(users.values());
            ObjectMapper mapper = new ObjectMapper();
            String userDataJSON = mapper.writeValueAsString(userList);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userDataJSON
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    public Response getUserByUsername(String username) {
        User search = null;
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                search=user;
                break;
            }
        }

        if (search != null) {
            try {
                // Convert user to JSON and return the response
                ObjectMapper mapper = new ObjectMapper();
                String userDataJSON = mapper.writeValueAsString(search);
                return new Response(HttpStatus.OK, ContentType.JSON, userDataJSON);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Error processing user data");
            }
        }
        return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User not found");
    }
    public User getUserByUsernameAndPassword(String username, String password) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)&&user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }


}


