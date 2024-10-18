package at.technikum_wien.data.services;

import at.technikum_wien.business.UserController;
import at.technikum_wien.data.http.ContentType;
import at.technikum_wien.data.http.HttpStatus;
import at.technikum_wien.data.http.Method;
import at.technikum_wien.data.server.Request;
import at.technikum_wien.data.server.Response;
import at.technikum_wien.data.server.Service;
import at.technikum_wien.models.User;

import java.util.HashMap;
import java.util.Map;

public class SessionService implements Service {

    private UserController userController;
    private Map<String, Integer> sessionTokens;  // Map token to user ID

    public SessionService(UserController userController) {
        this.userController = userController; // Use the same controller for user management
        this.sessionTokens = new HashMap<>();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            return loginUser(request);
        }
        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.PLAIN_TEXT, "Method Not Allowed");
    }

    // POST /sessions for login
    private Response loginUser(Request request) {
        // Parse the login data
        String requestBody = request.getBody();
        Map<String, String> loginData = parseJson(requestBody);

        String username = loginData.get("Username");
        String password = loginData.get("Password");

        // Verify the user exists and the password is correct
        User user = userController.getUserByUsernameAndPassword(username, password);
        if (user != null) {
            // Generate a token in the format: {username}-mtcgToken
            String token = username + "-mtcgToken";
            sessionTokens.put(token, user.getId());  // Map token to user ID

            // Return the token in the response
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, token);
        }

        // Return unauthorized if the credentials are incorrect
        return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Invalid credentials");
    }

    // Authentication checks
    public boolean isAuthenticated(String token) {
        return sessionTokens.containsKey(token);
    }

    // Parse JSON from request
    private Map<String, String> parseJson(String requestBody) {
        Map<String, String> data = new HashMap<>();
        String[] parts = requestBody.replaceAll("[{}\"]", "").split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            data.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return data;
    }
}
