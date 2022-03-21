package com.poleschuk.javatask4.entity;


import java.util.TimerTask;

public class TimerThread extends TimerTask {
    @Override
    public void run() {
        SeaPort seaPort = SeaPort.getInstance();
        seaPort.checkContainers();
    }
}