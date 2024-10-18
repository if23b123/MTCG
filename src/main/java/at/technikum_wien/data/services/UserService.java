package at.technikum_wien.data.services;

import at.technikum_wien.business.UserController;
import at.technikum_wien.data.http.Method;
import at.technikum_wien.data.http.HttpStatus;
import at.technikum_wien.data.http.ContentType;
import at.technikum_wien.data.server.Request;
import at.technikum_wien.data.server.Response;
import at.technikum_wien.data.server.Service;

import java.io.IOException;

public class UserService implements Service{
    private UserController userController;

    public UserService(UserController userController) {
        this.userController = userController; // In-memory user storage
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            return this.userController.registerUser(request);
        } else if(request.getMethod() == Method.GET) {
            if (request.getPathParts().size() > 1) { // "/users/{username}"
                String username = request.getPathParts().get(1); // Extract username from URL
                return this.userController.getUserByUsername(username);
            }
            return this.userController.getUsers();
        }
            return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.PLAIN_TEXT, "Method Not Allowed");
    }


}

