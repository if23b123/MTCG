package at.technikum_wien;

import at.technikum_wien.business.UserController;
import at.technikum_wien.business.PackageController;
import at.technikum_wien.data.services.PackageService;
import at.technikum_wien.data.services.SessionService;
import at.technikum_wien.data.services.UserService;
import at.technikum_wien.data.server.Server;
import at.technikum_wien.data.utils.Router;


public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try{
            server.start();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    private static Router configureRouter()
    {
        Router router = new Router();
        UserController userController = new UserController();
        PackageController packageController = new PackageController();
        router.addService("/users", new UserService(userController));
        router.addService("/sessions", new SessionService(userController));
        router.addService("/packages", new PackageService(packageController));
        return router;
    }
}


