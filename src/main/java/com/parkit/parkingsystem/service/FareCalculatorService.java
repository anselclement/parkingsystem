package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class FareCalculatorService {

    //TODO :parcours la liste retourne true si valeur plaque rentrer = true
    public boolean regNumberInTheDataBase() throws Exception {

        TicketDAO ticketDAO = new TicketDAO();
        InputReaderUtil inputReaderUtil = new InputReaderUtil();

        return ticketDAO.getVehicleRegNumberInTheDataBase().contains(inputReaderUtil.readVehicleRegistrationNumber());
    }


    public void calculateFare(Ticket ticket) throws Exception {
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        //int inHour  ticket.getInTime().getHours();
        //int outHour  ticket.getOutTime().getHours();

        Double inHour = (double)ticket.getInTime().getTime();
        Double outHour = (double)ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        //int duration = outHour - inHour;
        double duration = Math.round((((outHour - inHour)/60/1000)/60)*100.0)/100.0;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                if(duration <= 0.5){
                    ticket.setPrice(0);
                }else if (regNumberInTheDataBase()){ //TODO : -5% calcul
                    ticket.setPrice((duration * Fare.CAR_RATE_PER_HOUR) * Fare.REDUCTION_FIVE_PERCENT);
                }else{
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                }
                break;
            }
            case BIKE: {
                if(duration <= 0.5){
                    ticket.setPrice(0);
                }else if (regNumberInTheDataBase()) { //TODO : -5% calcul
                    ticket.setPrice((duration * Fare.BIKE_RATE_PER_HOUR) * Fare.REDUCTION_FIVE_PERCENT);
                }else {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                }
                break;
            }
            default: throw new IllegalArgumentException("Unknown Parking Type");
        }

    }
}