package com.pay.clip.formats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Inputs {
	private static final Logger logger = LoggerFactory.getLogger(Inputs.class);
	
	public static String getContenT(String inputFile) {
	    String content = "";
	    try {
	        content = new String (Files.readAllBytes(Paths.get(inputFile)));
	        logger.debug(content);
	    } catch (IOException e) {
	    	logger.error(e.getMessage());
	        e.printStackTrace();
	    }
	    return content;		
	}
}
