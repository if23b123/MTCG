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
}
