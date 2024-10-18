package at.technikum_wien.data.server;

import java.io.IOException;

public interface Service {
    public Response handleRequest(Request request) throws IOException;
}
