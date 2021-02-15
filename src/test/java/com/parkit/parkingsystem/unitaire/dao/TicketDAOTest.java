package com.parkit.parkingsystem.unitaire.dao;

import java.sql.Connection;
import java.util.Date;

import com.parkit.parkingsystem.dao.TicketDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TicketDAOTest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @AfterAll
    private static void tearDown() {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    @DisplayName("Vérifie que le ticket se sauvegarde bien et donc qu'on arrive à récupérer le ticket avec getTicket")
    void checkSaveTicketTest() {
        //GIVEN
        Ticket ticket = new Ticket();
        Date inTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ticket.setInTime(inTime);

        //WHEN
        ticketDAO.saveTicket(ticket);

        //THEN
        assertFalse(ticketDAO.saveTicket(ticket));
        assertNotNull(ticketDAO.getTicket("ABCDEF"));
    }

    @Test
    @DisplayName("Vérifie que le ticket se sauvegarde bien même si la durée de sortie est différente de null")
    void checkSaveTicketWithOutTimeTest() {
        //GIVEN
        Ticket ticket = new Ticket();
        Date inTime = new Date();
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ticket.setInTime(inTime);
        outTime.setTime(System.currentTimeMillis());
        ticket.setOutTime(outTime);

        //WHEN
        ticketDAO.saveTicket(ticket);

        //THEN
        assertFalse(ticketDAO.saveTicket(ticket));
        assertNotNull(ticketDAO.getTicket("ABCDEF"));
    }

    @Test
    @DisplayName("Vérifie que le ticket est bien mis à jour donc que outTime != null et prix != 0")
    void checkIfTicketIsUpdateTest(){
        //GIVEN
        Ticket ticket = new Ticket();
        Date inTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ticket.setInTime(inTime);
        ticketDAO.saveTicket(ticket);

        //WHEN
        Date outTime = new Date();
        ticket.setOutTime(outTime);
        ticket.setPrice(1.5);
        ticketDAO.updateTicket(ticket);

        //THEN
        assertNotNull(ticket.getOutTime());
        assertNotEquals(ticket.getPrice(), 0);
        assertTrue(ticketDAO.updateTicket(ticket));
    }

    @Test
    @DisplayName("Vérifie qu'un utilisateur est bien présent en BD")
    public void checkIfItsARecurringUserTest() {
        //GIVEN
        Ticket ticket = new Ticket();
        Date inTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ticket.setInTime(inTime);
        ticketDAO.saveTicket(ticket);

        //WHEN
        ticketDAO.checkIfRecurringUsers("ABCDEF");

        //THEN
        assertThat(ticketDAO.checkIfRecurringUsers("ABCDEF")).isTrue();
    }
}

