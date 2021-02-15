package com.parkit.parkingsystem.unitaire.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParkingSpotDAOTest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static ParkingSpot parkingSpot;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @AfterAll
    private static void tearDown() {
        dataBasePrepareService.clearDataBaseEntries();
    }


    @Test
    @DisplayName("Vérifier qu'une place de parking est disponible")
    public void checkIfGetNextAvailableSpotTest(){
        parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        assertEquals(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR), 1);
    }

    @Test
    @DisplayName("Vérifier qu'on retourne la bonne place de parking")
    public void checkIfParkingSpotIsCorrectTest(){
        parkingSpot = new ParkingSpot(4, ParkingType.CAR, false);
        assertEquals(parkingSpot.hashCode(), 4);
    }

    @Test
    @DisplayName(("Vérifier que le parking est mis à jour lors de la sortie d'une voiture et donc que la place de parking attribué est sur true"))
    public void checkIfParkingIsUpdateTest(){
        //GIVEN
        parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        parkingSpot.setId(1);
        parkingSpot.setParkingType(ParkingType.BIKE);
        parkingSpot.setAvailable(true);
        parkingSpot = new ParkingSpot(parkingSpot.getId(), parkingSpot.getParkingType(), parkingSpot.isAvailable());


        //WHEN
        parkingSpotDAO.updateParking(parkingSpot);

        //THEN
        assertEquals(parkingSpot.isAvailable(), true);
        assertEquals(parkingSpot.getParkingType(), ParkingType.BIKE);
    }
}
