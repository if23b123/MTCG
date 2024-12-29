package at.technikum_wien.app.services.battles;

import at.technikum_wien.app.dal.repository.BattleRepository;

public class BattleController {
    private BattleRepository battleRepo;
    public BattleController() {
        battleRepo = new BattleRepository();
    }
}
