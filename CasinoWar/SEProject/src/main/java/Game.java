import java.io.ByteArrayInputStream;
import java.util.Scanner;
import java.util.HashSet;
public class Game {
    private Player player;
    private House gameHouse;
    private Deck deck;
    public boolean tie = false;
    public boolean doubleTie = false;
    public boolean houseWin = false;
    public boolean playerWin = false;

    public Game(Player user){
        this.gameHouse = House.getInstance();
        if(gameHouse.checkHouseBalanceIsSufficient() == false){
            throw new IllegalArgumentException("House does not have sufficient funds at the moment. Come back later.");
        }
        this.player = user;
    }

    // player sets bet as argument
    // check if betOnTie is true
    // call pickRandomCard and assign a card to the player and house
    // compare the cards and go on based on what happened
    // if player won: call updateRecords(true, bet) and return
    // if house won: call updateRecords(false, bet) and return
    // if tie: prompt player if want to surrender (take back half the bet) or double down and play hand
    // to surrender: add half the money back to player balance and return
    // to double down: call playHand again
    public void playHand(boolean betOnTie, double tieBet, double bet){
        if(tieBet + bet > player.getBalance()){
            throw new IllegalArgumentException("Bet is larger than player balance. Deposit more money to keep playing.");
        }
        this.tie = false;
        this.deck = new Deck();
        Deck.Card playerCard = this.deck.pickRandomCard(); // player Card
        Deck.Card houseCard = this.deck.pickRandomCard(); // house Card
        if(playerCard.getNumber() > houseCard.getNumber()){
            this.playerWin = true;
            updateRecords(true, bet);
            if(betOnTie == true){
                this.player.withdraw(tieBet);
                gameHouse.houseWin(tieBet);
            }
            return;
        }
        if(playerCard.getNumber() < houseCard.getNumber()){
            this.houseWin = true;
            updateRecords(false, bet);
            if(betOnTie == true){
                this.player.withdraw(tieBet);
                gameHouse.houseWin(tieBet);
            }
            return;
        }
        if (playerCard.getNumber() == houseCard.getNumber()) {
            this.tie = true;
            if(betOnTie == true){
                wonTieBet(tieBet);
            }
            Scanner myObj = new Scanner(System.in);
            Boolean doubleDown = myObj.nextBoolean();
            if(doubleDown == true) {
                playTie(bet, 1);
            }
            else {
                this.player.withdraw(bet/2);
                this.gameHouse.houseWin(bet/2);
            }

        }
    }

    //We're passing in the initial bet amount as well as the number of tie. For example, if the game was a tie and then on the tiebreaker they tied again, the pot will increase in size.
    private void playTie (double bet, int tieNumber) {
        this.player.withdraw(bet);
        this.gameHouse.houseWin(bet);
        Deck.Card playerCard = this.deck.pickRandomCard(); // player Card
        Deck.Card houseCard = this.deck.pickRandomCard(); // house Card
        if (playerCard.getNumber() > houseCard.getNumber()) {
            updateRecords(true, bet*(tieNumber*2));
            return;
        }
        if (playerCard.getNumber() < houseCard.getNumber()) {
            updateRecords(false, bet*tieNumber);
            return;
        }
        if (playerCard.getNumber() == houseCard.getNumber()) {
            doubleTie = true;

            //This is to test for when there's a tie on the tiebreaker and the player doubles down again
            //ByteArrayInputStream letsGo = new ByteArrayInputStream(new String("True").getBytes());
            //System.setIn(letsGo);

            Scanner myObj = new Scanner(System.in);
            Boolean doubleDown = myObj.nextBoolean();
            if(doubleDown == true) {
                playTie(bet, tieNumber+1);
            }
            else {
                this.player.withdraw(bet/2);
                this.gameHouse.houseWin(bet/2);
            }
        }

    }

    // check if whoWon is true or false to determine who won
    // call addToTotalHands on player and house
    // if whoWon == true (player won): add bet to player.balance, playerWins++
    // if whoWon == false (house won): add bet to house.balance, houseWins++
    public void updateRecords(boolean whoWon, double bet){
        player.addToTotalHands();
        gameHouse.addToHouseHands();
        if(whoWon == true){
            gameHouse.houseLoss(bet);
            player.deposit(bet);
            player.addToWins();
        }
        else{
            player.withdraw(bet);
            gameHouse.houseWin(bet);
            gameHouse.addToHouseWins();
        }
    }

    public void wonTieBet(double bet){
        double total = bet * 10;
        this.player.deposit(total);
        this.gameHouse.houseLoss(total);
    }

}