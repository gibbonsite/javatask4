package com.poleschuk.javatask4.entity;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SeaPort {
    private static final Logger logger = LogManager.getLogger();
    private static double LOAD_FACTOR = 0.75;
    private static double EMPTINESS_FACTOR = 0.25;
    private static int MAX_CONTAINERS_SIZE = 1000;
    private static int BERTH_SIZE = 10;
    private static SeaPort instance;
    private static ReentrantLock creatorLock = new ReentrantLock();
    private static AtomicBoolean creator = new AtomicBoolean(false);
    private ReentrantLock lock = new ReentrantLock();
    private Deque<Berth> freeBerths = new ArrayDeque<>();
    private Deque<Condition> waitingQueue = new ArrayDeque<>();
    private ReentrantLock containersLock = new ReentrantLock();
    private Condition waitForUploadedContainers = containersLock.newCondition();
    private Condition waitForDownloadedContainers = containersLock.newCondition();
    private int containers;
    
	private SeaPort() {
        IntStream.range(0, BERTH_SIZE).forEach(i -> {
        	Berth berth = new Berth(i + 1, this);
            freeBerths.add(berth);
        });
    }

    public static SeaPort getInstance() {
        if (!creator.get()) {
            try{
                creatorLock.lock();
                if (instance == null) { 
                    instance = new SeaPort();
                    startTimer();
                    creator.set(true);
                }
            } finally {
                creatorLock.unlock();
            }
        }
        return instance;
    }

    private static void startTimer() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerThread(), 0, 500);
    }

    public Berth acquireBerth(Ship ship){
        Berth berth = null;
        try {
            lock.lock();
            TimeUnit.SECONDS.sleep(2);
            Condition condition = lock.newCondition();
            if (freeBerths.isEmpty()) {
                waitingQueue.addLast(condition);
                condition.await();
            }
            berth = freeBerths.removeFirst();
        } catch (InterruptedException e) {
            logger.error("The current thread is interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        return berth;
    }

    public void releaseBerth(Berth berth) {
        Condition condition = null;
        try {
            lock.lock();
            freeBerths.addLast(berth);
            TimeUnit.SECONDS.sleep(1);
            logger.log(Level.INFO,"Berth is released: " + berth.getBerthId());
            condition = waitingQueue.pollFirst();
        } catch(InterruptedException e){
            logger.log(Level.ERROR,"The current thread is interrupted", e);
            Thread.currentThread().interrupt();
        }finally {
            if(condition != null) {
                condition.signal();
            }
            lock.unlock();
        }
    }

    public void checkContainers() {
		containersLock.lock();
        try {
            if (containers > LOAD_FACTOR * MAX_CONTAINERS_SIZE) {
            	containers = MAX_CONTAINERS_SIZE / 2;
            } else if (containers < EMPTINESS_FACTOR * MAX_CONTAINERS_SIZE) {
            	containers = MAX_CONTAINERS_SIZE / 2;
            }
        } finally {
        	containersLock.unlock();
        }
    }

	public void addContainers(int containers) {
		containersLock.lock();
		try {
			while (this.containers + containers > MAX_CONTAINERS_SIZE) {
				waitForDownloadedContainers.await();
			}
			while (this.containers + containers < 0) {
				waitForUploadedContainers.await();
			}
			this.containers += containers;
			if (containers > 0) {
				waitForUploadedContainers.signal();
			} else if (containers < 0) {
				waitForDownloadedContainers.signal();
			}
        } catch (InterruptedException e) {
            logger.error("The current thread is interrupted", e);
            Thread.currentThread().interrupt();
		} finally {
			containersLock.unlock();
		}
	}
}
