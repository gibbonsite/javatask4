package com.poleschuk.javatask4.entity;

import java.util.concurrent.Callable;

public class Ship implements Callable<String>  {
	private long shipId;
	private State shipState;
	private int containers;
	private int maxSize;
	private boolean upload;
	
	public enum State {
		NEW, PROCESSING, FINISHED
	}
	
	public Ship(long shipId, int containers, int maxSize) {
		this.shipId = shipId;
		this.shipState = State.NEW;
		this.containers = containers;
		this.maxSize = maxSize;
		this.upload = containers == 0;
	}
	
	@Override
	public String call() {
        SeaPort seaPort = null;
        Berth berth = null;
        try {
            seaPort = SeaPort.getInstance();
            berth = seaPort.acquireBerth(this);
            berth.process(this);
            return String.format("Ship: %d   Berth: %d", this.getShipId(), berth.getBerthId());
        } finally {
            seaPort.releaseBerth(berth);
        }
	}

	public long getShipId() {
		return shipId;
	}

	public void setShipId(long shipId) {
		this.shipId = shipId;
	}

	public State getShipState() {
		return shipState;
	}

	public void setShipState(State shipState) {
		this.shipState = shipState;
	}

	public int getContainers() {
		return containers;
	}

	public void setContainers(int containers) {
		this.containers = containers;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public boolean isUpload() {
		return upload;
	}

	public void setUpload(boolean upload) {
		this.upload = upload;
	}
	
}
