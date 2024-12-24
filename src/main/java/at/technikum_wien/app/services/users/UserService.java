package at.technikum_wien.app.services.users;

import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;

import java.sql.SQLException;
//to handle the type of request
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
                return this.userController.getUserByUsername(request, username);
            }
            return this.userController.getUsers();
        }else if(request.getMethod()==Method.PUT){
            if (request.getPathParts().size() > 1) {
                String username = request.getPathParts().get(1);
                return this.userController.editUserData(username, request);

            }
        }
        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.PLAIN_TEXT, "Method Not Allowed");
    }


}

