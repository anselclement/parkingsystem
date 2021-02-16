package com.parkit.parkingsystem.unitaire.util;

import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class InputReaderUtilTest {

    private static InputReaderUtil inputReaderUtil;

   /*@Test
    public void ReadSelectionTest(){
        inputReaderUtil = new InputReaderUtil();
        String input =("1");
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        //THEN
        assertEquals(1, inputReaderUtil.readSelection());
    }

    @Test
    public void ReadVehicleRegistrationNumberTest() throws Exception {
        //GIVEN
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        //WHEN
        inputReaderUtil.readVehicleRegistrationNumber();

        //THEN
        assertEquals(inputReaderUtil.readVehicleRegistrationNumber(), "ABCDEF");
    }*/
}
