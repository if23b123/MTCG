package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.Card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class PackageRepository {
    private UnitOfWork unitOfWork;
    public PackageRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }
    public PackageRepository() {
        this.unitOfWork = new UnitOfWork();
    }

    public boolean insertPackage(Collection<Card> cards) {
        try {
            // Step 1: Insert into the packages table
            String sqlPackages = "INSERT INTO packages (name) VALUES (?)";
            PreparedStatement psPackage = unitOfWork.prepareStatement(sqlPackages);
            psPackage.setString(1, "admin");
            // Execute the insert statement for packages
            psPackage.executeUpdate();

            // Step 2: Retrieve the MAX(package_id) from the packages table, in order to add the package_id to each card
            String sqlPackageID = "SELECT MAX(packages_id) FROM packages WHERE name = ?";
            PreparedStatement psPackageID = unitOfWork.prepareStatement(sqlPackageID);
            psPackageID.setString(1, "admin");
            ResultSet rsPackageID = psPackageID.executeQuery();

            int packageID = 0;
            if (rsPackageID.next()) {
                packageID = rsPackageID.getInt(1); // Get the maximum package_id from the result set
            }

            // Step 3: Insert into the cards table for each card in the list
            String sqlCards = "INSERT INTO cards (id, name, damage, package_id, type, element) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psCard = unitOfWork.prepareStatement(sqlCards);

            for (Card card : cards) {
                psCard.setString(1, card.getId());
                psCard.setString(2, card.getCardName());
                psCard.setDouble(3, card.getDamage());
                psCard.setInt(4, packageID);
                psCard.setString(5, card.getType());
                psCard.setString(6, card.getElement());

                // Execute the insert for each card
                psCard.addBatch(); // Add to batch for efficiency
            }

            // Execute batch insert for cards
            int[] rs = psCard.executeBatch();
            boolean allInserted = true;

            // Check if all cards were inserted successfully
            for (int i : rs) {
                if (i == PreparedStatement.EXECUTE_FAILED) { //if i == -3
                    allInserted = false;
                    break;
                }
            }
            unitOfWork.commitTransaction();

            return allInserted;

        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }

    }

    public Collection<Integer> getPackageId(String token){
        String sql = "SELECT packages_id FROM packages WHERE acquired_by = ?";
        Collection<Integer> packageIds = new ArrayList<>();
        try(PreparedStatement ps = this.unitOfWork.prepareStatement(sql)){
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                packageIds.add(rs.getInt("packages_id"));
            }
            return packageIds;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Integer allPackagesOfToken(ArrayList<Integer> packagesToCheck, String token){
        String sql = "SELECT acquired_by FROM packages WHERE packages_id= ?";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            for(Integer packageId : packagesToCheck){
                ps.setInt(1,packageId);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    if(!Objects.equals(rs.getString("acquired_by"), token)){
                        return packageId;
                    }
                }
            }
            return null;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Integer getMaxPackageByToken(String token){
        String sql = "SELECT max(packages_id) FROM packages WHERE acquired_by = ?";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String getAcquiredByById(Integer packageId){
        String sql = "SELECT acquired_by FROM packages WHERE packages_id = ?";
        PreparedStatement ps = this.unitOfWork.prepareStatement(sql);
        try{
            ps.setInt(1,packageId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("acquired_by");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
