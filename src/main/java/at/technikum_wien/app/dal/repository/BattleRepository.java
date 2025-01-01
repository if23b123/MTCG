package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.User;
import at.technikum_wien.app.services.battles.BattleController;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BattleRepository {
    private UnitOfWork unitOfWork;
    public BattleRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }
    public BattleRepository() {
        this.unitOfWork = new UnitOfWork();
    }
    public boolean insertBattleStats(User player1, User player2, String winnerToken, String battleLog){
        String sql = "INSERT INTO battles (user1_token,user2_token,winner_token,battle_log) VALUES (?,?,?,?)";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            ps.setString(1,player1.getToken());
            ps.setString(2,player2.getToken());
            ps.setString(3, winnerToken);
            ps.setString(4,battleLog);
            int rs = ps.executeUpdate();
            if(rs > 0){
                this.unitOfWork.commitTransaction();
                return true;
            }
            return false;

        }catch(SQLException e){
            this.unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }
    public boolean insertBattleStatsDraw(User player1, User player2, String battleLog){
        String sql = "INSERT INTO battles (user1_token,user2_token,battle_log) VALUES (?,?,?)";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            ps.setString(1,player1.getToken());
            ps.setString(2,player2.getToken());
            ps.setString(3,battleLog);
            int rs = ps.executeUpdate();
            if(rs > 0){
                this.unitOfWork.commitTransaction();
                return true;
            }
            return false;

        }catch(SQLException e){
            this.unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }
}
