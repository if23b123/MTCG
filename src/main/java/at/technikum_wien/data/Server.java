package at.technikum_wien.data;

import at.technikum_wien.models.User;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Getter
public class Server {

    private Map<Integer, User> users;

    // Constructor
    public Server() {
        this.users = new HashMap<Integer, User>();
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public boolean register() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Type in your Username: ");
        //username=sc.nextLine();
        System.out.println("Type in your Password: ");
        //password=sc.nextLine();
        return true; // Registration successful
    }

    public boolean login(String username, String password) {
        //LogIn Logic - check w PostgreSQL database

        System.out.println("Invalid credentials!");
        return false;
    }
}
