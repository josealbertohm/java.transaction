package com.pay.clip.formats;

import java.text.ParseException;
import java.text.SimpleDateFormat;  
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dates {
	private static final Logger logger = LoggerFactory.getLogger(Dates.class);
	
	public static Date fromString(String value, String format) {
		try {
			return new SimpleDateFormat(format).parse(value); 
		} catch (ParseException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static String toString(Date value, String format) {
		return new SimpleDateFormat(format).format(value); 
	}
}
