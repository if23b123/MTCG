package at.technikum_wien.data.services;

import at.technikum_wien.business.UserController;
import at.technikum_wien.data.http.ContentType;
import at.technikum_wien.data.http.HttpStatus;
import at.technikum_wien.data.http.Method;
import at.technikum_wien.data.server.Request;
import at.technikum_wien.data.server.Response;
import at.technikum_wien.data.server.Service;
import at.technikum_wien.models.User;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SessionService implements Service {

    private UserController userController;

    public SessionService(UserController userController) {
        this.userController = userController; // Use the same controller for user management
    }

    @Override
    public Response handleRequest(Request request) throws SQLException {
        if (request.getMethod() == Method.POST) {
            return this.userController.loginUser(request);
        }
        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.PLAIN_TEXT, "Method Not Allowed");
    }

}
