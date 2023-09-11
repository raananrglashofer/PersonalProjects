import java.util.Scanner;
import java.util.HashSet;
public class Player {
    private double balance;
    private int totalHandsPlayed;
    private int playerWins;
    private int userID;
    private String name;
    private Game game;

    // userID will be generated in the house class and then passed into the player constructor;
    public Player(String name, double balance, int userID){
        this.name = name;
        this.balance = balance;
        this.userID = userID;
        House.getInstance().addPlayerToHouse(this);
    }

    public int getUserID(){
        return this.userID;
    }

    public double withdraw(double amount){
        this.balance -= amount;
        return this.balance;
    }

    public double deposit(double amount){
        this.balance += amount;
        return this.balance;
    }

    public double getWinPercentage(){
        if(this.totalHandsPlayed == 0){
            return 0;
        }
        return ((double) this.playerWins / (double)this.totalHandsPlayed);
    }

    public int getTotalHandsPlayed(){
        return this.totalHandsPlayed;
    }

    public double getBalance(){
        return this.balance;
    }

    public boolean enterGame(){
        if (this.game != null) {
            throw new IllegalArgumentException("Can't enter 2 games at once");
        }
        this.game = new Game(this);
        return true;
    }

    public boolean leaveGame(){
        if (this.game == null) {
            throw new IllegalArgumentException("Can't leave game if you're not in a game");
        }
        this.game = null;
        return true;
    }
    public void addToTotalHands(){
        this.totalHandsPlayed++;
    }

    public void addToWins(){
        this.playerWins++;
    }

    public Game getGame(){
        return this.game;
    }
}