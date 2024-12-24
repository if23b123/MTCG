package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.DataAccessException;
import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class UserRepository {
    private UnitOfWork unitOfWork;

    public UserRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public UserRepository() {
        this.unitOfWork = new UnitOfWork();
    }

    public boolean insertUser(User user) throws SQLException {
        try {
            String sql = "INSERT INTO users (username, password, name, bio, image) VALUES (?, ?, ?, ? ,?)";
            PreparedStatement ps = unitOfWork.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getBio());
            ps.setString(5, user.getImage());

            int rs = ps.executeUpdate();
            if (rs > 0) {
                unitOfWork.commitTransaction(); // Commit the transaction if insert was successful
                return true; // Return true if the user was successfully registered
            } else {
                return false; // Return false if no rows were inserted (user already exists)
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to register new user.", e);
        } catch (Exception e) { // Catch the Exception from AutoCloseable
            throw new DataAccessException("Failed to close UnitOfWork.", e);
        }
    }

    public boolean searchUser(String username) {
        try{
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement ps = unitOfWork.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                unitOfWork.commitTransaction();
                return true;
            }
            return false;
        }
        catch (SQLException e){
            throw new DataAccessException("Failed to search for existing user.", e);
        }
    }

    public Collection<User> getAllUsers(){
        Collection<User> usersFetched = new ArrayList<User>();

        try {
            String sql = "SELECT * FROM users";
            PreparedStatement ps = unitOfWork.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUuid(rs.getString("user_id"));
                user.setUsername(rs.getString("username"));
                user.setName(rs.getString("name"));
                user.setBio(rs.getString("bio"));
                user.setImage(rs.getString("image"));
                user.setElo(rs.getInt("elo"));
                user.setGamesPlayed(rs.getInt("wins")+rs.getInt("losses"));
                user.setGamesWon(rs.getInt("wins"));
                usersFetched.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return usersFetched;
    }

    public User getByUsername(String Username){
        try{
            User searchUser = new User();
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement ps = unitOfWork.prepareStatement(sql);
            ps.setString(1, Username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                searchUser.setUuid(rs.getString("user_id"));
                searchUser.setUsername(rs.getString("username"));
                searchUser.setPassword(rs.getString("password"));
                searchUser.setToken(rs.getString("token"));
                searchUser.setName(rs.getString("name"));
                searchUser.setBio(rs.getString("bio"));
                searchUser.setImage(rs.getString("image"));
                searchUser.setElo(rs.getInt("elo"));
                searchUser.setGamesPlayed(rs.getInt("wins")+rs.getInt("losses"));
                searchUser.setGamesWon(rs.getInt("wins"));
            }
            return searchUser;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public boolean updateToken(User user, String token) {
        try{
            String sql = "UPDATE users SET token = ? WHERE username = ? AND password = ?";
            PreparedStatement ps = unitOfWork.prepareStatement(sql);
            ps.setString(1,token);
            ps.setString(2,user.getUsername());
            ps.setString(3,user.getPassword());
            int rs = ps.executeUpdate();
            if (rs > 0) {
                unitOfWork.commitTransaction();
                return true;
            }
            return false;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public boolean updateNameBioImage(User user){
        try{
            String sql = "UPDATE users SET name = ?,  bio = ?, image = ? WHERE username = ?";
            PreparedStatement ps = unitOfWork.prepareStatement(sql);
            ps.setString(1,user.getName());
            ps.setString(2, user.getBio());
            ps.setString(3,user.getImage());
            ps.setString(4,user.getUsername());
            int rs = ps.executeUpdate();
            if (rs > 0) {
                unitOfWork.commitTransaction();
                return true;
            }
            return false;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
