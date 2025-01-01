package at.technikum_wien.app.business;

import at.technikum_wien.app.models.Card;
import lombok.Setter;
import lombok.Getter;

public class BattleLogic {
    @Getter @Setter
    private String battleLog = "";
    public Card pureMonsterCardFight(Card card1, Card card2) {
        battleLog+="\r\n";
        if(card1.getCardName().contains("Goblin") && card2.getCardName().contains("Dragon")){
            battleLog+="Goblin is trembling in fear at the sight of the Dragon!" + "\r\n" + "Goblin gives up on his attack" + "\r\n" + "Dragon wins! Goblin is defeated" + "\r\n";
            return card2;
        }else if(card1.getCardName().contains("Dragon") && card2.getCardName().contains("Goblin")){
            battleLog+="Goblin is trembling in fear at the sight of the Dragon!" + "\r\n" + "Goblin gives up on his attack" + "\r\n" + "Dragon wins! Goblin is defeated"+ "\r\n";
            return card1;
        }else if(card1.getCardName().contains("Wizzard") && card2.getCardName().contains("Ork")){
            battleLog+="Wizzard casts a magic spell on Ork" + "\r\n" + "Ork tries to attack, but fails!" + "\r\n" + "Wizzard wins! Ork is defeated"+ "\r\n";
            return card1;
        }else if(card1.getCardName().contains("Ork") && card2.getCardName().contains("Wizzard")){
            battleLog+="Wizzard casts a magic spell on Ork" + "\r\n" + "Ork tries to attack, but fails!" + "\r\n" + "Wizzard wins! Ork is defeated"+ "\r\n";
            return card2;
        }else if(card1.getCardName().contains("FireElf") && card2.getCardName().contains("Dragon")){
            battleLog+="The Dragon starts its attack, but the FireElf expertly evade the dragon's fiery onslaughts." + "\r\n" + "FireElf wins! Dragon is defeated"+ "\r\n";
            return card1;
        }else if(card1.getCardName().contains("Dragon") && card2.getCardName().contains("FireElf")){
            battleLog+="The Dragon starts its attack, but the FireElf expertly evade the dragon's fiery onslaughts." + "\r\n" + "FireElf wins! Dragon is defeated"+ "\r\n";
            return card2;
        }
        if(card1.getDamage()<card2.getDamage()){
            battleLog+= card1.getCardName() + "starts his attack, but out of nowhere is met with the " + card2.getCardName() +
                    "'s powerful force." + "\r\n" + card2.getCardName() + " wins! " + card1.getCardName() + " is defeated"+ "\r\n";
            return card2;
        } else if (card1.getDamage()>card2.getDamage()) {
            battleLog+= card2.getCardName() + "starts his attack, but out of nowhere is met with the " + card1.getCardName() +
                    "'s powerful force." + "\r\n" + card1.getCardName() + " wins! " + card2.getCardName() + " is defeated"+ "\r\n";
            return card1;
        }
        battleLog+="The " + card1.getCardName() + " and " + card2.getCardName() + " clash fiercely, their attacks exchanging blows with neither side gaining the upper hand. After a brutal battle, both the " +
        card1.getCardName() + " and " + card2.getCardName() + " retreat, exhausted and wounded" + "\r\n" + "Draw"+ "\r\n";
        return null;
    }

    public Card pureSpellCardFight(Card cardPlayer1, Card cardPlayer2) {
        battleLog+="\r\n";

        double adjustedDamage1 = cardPlayer1.getDamage();
        double adjustedDamage2 = cardPlayer2.getDamage();



        if (isEffective(cardPlayer1.getElement(), cardPlayer2.getElement())) {
            adjustedDamage1 *= 2;
            battleLog += cardPlayer1.getCardName() + "'s " + cardPlayer1.getElement() + " spell is super effective against " + cardPlayer2.getCardName() + "! Damage is doubled." + "\r\n";
        } else if (isNotEffective(cardPlayer1.getElement(), cardPlayer2.getElement())) {
            adjustedDamage1 /= 2;
            battleLog += cardPlayer1.getCardName() + "'s " + cardPlayer1.getElement() + " spell is not very effective against " + cardPlayer2.getCardName() + ". Damage is halved." + "\r\n";
        }

        if (isEffective(cardPlayer2.getElement(), cardPlayer1.getElement())) {
            adjustedDamage2 *= 2;
            battleLog += cardPlayer2.getCardName() + "'s " + cardPlayer2.getElement() + " spell is super effective against " + cardPlayer1.getCardName() + "! Damage is doubled." + "\r\n";
        } else if (isNotEffective(cardPlayer2.getElement(), cardPlayer1.getElement())) {
            adjustedDamage2 /= 2;
            battleLog += cardPlayer2.getCardName() + "'s " + cardPlayer2.getElement() + " spell is not very effective against " + cardPlayer1.getCardName() + ". Damage is halved." + "\r\n";
        }

        if (adjustedDamage1 > adjustedDamage2) {
            battleLog += cardPlayer1.getCardName() + " casts a powerful " + cardPlayer1.getElement() + " spell that overwhelms " + cardPlayer2.getCardName() + "'s defense!" + "\r\n" + cardPlayer1.getCardName() + " wins! " + cardPlayer2.getCardName() + " is defeated" + "\r\n";
            return cardPlayer1;
        } else if (adjustedDamage2 > adjustedDamage1) {
            battleLog += cardPlayer2.getCardName() + " casts a powerful " + cardPlayer2.getElement() + " spell that overwhelms " + cardPlayer1.getCardName() + "'s defense!" + "\r\n" + cardPlayer2.getCardName() + " wins! " + cardPlayer1.getCardName() + " is defeated" + "\r\n";
            return cardPlayer2;
        } else {
            battleLog += "Both " + cardPlayer1.getCardName() + " and " + cardPlayer2.getCardName() + " cast their spells, but neither gains the upper hand." + "\r\n" + "After an intense magical exchange, both retreat, exhausted and wounded" + "\r\n" + "Draw" + "\r\n";
            return null;
        }
    }


    private boolean isEffective(String element1, String element2) {
        return (element1.equals("water") && element2.equals("fire")) ||
                (element1.equals("fire") && element2.equals("normal")) ||
                (element1.equals("normal") && element2.equals("water"));
    }

    private boolean isNotEffective(String element1, String element2) {
        return (element1.equals("water") && element2.equals("normal")) ||
                (element1.equals("fire") && element2.equals("water")) ||
                (element1.equals("normal") && element2.equals("fire"));
    }

    public Card mixTypeFight(Card cardPlayer1, Card cardPlayer2) {
        battleLog+="\r\n";

        if (isKraken(cardPlayer1)) {
            battleLog += cardPlayer1.getCardName() + " is immune to all spells and attacks! " + cardPlayer2.getCardName() + " can't touch it." + "\r\n" + cardPlayer1.getCardName() + " wins! " + cardPlayer2.getCardName() + " is defeated" + "\r\n";
            return cardPlayer1;
        }
        if (isKraken(cardPlayer2)) {
            battleLog += cardPlayer2.getCardName() + " is immune to all spells and attacks! " + cardPlayer1.getCardName() + " can't touch it." + "\r\n" + cardPlayer2.getCardName() + " wins! " + cardPlayer1.getCardName() + " is defeated" + "\r\n";
            return cardPlayer2;
        }

        if (isWaterSpell(cardPlayer1) && isKnight(cardPlayer2)) {
            battleLog += cardPlayer1.getCardName() + " unleashes a powerful water attack!" + "\r\n" + cardPlayer2.getCardName() + " is drowned instantly by the water." + "\r\n" + cardPlayer1.getCardName() + " wins! " + cardPlayer2.getCardName() + " is defeated" + "\r\n";
            return cardPlayer1;
        }
        if (isWaterSpell(cardPlayer2) && isKnight(cardPlayer1)) {
            battleLog += cardPlayer2.getCardName() + " unleashes a powerful water attack!" + "\r\n" + cardPlayer1.getCardName() + " is drowned instantly by the water." + "\r\n" + cardPlayer2.getCardName() + " wins! " + cardPlayer1.getCardName() + " is defeated" + "\r\n";
            return cardPlayer2;
        }

        double adjustedDamage1 = cardPlayer1.getDamage();
        double adjustedDamage2 = cardPlayer2.getDamage();

        if (isEffective(cardPlayer1.getElement(), cardPlayer2.getElement())) {
            adjustedDamage1 *= 2;
            battleLog += cardPlayer1.getCardName() + "'s attack is super effective against " + cardPlayer2.getCardName() + "! Damage is doubled." + "\r\n";
        } else if (isNotEffective(cardPlayer1.getElement(), cardPlayer2.getElement())) {
            adjustedDamage1 /= 2;
            battleLog += cardPlayer1.getCardName() + "'s attack is not very effective against " + cardPlayer2.getCardName() + ". Damage is halved." + "\r\n";
        }


        if (isEffective(cardPlayer2.getElement(), cardPlayer1.getElement())) {
            adjustedDamage2 *= 2;
            battleLog += cardPlayer2.getCardName() + "'s attack is super effective against " + cardPlayer1.getCardName() + "! Damage is doubled." + "\r\n";
        } else if (isNotEffective(cardPlayer2.getElement(), cardPlayer1.getElement())) {
            adjustedDamage2 /= 2;
            battleLog += cardPlayer2.getCardName() + "'s attack is not very effective against " + cardPlayer1.getCardName() + ". Damage is halved." + "\r\n";
        }

        if (adjustedDamage1 > adjustedDamage2) {
            battleLog += cardPlayer1.getCardName() + " starts the attack, but " + cardPlayer2.getCardName() + "'s defense is no match for the overwhelming force!" + "\r\n" + cardPlayer1.getCardName() + " wins! " + cardPlayer2.getCardName() + " is defeated" + "\r\n";
            return cardPlayer1;
        } else if (adjustedDamage2 > adjustedDamage1) {
            battleLog += cardPlayer2.getCardName() + " starts the attack, but " + cardPlayer1.getCardName() + "'s defense is no match for the overwhelming force!" + "\r\n" + cardPlayer2.getCardName() + " wins! " + cardPlayer1.getCardName() + " is defeated" + "\r\n";
            return cardPlayer2;
        } else {
            battleLog += "Both " + cardPlayer1.getCardName() + " and " + cardPlayer2.getCardName() + " unleash their attacks, but neither gains the upper hand." + "\r\n" + "After a fierce battle, both fighters retreat, exhausted and wounded" + "\r\n" + "Draw" + "\r\n";
            return null;
        }
    }


    private boolean isKraken(Card card) {
        return card.getCardName().equalsIgnoreCase("Kraken");
    }

    private boolean isWaterSpell(Card card) {
        return card.getElement().equals("water") && card.getType().equals("spellcard");
    }

    private boolean isKnight(Card card) {
        return card.getCardName().equalsIgnoreCase("Knight");
    }
    public void addToBattleLog(String log){
        this.battleLog += log;
    }
}
