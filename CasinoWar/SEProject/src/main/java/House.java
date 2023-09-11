import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

class House {
    private static House house = null;
    private int totalHandsPlayed;
    private int houseWins;
    private double houseBalance;
    private double aggregatePlayerBalance;
    private HashMap<Integer, Player> playersMap = new HashMap<>();
    private boolean houseOnline = true; // should it start on or off
    private int numberOfPlayers;
    private Deck.Card card;
    private House(double balance){
        houseBalance = balance;
    }
    public static synchronized House getInstance(double balance){
        if(house == null){
            house = new House(balance);
        }
        return house;
    }

    public static synchronized House getInstance(){
        return house;
    }

    public double getHouseBalance(){
        return house.houseBalance;
    }

    public double getAggregatePlayerBalance(){
        return house.aggregatePlayerBalance;
    }

    // divide and return houseWins by totalHandsPlayed
    public double getWinPercentage(){
        if(totalHandsPlayed == 0){
            return 0;
        }
        return (houseWins/totalHandsPlayed);
    }
    // make sure the house balance is greater than the players' total balance
    // use a for each to parse through each player and add their balance to the total balance
    // compare houseBalance to playerTotalBalance
    // if greater, houseOnline is set to true and return true
    // if equal or less, houseOnline is set to false and return false

    public boolean checkHouseBalanceIsSufficient(){
        for(Player p : playersMap.values()){
            aggregatePlayerBalance += p.getBalance();
        }
        if(houseBalance > aggregatePlayerBalance){
            houseOnline = true;
            return true;
        }
        if(houseBalance <= aggregatePlayerBalance){
            houseOnline = false;
            return false;
        }
        return true;
    }

    // the userID of a player is their number (i.e. the third player's userID is 3)
    // called when passing in parameter for player
    public int createUserID(){
        house.numberOfPlayers++;
        return house.numberOfPlayers;
    }
    public void addToHouseHands(){
        this.totalHandsPlayed++;
    }

    public void addToHouseWins(){
        this.houseWins++;
    }

    public void houseWin(double bet){
        this.houseBalance += bet;
    }
    public void houseLoss(double bet){
        this.houseBalance -= bet;
    }
    public Deck.Card getCard(){
        return this.card;
    }

    public void addPlayerToHouse(Player player){
        if(houseOnline == true) {
            house.playersMap.put(player.getUserID(), player);
        }
    }
}