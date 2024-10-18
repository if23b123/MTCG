package at.technikum_wien.data.utils;

import at.technikum_wien.data.http.ContentType;
import at.technikum_wien.data.http.HttpStatus;
import at.technikum_wien.data.server.Request;
import at.technikum_wien.data.server.Response;
import at.technikum_wien.data.server.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private Socket clientSocket;
    private Router router;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    public RequestHandler(Socket clientSocket, Router router) throws IOException {
        this.clientSocket = clientSocket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        this.printWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
        this.router = router;
    }

    @Override
    public void run() {
        try {
            Response response;
            Request request = new RequestBuilder().buildRequest(this.bufferedReader);

            if (request.getPathname() == null) {
                response = new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{\"error\": \"Bad Request\"}"
                );
            } else {
                // Resolve the service based on the request route
                Service service = this.router.resolve(request.getServiceRoute());
                if (service == null) {  // Handle unregistered routes
                    response = new Response(
                            HttpStatus.NOT_FOUND,
                            ContentType.JSON,
                            "{\"error\": \"Route not found\"}"
                    );
                } else {
                    response = service.handleRequest(request);
                }
            }
            printWriter.write(response.get());
        } catch (IOException e) {
            System.err.println(Thread.currentThread().getName() + " Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
