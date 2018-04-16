package com.pay.clip.formats;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {
	private static final Logger logger = LoggerFactory.getLogger(Json.class);

	public static String convert(Object tx, boolean pretty) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			// Create a temp file
			File temp = File.createTempFile("output-json", ".tmp"); 
			
			// Convert object to JSON string and save into a file directly
			mapper.writeValue(temp, tx);

			// Convert object to JSON string
			String toJson = null;
			// Convert object to JSON string and pretty print
			if (pretty) {
				toJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tx);
			} else {
				toJson = mapper.writeValueAsString(tx);
			}
			// Remove the temp file
			temp.delete();
			return toJson;
		} catch (JsonGenerationException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (JsonMappingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
