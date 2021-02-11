package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.RoundingMode;
import java.text.DecimalFormat;


public class FareCalculatorService {


    public void calculateFare(Ticket ticket) throws Exception {

        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        Double inHour = (double)ticket.getInTime().getTime();
        Double outHour = (double)ticket.getOutTime().getTime();

        double duration = Math.round((((outHour - inHour)/60/1000)/60)*100.0)/100.0;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                if(duration <= 0.5){
                    ticket.setPrice(0);
                }else if (ticket.getRecurrentUser()){
                    double roundedPrice = (double) Math.round(((duration * Fare.CAR_RATE_PER_HOUR) * Fare.REDUCTION_FIVE_PERCENT)*100)/100;
                    ticket.setPrice(roundedPrice);
                }else{
                    double roundedPrice = (double) Math.round((duration * Fare.CAR_RATE_PER_HOUR)*100)/100;
                    ticket.setPrice(roundedPrice);
                }
                break;
            }
            case BIKE: {
                if(duration <= 0.5){
                    ticket.setPrice(0);
                }else if (ticket.getRecurrentUser()) {
                    double roundedPrice = (double) Math.round(((duration * Fare.BIKE_RATE_PER_HOUR) * Fare.REDUCTION_FIVE_PERCENT)*100)/100;
                    ticket.setPrice(roundedPrice);
                }else {
                    double roundedPrice = (double) Math.round((duration * Fare.BIKE_RATE_PER_HOUR)*100)/100;
                    ticket.setPrice(roundedPrice);
                }
                break;
            }
            default: throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}