import org.junit.Assert;
import org.junit.Test;
import java.util.HashSet;

import static org.junit.Assert.*;

public class PlayerTest {

    @Test
    public void withdrawAndDepositTest(){
        House house = House.getInstance(4000);
        Player player1 = new Player("Raanan", 1000, house.createUserID());
        assertEquals(player1.getBalance(), 1000, 0);
        player1.withdraw(500);
        assertEquals(player1.getBalance(), 500, 0);
        player1.deposit(1000);
        assertEquals(player1.getBalance(), 1500, 0);
    }

    @Test
    public void enterGameAndLeaveGameTest(){
        House house = House.getInstance(4000);
        Player player1 = new Player("Raanan", 1000, house.createUserID());
        assertTrue(player1.enterGame());
        assertThrows(IllegalArgumentException.class, () -> player1.enterGame());
        assertTrue(player1.leaveGame());
        assertThrows(IllegalArgumentException.class, () -> player1.leaveGame());
    }
}