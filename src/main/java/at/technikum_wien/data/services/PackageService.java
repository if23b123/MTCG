package at.technikum_wien.data.services;

import at.technikum_wien.business.PackageController;
import at.technikum_wien.data.http.ContentType;
import at.technikum_wien.data.http.HttpStatus;
import at.technikum_wien.data.http.Method;
import at.technikum_wien.data.server.Request;
import at.technikum_wien.data.server.Response;
import at.technikum_wien.data.server.Service;

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
