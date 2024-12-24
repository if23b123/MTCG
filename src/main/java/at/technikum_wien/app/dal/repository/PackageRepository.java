package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.models.Card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

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
            String sqlCards = "INSERT INTO cards (id, name, damage, package_id) VALUES (?, ?, ?, ?)";
            PreparedStatement psCard = unitOfWork.prepareStatement(sqlCards);

            for (Card card : cards) {
                psCard.setString(1, card.getId());
                psCard.setString(2, card.getCardName());
                psCard.setDouble(3, card.getDamage());
                psCard.setInt(4, packageID);

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
            throw new RuntimeException(e);
        }

    }
}
