package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.Card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class CardRepository {
    private UnitOfWork unitOfWork;
    public CardRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }
    public CardRepository() {
        this.unitOfWork = new UnitOfWork();
    }

    public Collection<Card> selectAllAcquiredCards(Integer packageId){
        String sql = "SELECT id, name, element, type, damage FROM cards WHERE package_id = ?";
        Collection<Card> cards = new ArrayList<Card>();
        try(PreparedStatement ps = this.unitOfWork.prepareStatement(sql)){
            ps.setInt(1, packageId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Card card = new Card();
                card.setId(rs.getString("id"));
                card.setCardName(rs.getString("name"));
                card.setElement(rs.getString("element"));
                card.setType(rs.getString("type"));
                card.setDamage(rs.getInt("damage"));
                cards.add(card);
            }
            this.unitOfWork.commitTransaction();
            return cards;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Card> getCardsById(Collection<String> cardsIds) {
        ArrayList<Card> cardsById = new ArrayList<>();

        String sql = "SELECT * FROM cards WHERE id = ?";
        try (PreparedStatement ps = this.unitOfWork.prepareStatement(sql)) {
            for (String id : cardsIds) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Card retrievedCard = new Card();
                        retrievedCard.setId(rs.getString("id"));
                        retrievedCard.setCardName(rs.getString("name"));
                        retrievedCard.setDamage(rs.getInt("damage"));
                        retrievedCard.setPackage_id(rs.getInt("package_id"));
                        retrievedCard.setType(rs.getString("type"));
                        retrievedCard.setElement(rs.getString("element"));
                        cardsById.add(retrievedCard);
                    }
                }
            }
            unitOfWork.commitTransaction();
            return cardsById;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException("Failed to fetch cards by ID", e);
        }
    }

    public boolean updatePackageId(String cardId, Integer packageId) {
        String sql = "UPDATE cards SET package_id = ? WHERE id = ?";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            ps.setInt(1,packageId);
            ps.setString(2,cardId);
            int rs = ps.executeUpdate();
            if(rs > 0){
                unitOfWork.commitTransaction();
                return true;
            }

            return false;
        }catch(SQLException e){
            this.unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }


}
