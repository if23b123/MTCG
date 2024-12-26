package at.technikum_wien.app.services.packages;

import at.technikum_wien.app.controller.Controller;
import at.technikum_wien.app.dal.repository.PackageRepository;
import at.technikum_wien.app.dal.repository.UserRepository;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.httpserver.http.ContentType;
import at.technikum_wien.httpserver.http.HttpStatus;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.server.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collection;
import java.util.Objects;


public class PackageController extends Controller {
    private PackageRepository packageRepository;
    private UserRepository userRepository;
    public PackageController() {
        packageRepository = new PackageRepository();
        userRepository = new UserRepository();
    }

    public Response createPackage(Request request) {
        try{
            String Token = request.getHeaderMap().getHeader("Authorization").substring(7);
            if(this.userRepository.searchToken(Token) && Token.equals("admin-mtcgToken")){
                Collection<Card> cardToPackage = this.getObjectMapper().readValue(request.getBody(),new TypeReference<>(){});
                boolean created = this.packageRepository.insertPackage(cardToPackage);
                if(created){
                    return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Package created");
                }
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Package creation failed");
            }
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized");
        }catch(JsonProcessingException e){
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Issue processing Json");
        }catch(RuntimeException e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "SQL Error");
        }
    }
}
