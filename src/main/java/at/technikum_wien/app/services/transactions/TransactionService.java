package at.technikum_wien.app.services.transactions;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;

import java.io.IOException;
import java.sql.SQLException;

public class TransactionService implements Service {
    private TransactionController controller;

    public TransactionService(TransactionController controller) {
        this.controller = controller;
    }

    public TransactionService() {
        controller = new TransactionController();
    }


    @Override
    public Response handleRequest(Request request) throws IOException, SQLException{
        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.PLAIN_TEXT, "Method not implemented");
    }
}
