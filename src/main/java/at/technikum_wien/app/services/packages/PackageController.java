package at.technikum_wien.app.services.packages;

import at.technikum_wien.app.dal.DBConnection;
import at.technikum_wien.app.dal.repository.PackageRepository;

import java.sql.Connection;

public class PackageController {
    private PackageRepository packageRepository;
    public PackageController() {
        packageRepository = new PackageRepository();
    }

}
