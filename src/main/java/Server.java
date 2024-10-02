import java.util.HashMap;
import java.util.Map;

public class Server {

    private Map<String, User> users;

    // Constructor
    public Server() {
        this.users = new HashMap<String, User>();
    }

    public Map<String, User> getUsers() {
        return users;
    }
}
