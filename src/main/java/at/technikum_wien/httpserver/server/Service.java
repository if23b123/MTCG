package at.technikum_wien.httpserver.server;

import java.io.IOException;
import java.sql.SQLException;

public interface Service {
    public Response handleRequest(Request request) throws IOException, SQLException;
}
