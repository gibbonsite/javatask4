package com.poleschuk.javatask4.entity;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Berth {
    private static final Logger logger = LogManager.getLogger();
    private final long berthId;
    private final SeaPort seaPort;
    private final Random random = new Random();
	
    public Berth(long berthId, SeaPort seaPort){
        this.berthId = berthId;
        this.seaPort = seaPort;
    }

    public void process(Ship ship) {
    	ship.setShipState(Ship.State.PROCESSING);
        logger.info(String.format("Berth %d started processing a ship %d", berthId, ship.getShipId()));
        try {
        	TimeUnit.SECONDS.sleep(random.nextInt(10));
        } catch(InterruptedException e) {
            logger.error("Thread was interrupted while sleeping",e);
            Thread.currentThread().interrupt();
        }
        seaPort.addContainers(ship.isUpload() ? ship.getContainers() : -ship.getMaxSize() + ship.getContainers());
        ship.setContainers(ship.isUpload() ? 0 : ship.getMaxSize());
    	ship.setShipState(Ship.State.FINISHED);
        logger.info(String.format("Berth %d has finished processing a ship %d", berthId, ship.getShipId()));
    }

	public long getBerthId() {
		return berthId;
	}
    
}
