package com.poleschuk.javatask4.parser;

import com.poleschuk.javatask4.entity.Ship;

public class CustomParser {
    private static final String REGEX_DELIMITER = "\\s+";
    private static final char EQUALS_DELIMITER = '=';
    public Ship parseShip(String lineShip){
        Ship ship = null;
        int index;
        lineShip = lineShip.trim();
        String[] infoShip= lineShip.split(REGEX_DELIMITER);
        if(infoShip.length == 3){
            index = infoShip[0].indexOf(EQUALS_DELIMITER);
            long id = Long.parseLong(infoShip[0].substring(index+1));
            index = infoShip[1].indexOf(EQUALS_DELIMITER);
            int productSize = Integer.parseInt(infoShip[1].substring(index+1));
            index = infoShip[2].indexOf(EQUALS_DELIMITER);
            int maxSize = Integer.parseInt(infoShip[2].substring(index+1));
            ship = new Ship(id,productSize,maxSize);
        }else{
            throw new IllegalArgumentException("The length is " + infoShip.length);
        }
        return ship;
    }
}
