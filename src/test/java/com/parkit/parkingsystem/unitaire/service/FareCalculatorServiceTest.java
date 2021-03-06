package com.parkit.parkingsystem.unitaire.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    @DisplayName("Calcule le prix du stationnement pour le type de véhicule voiture")
    public void calculateFareCar() throws Exception {
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("Calcule le prix du stationnement pour le type de véhicule vélo")
    public void calculateFareBike() throws Exception {
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("Test du calcul du prix lors d'un type de vehicle inconnu")
    public void calculateFareUnknownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.DEFAULT,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
        assertEquals("Unknown Parking Type", thrown.getMessage());
    }

    @Test
    @DisplayName("Test erreur si inTime > outTime pour le Vélo")
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    @DisplayName("Test erreur si inTime > outTime pour le Voiture")
    public void calculateFareCarWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    @DisplayName("Calcule le prix du stationnement pour le type de véhicule vélo pour une durée < 1h")
    public void calculateFareBikeWithLessThanOneHourParkingTime() throws Exception {
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((double)Math.round((0.75 * Fare.BIKE_RATE_PER_HOUR)*100)/100, ticket.getPrice() );
    }

    @Test
    @DisplayName("Calcule le prix du stationnement pour le type de véhicule voiture pour une durée < 1h")
    public void calculateFareCarWithLessThanOneHourParkingTime() throws Exception {
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (double)Math.round((0.75 * Fare.CAR_RATE_PER_HOUR)*100)/100 , ticket.getPrice());
    }

    @Test
    @DisplayName("Calcule le prix du stationnement pour le type de véhicule voiture pour une durée > 24h")
    public void calculateFareCarWithMoreThanADayParkingTime() throws Exception {
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    @DisplayName("Calcule le prix du stationnement pour le type de véhicule vélo pour une durée > 24h")
    public void calculateFareBikeWithMoreThanADayParkingTime() throws Exception {
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.BIKE_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    @DisplayName("Calcule le prix du stationnement pour le type de véhicule voiture pour une durée < 30min")
    public void calculateFareCarForThirtyMinutesOrLessParkingTime() throws Exception {
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (30 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    @DisplayName("Calcule le prix du stationnement pour le type de véhicule vélo pour une durée < 30min")
    public void calculateFareBikeForThirtyMinutesOrLessParkingTime() throws Exception {
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (30 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    @DisplayName("Vérifie si l'utilisateur d'un vélo a le droit à une réduction de 5%")
    public void checkIfFivePercentReductionIsApplyToCustomerWithBike() throws Exception {
        try{
            //GIVEN
            Date inTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
            inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
            Date outTime = new Date();
            Ticket ticket = new Ticket();
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            ticket.setRecurrentUser(true);

            //WHEN
            fareCalculatorService.calculateFare(ticket);

            //THEN
            assertThat(ticket.getPrice()).isEqualTo( (double) Math.round((( Fare.BIKE_RATE_PER_HOUR) * Fare.REDUCTION_FIVE_PERCENT)*100)/100);

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Fail to apply 5% reduction on the price");
        }
    }

    @Test
    @DisplayName("Vérifie si l'utilisateur d'une voiture a le droit à une réduction de 5%")
    public void checkIfFivePercentReductionIsApplyToCustomerWithCar() throws Exception {
        try{
            //GIVEN
            Date inTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
            Date outTime = new Date();
            Ticket ticket = new Ticket();
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            ticket.setRecurrentUser(true);

            //WHEN
            fareCalculatorService.calculateFare(ticket);

            //THEN
            assertThat(ticket.getPrice()).isEqualTo( (double) Math.round((( Fare.CAR_RATE_PER_HOUR) * Fare.REDUCTION_FIVE_PERCENT)*100)/100);

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Fail to apply 5% reduction on the price");
        }
    }
}
