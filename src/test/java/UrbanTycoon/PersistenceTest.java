
package UrbanTycoon;

import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class PersistenceTest {
    private final int FIELDSIZE = 80;
    private final int FIELDROWSNUM = 8;
    private final int FIELDCOLSNUM = 16;
    private final int INITIALMONEY = 100000;
    private final int INITIALRESIDENT = 20;
    private final int ZONEPRICE = 250;
    private final int ROADPRICE = 75;
    private final int STADIUMPRICE = 1000;
    private final int POLICESTATIONPRICE = 750;
    private final int FIRESTATIONPRICE = 750;
    private final int FORESTPRICE = 500;
    private final double ANNUALFEEPERCENTAGE = 0.3; // playerBuildIt annualFee = price * ANNUALFEEPERCENTAGE
    private final int RESIDENTCAPACITY = 5;
    private final int WORKPLACECAPACITY = 18;
    private final double REFUND = 0.4;
    private final int RADIUS = 3;
    private final int CRITSATISFACTION = -5;
    private final int MOVEINATLEASTSATISFACTION = 5;
    private final double CHANCEOFFIRE = 0.05;
    
    static City city;
    static City city2;
    
    @BeforeEach
    public void setUp(){
        city = new City(INITIALRESIDENT, FIELDSIZE, FIELDROWSNUM, FIELDCOLSNUM, CRITSATISFACTION,
                MOVEINATLEASTSATISFACTION, INITIALMONEY,
                ZONEPRICE, ROADPRICE, STADIUMPRICE, POLICESTATIONPRICE, FIRESTATIONPRICE, FORESTPRICE,
                ANNUALFEEPERCENTAGE,
                RESIDENTCAPACITY, WORKPLACECAPACITY, REFUND, CHANCEOFFIRE, RADIUS, null);
        city2 = new City(INITIALRESIDENT, FIELDSIZE, FIELDROWSNUM, FIELDCOLSNUM, CRITSATISFACTION,
                MOVEINATLEASTSATISFACTION, INITIALMONEY,
                ZONEPRICE, ROADPRICE, STADIUMPRICE, POLICESTATIONPRICE, FIRESTATIONPRICE, FORESTPRICE,
                ANNUALFEEPERCENTAGE,
                RESIDENTCAPACITY, WORKPLACECAPACITY, REFUND, CHANCEOFFIRE, RADIUS, null);
    }
    
    @Test
    public void loadSameGame(){
        for(int i=0;i<city.getFields().length;i++)
            for(int j=0; j<city.getFields()[0].length;j++)
                assertEquals(city.getFields()[i][j],city2.getFields()[i][j]);
        String str = city.gameStateAsString();
        Scanner sc = new Scanner(str);
        city.loadGame(sc, false);
        for(int i=0;i<city.getFields().length;i++)
            for(int j=0; j<city.getFields()[0].length;j++)
                assertEquals(city.getFields()[i][j],city2.getFields()[i][j]);
    }
    @Test
    public void changeThenLoad(){
        city.fieldSelect( 1,5);
        city.build(Stadium.class);
        city.fieldSelect(0,6);
        city.selectField(IndustrialZone.class);
        city.yearElapsed();
        city.moveInOneResident(false);
        String str = city.gameStateAsString();
        city2.loadGame(new Scanner(str), false);
        for(int i=0;i<city.getFields().length;i++)
            for(int j=0; j<city.getFields()[0].length;j++)
                assertEquals(city.getFields()[i][j],city2.getFields()[i][j]);
        for(int i=0;i<city.getResidents().size();i++)
            assertEquals(city.getResidents().get(i),city2.getResidents().get(i));
    }
}
