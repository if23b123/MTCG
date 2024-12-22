package at.technikum_wien.app.services.packages;

import at.technikum_wien.app.dal.DBConnection;

import java.sql.Connection;

public class PackageController {
    private Connection connection;
    public PackageController() {
        connection = DBConnection.getConnection();
        if (connection == null) {
            connection=DBConnection.connect();
        }
    }

}
