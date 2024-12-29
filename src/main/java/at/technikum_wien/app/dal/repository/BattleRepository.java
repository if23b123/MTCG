package at.technikum_wien.app.dal.repository;

import at.technikum_wien.app.dal.UnitOfWork;
import at.technikum_wien.app.services.battles.BattleController;

public class BattleRepository {
    private UnitOfWork unitOfWork;
    public BattleRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }
    public BattleRepository() {
        this.unitOfWork = new UnitOfWork();
    }
}
