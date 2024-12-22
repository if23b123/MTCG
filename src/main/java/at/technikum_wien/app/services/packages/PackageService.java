package at.technikum_wien.app.services.packages;

import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.http.Method;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import at.technikum_wien.httpserver.server.Service;

public class PackageService implements Service {
    private PackageController controller;
    public PackageService(PackageController controller) {
        this.controller = controller;
    }

    public Response handleRequest(Request request) {
        if(request.getMethod()== Method.POST){

        }
        return new Response(HttpStatus.NOT_IMPLEMENTED, ContentType.PLAIN_TEXT, "Method Not Allowed");
    }
}
