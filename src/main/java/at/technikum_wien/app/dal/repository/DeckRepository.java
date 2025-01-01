package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.Deck;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class DeckRepository {
    private UnitOfWork unitOfWork;
    public DeckRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }
    public DeckRepository(){
        this.unitOfWork = new UnitOfWork();
    }

    public boolean insertUser(String token){
        try{
            String sql = "INSERT INTO decks (user_token) VALUES (?)";
            PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
            int i =0;
            for(int x=1; x<=4; ++x){
                ps.setString(1,token);
                i += ps.executeUpdate();
            }
            if(i==4){
                this.unitOfWork.commitTransaction();
                return true;
            }
            return false;
        } catch (SQLException e) {
            this.unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }
    public Integer getMaxDeckCardID(String token){
        String sql = "SELECT MAX(deck_card_id) FROM decks WHERE user_token = ? AND position IS NULL";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        Integer maxDeckCardId=null;
        try{
            ps.setString(1,token);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                maxDeckCardId = rs.getInt(1);
            }
            return maxDeckCardId;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public boolean insertDeck(String token, Deck deck){
        try{
            String sql = "UPDATE decks SET card_id = ?, position = ? WHERE user_token = ? AND position IS NULL AND deck_card_id = ?";
            PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
            int i = 0;
            int position = 0;

            for(Card card : deck.getCards()){
                ++position;
                ps.setString(1, card.getId());
                ps.setInt(2, position);
                ps.setString(3, token);
                ps.setInt(4, getMaxDeckCardID(token));
                i += ps.executeUpdate();
            }
            if(i==4){
                this.unitOfWork.commitTransaction();
                return true;
            }
            return false;
        }catch(SQLException | RuntimeException e){
            this.unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> getCardIds(String token){
        ArrayList<String> cardIds = new ArrayList<>();
        String sql = "SELECT card_id FROM decks WHERE user_token = ?";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            ps.setString(1,token);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                cardIds.add(rs.getString("card_id"));
            }
            return cardIds;

        }catch(SQLException e){
            this.unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }

    public Integer getMaxPosition(String token){
        String sql = "SELECT MAX(position) FROM decks WHERE user_token = ?";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        Integer maxPosition = 0;
        try{
            ps.setString(1,token);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                maxPosition = rs.getInt(1);
            }
            return maxPosition;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateTokenPosition(String token, String cardId){
        Integer maxPosition = getMaxPosition(token);
        String sql = "UPDATE decks SET position = ?, user_token = ? WHERE card_id = ?";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        int i = 0;
        try {
            ps.setInt(1, maxPosition+1);
            ps.setString(2, token);
            ps.setString(3, cardId);
            i = ps.executeUpdate();
            if(i==1){
                this.unitOfWork.commitTransaction();
                return true;
            }
            return false;
        }catch(SQLException | RuntimeException e){
            this.unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }

    public Integer countDeckByToken(String token){
        String sql = "SELECT COUNT(card_id) FROM decks WHERE user_token = ?";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            ps.setString(1,token);
            Integer count = 0;
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                count = rs.getInt(1);
            }
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updatePosition(String token){
        Integer count = countDeckByToken(token);
        if (count == 0) {
            return false;
        }

        String sql = "UPDATE decks SET position = ? WHERE user_token = ? AND card_id = ?";
        try (PreparedStatement ps = this.unitOfWork.prepareStatement(sql)) {
            int j = 0;

            String selectSql = "SELECT card_id FROM decks WHERE user_token = ? ORDER BY card_id";
            try (PreparedStatement selectPs = this.unitOfWork.prepareStatement(selectSql)) {
                selectPs.setString(1, token);
                ResultSet rs = selectPs.executeQuery();

                while (rs.next()) {
                    ps.setInt(1, j + 1); // Set position to 1, 2, 3, etc.
                    ps.setString(2, token); // user_token
                    ps.setString(3, rs.getString("card_id")); // card_id
                    ps.addBatch();
                    j++;
                }

                if (j > 0) {
                    int[] updateCounts = ps.executeBatch();
                    if (updateCounts.length == j) {
                        this.unitOfWork.commitTransaction();
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            this.unitOfWork.rollbackTransaction();
            throw new RuntimeException("Error updating positions for token: " + token, e);
        }
    }
    public boolean isCardOnTokensDeck(String token, String cardId){
        String sql = "SELECT * FROM decks WHERE user_token = ? AND card_id = ?";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            ps.setString(1,token);
            ps.setString(2, cardId);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return true;
            return false;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


}
