package com.poleschuk.javatask4.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.poleschuk.javatask4.exception.CustomException;

public class CustomReader {
    private static final Logger logger = LogManager.getLogger();
    public List<String> readFiles(String filename){
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<String> lines = reader.lines()
                    .collect(Collectors.toList());
            if(lines == null){
                throw new CustomException("File is empty");
            }
            logger.log(Level.DEBUG,lines.toString());
            return lines;
        } catch (IOException | CustomException e) {
            logger.log(Level.FATAL,"File is not read or it's empty" + filename);
            throw new RuntimeException("File error ", e);
        }
    }
}
