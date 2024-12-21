package at.technikum_wien.business;

import at.technikum_wien.data.DBConnection;
import at.technikum_wien.data.http.HttpStatus;
import at.technikum_wien.data.http.ContentType;
import at.technikum_wien.data.server.Request;
import at.technikum_wien.data.server.Response;
import at.technikum_wien.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserController{
    private Map<Integer, User> users;
    private Connection connection;

    public UserController() {
        connection = DBConnection.getConnection();
        if (connection == null) {
            connection=DBConnection.connect();
        }
    }
    private boolean userExists(String username) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }

        return false;
    }
    //POST user
    public Response registerUser(Request request) throws SQLException {
        // Parse the request body
        String requestBody = request.getBody();
        Map<String, String> userData = parseJson(requestBody);

        String username = userData.get("Username");
        String password = userData.get("Password");

        if (userExists(username)) {
            // User already exists
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "User already exists");
        }

        // Add new user
        PreparedStatement ps = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
        ps.setString(1, username);
        ps.setString(2, password);
        int rs = ps.executeUpdate();

        return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "User created");
    }

    private Map<String, String> parseJson(String requestBody) {
        Map<String, String> userData = new HashMap<>();

        // Remove surrounding braces and quotes
        String cleanedRequestBody = requestBody.trim().replaceAll("^[{]|[}]$", "");

        // Split by commas to separate key-value pairs
        String[] keyValuePairs = cleanedRequestBody.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for (String pair : keyValuePairs) {
            // Split key-value pair by the first colon
            String[] keyValue = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);

            if (keyValue.length == 2) {
                // Remove any extra quotes from keys and values
                String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
                String value = keyValue[1].trim().replaceAll("^\"|\"$", "");
                userData.put(key, value);
            }
        }
        return userData;
    }

    //GET users
    public Response getUsers() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users");
            ResultSet rs = ps.executeQuery();
            StringBuilder userDataJSON = new StringBuilder();

            while(rs.next()) {
                String uuid = rs.getString("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                Integer elo = rs.getInt("elo");
                Integer wins = rs.getInt("wins");
                Integer loss = rs.getInt("losses");

                userDataJSON.append(username).append(" (username) - ").append(elo).append(" (elo) - ").append(wins).append(" (wins) - ").append(loss).append(" (loss)").append("\r\n");
            }
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userDataJSON.toString()
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    public Response getUserByUsername(Request request, String username) throws SQLException {
        if (userExists(username)) {
            if(authorize(username, request)){
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
                ps.setString(1, username);
                ResultSet search = ps.executeQuery();
                StringBuilder userDataJSON = new StringBuilder();

                while (search.next()) {
                    String uuid = search.getString("user_id");
                    String username1 = search.getString("username");
                    String name = search.getString("name");
                    String bio = search.getString("bio");
                    String image = search.getString("image");
                    String password = search.getString("password");
                    Integer elo = search.getInt("elo");
                    Integer wins = search.getInt("wins");
                    Integer loss = search.getInt("losses");

                    userDataJSON.append(username).append(" (username) - ").append(name).append(" (name) - ").append(bio).append(" (bio) - ").append(image).append(" (image) - ").
                            append(elo).append(" (elo) - ").append(wins).append(" (wins) - ").append(loss).append(" (loss)").append("\r\n");

                    return new Response(HttpStatus.OK, ContentType.JSON, userDataJSON.toString());
                }
            }else{
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Attempted unauthorized access");
            }
        }
        return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User not found");
    }
    public boolean getUserByUsernameAndPassword(String username, String password) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            return true;
        }
        return false;
    }
    public boolean pushToken(String username, String password, String token) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE users SET token = ? WHERE username = ? AND password = ?");
        ps.setString(1, token);
        ps.setString(2, username);
        ps.setString(3, password);
        int rs = ps.executeUpdate();
        if(rs!=0)
            return true;
        return false;

    }

    public Response editUserData(String username, Request request) throws SQLException {
        if(authorize(username,request)) {
            if(userExists(username)){
                String requestBody = request.getBody();
                Map<String, String> userData = parseJson(requestBody);

                String name = userData.get("Name");
                String bio = userData.get("Bio");
                String image = userData.get("Image");


                PreparedStatement ps = connection.prepareStatement("UPDATE users SET name = ?,  bio = ?, image = ? WHERE username = ?");
                ps.setString(1, name);
                ps.setString(2, bio);
                ps.setString(3, image);
                ps.setString(4, username);
                int rs = ps.executeUpdate();

                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "User updated");
            }
        }
        return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Not authorized");
    }
    private boolean authorize(String username, Request request) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
        ps.setString(1, username);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            String token = result.getString("token");
            return Objects.equals(token, request.getHeaderMap().getHeader("Authorization").substring(7));
        }
        return false;
    }

    public Response loginUser(Request request) throws SQLException {
        // Parse the login data
        String requestBody = request.getBody();
        Map<String, String> loginData = parseJson(requestBody);

        String username = loginData.get("Username");
        String password = loginData.get("Password");

        // Verify the user exists and the password is correct
        boolean doesUserExist = getUserByUsernameAndPassword(username, password);
        if (doesUserExist) {
            // Generate a token in the format: {username}-mtcgToken
            String token = username + "-mtcgToken";
            boolean addTokenToUser = pushToken(username, password, token);
            if (addTokenToUser) {
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, token);
            }
        }
        // Return unauthorized if the credentials are incorrect
        return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Invalid credentials for Login");
    }
}


