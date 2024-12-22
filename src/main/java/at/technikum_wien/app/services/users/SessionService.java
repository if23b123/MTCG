package at.technikum_wien.app.services.users;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;

import java.sql.SQLException;

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
