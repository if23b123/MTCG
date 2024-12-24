package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;

public class PackageRepository {
    private UnitOfWork unitOfWork;
    public PackageRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }
    public PackageRepository() {
        this.unitOfWork = new UnitOfWork();
    }
}
