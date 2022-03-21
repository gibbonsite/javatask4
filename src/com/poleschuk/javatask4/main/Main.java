package com.poleschuk.javatask4.main;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.poleschuk.javatask4.entity.Ship;
import com.poleschuk.javatask4.parser.CustomParser;
import com.poleschuk.javatask4.reader.CustomReader;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private static final Logger logger = LogManager.getLogger();
    public static void main(String ...args) {
        String filename = "data/ship.txt";
        CustomReader reader = new CustomReader();
        CustomParser parser = new CustomParser();
        try {
            List<String> list = reader.readFiles(filename);
            ExecutorService executor = Executors.newFixedThreadPool(list.size());
            ArrayList<Future<String>> listFuture = new ArrayList<>();
            for (String line : list) {
                Ship ship = parser.parseShip(line);
                listFuture.add(executor.submit(ship));
            }
            executor.shutdown();
            for (Future<String> future : listFuture) {
                    logger.log(Level.INFO, future.get());
            }
        } catch(InterruptedException e) {
            logger.error("Thread was interrupted", e);
            Thread.currentThread().interrupt();
        }catch (ExecutionException e ){
            logger.log(Level.ERROR,"ExecutionException ", e);
        }
    }

}
