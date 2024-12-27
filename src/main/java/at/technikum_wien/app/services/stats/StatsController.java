package at.technikum_wien.app.services.stats;

import at.technikum_wien.app.controller.Controller;
import at.technikum_wien.app.dal.repository.UserRepository;

public class StatsController extends Controller {
    UserRepository userRepository = new UserRepository();
    public StatsController() {
        userRepository = new UserRepository();
    }
}
