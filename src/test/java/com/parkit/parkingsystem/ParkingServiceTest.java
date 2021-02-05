package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private FareCalculatorService fareCalculatorService;
    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            fareCalculatorService = new FareCalculatorService();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest(){
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processIncomingVehicleCarTest() throws Exception {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(8);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        //WHEN
        parkingService.processIncomingVehicle();

        verify(inputReaderUtil, Mockito.times(1)).readSelection();
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
        assertThat(parkingService.getNextParkingNumberIfAvailable().getId()).isEqualTo(8);
    }

    @Test
    public void processIncomingVehicleBikeTest() throws Exception {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(8);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        //WHEN
        parkingService.processIncomingVehicle();

        //THEN
        verify(inputReaderUtil, Mockito.times(1)).readSelection();
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
        assertThat(parkingService.getNextParkingNumberIfAvailable().getId()).isEqualTo(8);
    }

    @Test
    public void parkingFullTest() throws Exception {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

        //WHEN
        parkingService.processIncomingVehicle();

        //THEN
        verify(inputReaderUtil, Mockito.times(1)).readSelection();
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
        assertThat(parkingService.getNextParkingNumberIfAvailable()).isEqualTo(null);
    }


    /*@Test
    public void checkIfItsRegularUser() {
            //GIVEN
            when(ticketDAO.getVehicleRegNumberInTheDataBase()).thenReturn(Arrays.asList("ABCDEF"));

            //WHEN
            List<String> result = ticketDAO.getVehicleRegNumberInTheDataBase();

            //THEN
            verify(ticketDAO).getVehicleRegNumberInTheDataBase();
            assertThat(result).contains("ABCDEF");
    }*/


   /* @Test
    public void checkIfFivePercentReductionIsApplyToCustomerWithBike() throws Exception {
        try{
            //GIVEN
            Date inTime = new Date();
            inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
            Date outTime = new Date();
            Ticket ticket = new Ticket();
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            fareCalculatorService.calculateFare(ticket);
            when(fareCalculatorService.regNumberInTheDataBase()).thenReturn(true);


            //THEN
            verify(fareCalculatorService.regNumberInTheDataBase());
            assertThat(ticket.getPrice()).isEqualTo(0.95);

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Fail to apply 5% reduction on the price");
        }
    }*/

}
