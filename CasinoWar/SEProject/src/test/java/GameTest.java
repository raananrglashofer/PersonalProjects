import org.junit.Assert.*;
import org.junit.Test;
import java.util.List;
import java.util.HashSet;

import java.io.ByteArrayInputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import static org.junit.Assert.*;

public class GameTest {
    @Test
    //Test for when there's a tie and the player elects to surrender half of his bet (forfeit the hand)
    public void playGameTestTieForfeit(){
        ByteArrayInputStream forfeit = new ByteArrayInputStream(new String("False").getBytes());
        System.setIn(forfeit);
        House house = House.getInstance(100000);
        Player Raanan = new Player("Raanan", 10000, house.createUserID());
        Raanan.enterGame();
        while (Raanan.getGame().tie == false) {
            Raanan.getGame().playHand(false, 0, 10);
            System.out.println(Raanan.getBalance());
            System.out.println(house.getHouseBalance());
        }
    }

    @Test
    //Test for when there's a tie and the player elects to double down
    public void playGameTestTieDoubleDown(){
        ByteArrayInputStream doubleDown = new ByteArrayInputStream(new String("True").getBytes());
        System.setIn(doubleDown);
        House house = House.getInstance(100000);
        Player Raanan = new Player("Raanan", 10000, house.createUserID());
        Raanan.enterGame();
        while (Raanan.getGame().tie == false) {
            Raanan.getGame().playHand(false, 0, 10);
            System.out.println(Raanan.getBalance());
            System.out.println(house.getHouseBalance());
        }
    }

    //This tests for a double tiebreaker. To run this test, reinstitute the crossed-out code in Game.playTie.
//    @Test
//    public void playGameTestDoubleTie(){
//        House house = House.getInstance(1000000);
//        Player Raanan = new Player("Raanan", 10000, house.createUserID());
//        Raanan.enterGame();
//        double preBetBalance = 0;
//        while (Raanan.getGame().doubleTie == false) {
//            ByteArrayInputStream doubleDown = new ByteArrayInputStream(new String("True").getBytes());
//            System.setIn(doubleDown);
//            preBetBalance = Raanan.getBalance();
//            Raanan.getGame().playHand(false, 0, 10);
//            System.out.println(Raanan.getBalance());
//            System.out.println(house.getHouseBalance());
//        }
//        List<Double> possible = new ArrayList<>();
//        possible.add(preBetBalance + 20);
//        possible.add(preBetBalance - 40);
//        assertTrue(possible.contains(Raanan.getBalance()));
//    }

    @Test
    public void playGameHouseWinTest(){
        House house = House.getInstance(200);
        Player Raanan = new Player("Raanan", 50, house.createUserID());
        assertEquals(Raanan.getTotalHandsPlayed(), 0, 0);
        assertEquals(Raanan.getBalance(), 50, 0);
        assertEquals(house.getHouseBalance(), 200, 0);
        assertEquals(house.getWinPercentage(), 0, 0);
        double gamesPlayed = 0;
        double raananWins = 0;
        Raanan.enterGame();
        while(Raanan.getGame().houseWin == false){
            Raanan.getGame().playHand(false, 0, 5);
            gamesPlayed++;
            if (Raanan.getGame().houseWin == false) {
                raananWins++;
            }
        }
        assertEquals(Raanan.getTotalHandsPlayed(), gamesPlayed, 0);
        //Should give us Raanan's win percentage after he loses his first game
        assertEquals(raananWins/gamesPlayed, Raanan.getWinPercentage(), 0);
    }

    @Test
    public void playGamePlayerWinTest(){
        House house = House.getInstance(200);
        Player Raanan = new Player("Raanan", 50, house.createUserID());
        assertEquals(Raanan.getTotalHandsPlayed(), 0, 0);
        assertEquals(Raanan.getBalance(), 50, 0);
        assertEquals(house.getHouseBalance(), 200, 0);
        assertEquals(house.getWinPercentage(), 0, 0);
        double gamesPlayed = 0;
        Raanan.enterGame();
        while(Raanan.getGame().playerWin == false){
            Raanan.getGame().playHand(false, 0, 5);
            gamesPlayed++;
        }
        assertEquals(Raanan.getTotalHandsPlayed(), gamesPlayed, 0);
        //Should give us Raanan's win percentage after he wins his first game
        assertEquals(1/gamesPlayed, Raanan.getWinPercentage(), 0);
    }

    @Test
    public void playGameTieBet(){
        ByteArrayInputStream forfeit = new ByteArrayInputStream(new String("False").getBytes());
        System.setIn(forfeit);
        House house = House.getInstance(200);
        Player Raanan = new Player("Raanan", 50, house.createUserID());
        assertEquals(Raanan.getBalance(), 50, 0);
        assertEquals(house.getHouseBalance(), 200, 0);
        Raanan.enterGame();
        while(Raanan.getGame().tie == false){
            Raanan.getGame().playHand(true, 1, 10);
        }
        System.out.println(Raanan.getBalance());
        System.out.println(house.getHouseBalance());
    }
}