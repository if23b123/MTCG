package at.technikum_wien.httpserver.server;

import at.technikum_wien.httpserver.utils.RequestHandler;
import at.technikum_wien.httpserver.utils.Router;
import at.technikum_wien.app.dal.DBConnection;
import lombok.Getter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class Server {
    private int port;
    private Router router;

    public Server(int port, Router router) {
        this.port = port;
        this.router = router;
    }
    public void start() throws IOException {

        Connection myCon = DBConnection.connect();

        final ExecutorService executorService = Executors.newFixedThreadPool(10);

        System.out.println("Start http-server...");
        System.out.println("http-server running at: http://localhost:" + this.port);

        try(ServerSocket serverSocket = new ServerSocket(this.port)) {
            while(true) {
                final Socket clientConnection = serverSocket.accept();
                final RequestHandler socketHandler = new RequestHandler(clientConnection, this.router);
                executorService.submit(socketHandler);
            }
        } finally {
            DBConnection.close();
        }
    }

}
