package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.Trade;
import at.technikum_wien.app.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class TradingRepository {
    private UnitOfWork unitOfWork;
    public TradingRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }
    public TradingRepository(){
        this.unitOfWork = new UnitOfWork();
    }

    public ArrayList<Trade> selectAll(){
        ArrayList<Trade> deals = new ArrayList<>();
        String sql = "SELECT * FROM trading_deals";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Trade trade = new Trade();
                trade.setId(rs.getString("deal_id"));
                trade.setDealCreatorUsername(rs.getString("creator_username"));
                trade.setCardIdToTrade(rs.getString("card_to_trade"));
                trade.setWantedType(rs.getString("required_type"));
                trade.setMinDamage(rs.getInt("minimum_damage"));
                trade.setTraderUsername(rs.getString("trader_username"));
                trade.setOfferedCardId(rs.getString("offered_card"));
                deals.add(trade);
            }
            return deals;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean createDeal(Trade trade){
        String sql = "INSERT INTO trading_deals (deal_id, creator_username, card_to_trade, required_type, minimum_damage) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            ps.setString(1, trade.getId());
            ps.setString(2, trade.getDealCreatorUsername());
            ps.setString(3, trade.getCardIdToTrade());
            ps.setString(4,trade.getWantedType());
            ps.setInt(5, trade.getMinDamage());
            int rows = ps.executeUpdate();
            if(rows == 1){
                this.unitOfWork.commitTransaction();
                return true;
            }
            return false;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    public boolean deleteDealTokenId(String id, String username){
        String sql = "DELETE FROM trading_deals WHERE deal_id = ? AND creator_username = ?";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            ps.setString(1,id);
            ps.setString(2,username);
            int rs = ps.executeUpdate();
            if(rs == 1){
                this.unitOfWork.commitTransaction();
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Trade getDealById(String id){
        String sql = "SELECT * FROM trading_deals WHERE deal_id = ?";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            Trade trade = new Trade();
            ps.setString(1,id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                trade.setId(rs.getString("deal_id"));
                trade.setDealCreatorUsername(rs.getString("creator_username"));
                trade.setCardIdToTrade(rs.getString("card_to_trade"));
                trade.setWantedType(rs.getString("required_type"));
                trade.setMinDamage(rs.getInt("minimum_damage"));
                trade.setTraderUsername(rs.getString("trader_username"));
                trade.setOfferedCardId(rs.getString("offered_card"));
            }
            return trade;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public boolean insertTrader(String dealId, String cardId, User user){
        Trade trade = getDealById(dealId);
        if(Objects.equals(trade.getDealCreatorUsername(), user.getUsername()) || (trade.getOfferedCardId()!=null && trade.getTraderUsername()!=null)){
                return false;
            }
            String sql = "UPDATE trading_deals SET trader_username = ?, offered_card = ? WHERE deal_id = ?";
            PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
            try{
                ps.setString(1, user.getUsername());
                ps.setString(2,cardId);
                ps.setString(3,dealId);
                int rs = ps.executeUpdate();
                if(rs == 1){
                    this.unitOfWork.commitTransaction();
                    return true;
                }
                return false;
            }catch(SQLException e){
                throw new RuntimeException(e);
            }

    }

    public String getCardIdOfTrade(String dealId){
        String sql = "SELECT card_to_trade FROM trading_deals WHERE deal_id = ?";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            ps.setString(1,dealId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("card_to_trade");
            }
            return null;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
