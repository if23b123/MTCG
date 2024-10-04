package at.technikum_wien;

import java.util.HashMap;
import java.util.Map;

public class Server {

    private Map<Integer, User> users;

    // Constructor
    public Server() {
        this.users = new HashMap<Integer, User>();
    }

    public Map<Integer, User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }
}
