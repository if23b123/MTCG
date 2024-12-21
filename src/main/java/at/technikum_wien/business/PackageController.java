package at.technikum_wien.business;

import at.technikum_wien.data.DBConnection;

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
