import org.junit.Assert;
import org.junit.Test;
import java.util.HashSet;

import static org.junit.Assert.*;

public class HouseTest {

    @Test
    public void checkIfSingletonWorks(){
        House house1 = House.getInstance(4000);
        House house2 = House.getInstance();
        assertEquals(house1, house2);
    }

    @Test
    public void isHouseBalanceSufficient(){
        House house = House.getInstance(4000);
        Player player1 = new Player("Raanan", 1000, house.createUserID());
        Player player2 = new Player("Asher", 1000, house.createUserID());
        Player player3 = new Player("Jake", 1000, house.createUserID());
        // testing whether createUserID method is working properly
        assertEquals(player3.getUserID(), 3);
        // house should have a larger balance than the player's total balance
        assertTrue(house.checkHouseBalanceIsSufficient());
        Player player4 = new Player("Lebron", 2000, house.createUserID());
        // house should have a smaller balance than the player's total balance after adding lebron
        assertFalse(house.checkHouseBalanceIsSufficient());
    }
}