package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.Card;
import at.technikum_wien.app.models.Deck;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            this.unitOfWork.rollbackTransaction();
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
                System.out.println(card.getId() + " " + position + " " + token + " " + getMaxDeckCardID(token));
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
            this.unitOfWork.rollbackTransaction();
            return false;
        }catch(SQLException | RuntimeException e){
            this.unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }


}
