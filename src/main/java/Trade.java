public class Trade {
    private Card offerCard;
    private String condition;  // e.g., "Spell with min damage 70"
    private User offeringPlayer;

    public Trade(Card offerCard, String condition, User offeringPlayer) {
        this.offerCard = offerCard;
        this.condition = condition;
        this.offeringPlayer = offeringPlayer;
    }
    private boolean matchesCondition(Card requestCard) {
        return true;
    }
}
