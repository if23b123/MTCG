package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.DataAccessException;
import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionRepository {
    private UnitOfWork unitOfWork;
    private UserRepository userRepository;
    public TransactionRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
        this.userRepository = new UserRepository(unitOfWork);
    }
    public TransactionRepository(){
        this.unitOfWork = new UnitOfWork();
        this.userRepository = new UserRepository(unitOfWork);
    }

    public boolean postAcquiredBy(String token){
        try{
            String sqlRandomPackage = "SELECT packages_id FROM packages WHERE acquired_by IS NULL ORDER BY RANDOM() LIMIT 1";
            PreparedStatement ps = this.unitOfWork.prepareStatement(sqlRandomPackage);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int packageId = rs.getInt("packages_id");
                String sql = "UPDATE packages SET acquired_by = ? WHERE packages_id = ?";
                PreparedStatement ps1 = this.unitOfWork.prepareStatement(sql);
                ps1.setString(1, token);
                ps1.setInt(2, packageId);
                int rs1 = ps1.executeUpdate();
                if(rs1 > 0){
                    unitOfWork.commitTransaction();
                    return true;
                }
                return false;
            }
            return false;
        }catch(RuntimeException e){
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Issue retrieving user from db" + e.getMessage(), e);
        }catch(SQLException e){
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Insert token into packages", e);

        }
    }
}
