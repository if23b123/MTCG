package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.DataAccessException;
import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
            String sql = "INSERT INTO users (username, password, name, bio, image, coins) VALUES (?, ?, ?, ? ,?, ?)";
            PreparedStatement ps = unitOfWork.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getBio());
            ps.setString(5, user.getImage());
            ps.setInt(6, user.getCoins());

            int rs = ps.executeUpdate();
            if (rs > 0) {
                unitOfWork.commitTransaction(); // Commit the transaction if insert was successful
                return true; // Return true if the user was successfully registered
            }
            return false; // Return false if no rows were inserted (user already exists)

        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to register new user.", e);
        } catch (Exception e) { // Catch the Exception from AutoCloseable
            unitOfWork.rollbackTransaction();
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
                return true;
            }
            return false;
        }
        catch (SQLException e){
            throw new DataAccessException("Failed to search for existing user.", e);
        }
    }

    public ArrayList<User> getAllUsers(){
        ArrayList<User> usersFetched = new ArrayList<>();

        try {
            String sql = "SELECT * FROM users";
            PreparedStatement ps = unitOfWork.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUuid(rs.getString("user_id"));
                user.setUsername(rs.getString("username"));
                user.setName(rs.getString("name"));
                user.setToken(rs.getString("token"));
                user.setBio(rs.getString("bio"));
                user.setImage(rs.getString("image"));
                user.setElo(rs.getInt("elo"));
                user.setGamesPlayed(rs.getInt("wins")+rs.getInt("losses"));
                user.setGamesWon(rs.getInt("wins"));
                user.setCoins(rs.getInt("coins"));
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
                searchUser.setCoins(rs.getInt("coins"));
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
            unitOfWork.rollbackTransaction();
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
            unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }

    public boolean searchToken(String token){
        try{
            String sql = "SELECT * FROM users WHERE token = ?";
            PreparedStatement ps = unitOfWork.prepareStatement(sql);
            ps.setString(1,token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public int getCoins(String token){
        String sqlGetCoins = "SELECT coins FROM users WHERE token = ?";
        try(PreparedStatement ps = unitOfWork.prepareStatement(sqlGetCoins)){
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("coins");
            }else {
                throw new DataAccessException("Token not found: " + token);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void updateCoins(String token, int coins) {
        String sqlUpdateCoins = "UPDATE users SET coins = ? WHERE token = ?";

            try (PreparedStatement ps2 = unitOfWork.prepareStatement(sqlUpdateCoins)) {
                ps2.setInt(1,  coins - 5);
                ps2.setString(2, token);

                int rowsAffected = ps2.executeUpdate();
                if (rowsAffected > 0) {
                    unitOfWork.commitTransaction();
                }
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }

    public User getUserByToken(String token){
        try{
            String sql = "SELECT * FROM users WHERE token = ?";
            PreparedStatement ps = unitOfWork.prepareStatement(sql);
            ps.setString(1,token);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUuid(rs.getString("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setToken(rs.getString("token"));
                user.setName(rs.getString("name"));
                user.setBio(rs.getString("bio"));
                user.setImage(rs.getString("image"));
                user.setElo(rs.getInt("elo"));
                user.setGamesPlayed(rs.getInt("wins") + rs.getInt("losses"));
                user.setGamesWon(rs.getInt("wins"));
                user.setCoins(rs.getInt("coins"));
                return user;
            }else{
                throw new DataAccessException("User not found for token: " + token);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void updateWins(Integer elo, String token){
        String sql = "UPDATE users SET wins = wins + ?, elo = ? WHERE token = ?";
        PreparedStatement ps = unitOfWork.prepareStatement(sql);
        try{
            ps.setInt(1,1);
            ps.setInt(2,elo+3);
            ps.setString(3,token);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                unitOfWork.commitTransaction();
            } else{
                throw new DataAccessException("User not found for token: " + token);
            }
        }catch(SQLException e){
            unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }
    public void updateLosses(Integer elo, String token){
        String sql = "UPDATE users SET losses = losses + ?, elo = ? WHERE token = ?";
        PreparedStatement ps = unitOfWork.prepareStatement(sql);
        try{
            ps.setInt(1,1);
            ps.setInt(2,elo-5);
            ps.setString(3,token);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                unitOfWork.commitTransaction();
            }else{
                throw new DataAccessException("User not found for token: " + token);
            }
        }catch(SQLException e){
            unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }





}

