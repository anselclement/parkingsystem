package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static Ticket ticket;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticket = new Ticket();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    @DisplayName("Test si le ticket d'une voiture entrante s'enregistre en BD et met sa place en indisponible")
    public void testParkingACar() throws Exception {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle();
        String ticketInTheDataBase = ticketDAO.getTicket("ABCDEF").getVehicleRegNumber();
        boolean parkingSpotNotAvailable = ticketDAO.getTicket("ABCDEF").getParkingSpot().isAvailable();
        ParkingType  parkingTypeIsACar = ticketDAO.getTicket("ABCDEF").getParkingSpot().getParkingType();

        //THEN
        assertThat(ticketInTheDataBase).isEqualTo("ABCDEF");
        assertThat(parkingSpotNotAvailable).isEqualTo(false);
        assertThat(parkingTypeIsACar).isEqualTo(ParkingType.CAR);
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
    }

    @Test
    @DisplayName("Test si le ticket d'un vélo entrant s'enregistre en BD et met sa place en indisponible")
    public void testParkingABike() throws Exception {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(2);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle();
        String ticketInTheDataBase = ticketDAO.getTicket("ABCDEF").getVehicleRegNumber();
        boolean parkingSpotNotAvailable = ticketDAO.getTicket("ABCDEF").getParkingSpot().isAvailable();
        ParkingType  parkingTypeIsABike = ticketDAO.getTicket("ABCDEF").getParkingSpot().getParkingType();

        //THEN
        assertThat(ticketInTheDataBase).isEqualTo("ABCDEF");
        assertThat(parkingSpotNotAvailable).isEqualTo(false);
        assertThat(parkingTypeIsABike).isEqualTo(ParkingType.BIKE);
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
    }

    @Test
    @DisplayName("Test si le prix et l'heure de sortie du véhicule sont bien enregistré en BD")
    public void testParkingLotExit() throws Exception {
        //GIVEN
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processExitingVehicle();
        double fareIsCorrectlyPopulated = ticketDAO.getTicket("ABCDEF").getPrice();
        Date outTimeIsCorrectlyPopulated = ticketDAO.getTicket("ABCDEF").getOutTime();

        //THEN
        assertThat(fareIsCorrectlyPopulated).isEqualTo(0);
        assertNotNull(outTimeIsCorrectlyPopulated);
        assertNotNull(fareIsCorrectlyPopulated);
        //TODO: check that the fare generated and out time are populated correctly in the database
    }

    /*@Test
    @DisplayName("erreur lors du choix du vehicle")
    public void testErrorVehicleChoice() throws Exception {
        // GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // WHEN
        when(inputReaderUtil.readSelection()).thenReturn(4);

        // THEN
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> inputReaderUtil.readVehicleRegistrationNumber());
        assertEquals("Error parsing user input for type of vehicle", thrown.getMessage());
    }*/

    /*@Test
    @DisplayName("erreur lors de la lecture de la plaque d'immatriculation")
    public void testUnknownVehicle() throws Exception {
        // GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(null);

        // THEN
        assertThrows(IllegalArgumentException.class, () -> inputReaderUtil.readVehicleRegistrationNumber());
    }*/


    @Test
    @DisplayName("test si un utilisateur récurrent roulant en voiture part avec une réduction de 5%")
    public void testParkingCarLotExitWithReduction(){

    }

    @Test
    @DisplayName("test si un utilisateur récurrent roulant en vélo part avec une réduction de 5%")
    public void testParkingBikeLotExitWithReduction(){

    }
}
