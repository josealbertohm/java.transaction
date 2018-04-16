package com.pay.clip.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay.clip.config.Variables;

public class Hsqldb {
	private static final Logger logger = LoggerFactory.getLogger(Hsqldb.class);
	
	private Connection conn = null;
	private Server server = null;
	
	public Hsqldb() {
    	try {
    		// Start HSQLDB server
    		this.server = this.databaseServer();
    		
            //Registering the HSQLDB JDBC driver
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            
            // Database connection URL
            String url = "jdbc:hsqldb:hsql://"+this.server.getAddress()+":"+this.server.getPort() +"/" + Variables.DB_NAME;
            
            //Creating the connection with HSQLDB
            this.conn = DriverManager.getConnection(url, Variables.DB_USER, Variables.DB_PASS);
            if (conn!= null) {
               logger.info("Database connection created successfully!"); 
               if (!this.validateTable("TRANSACTIONS")) {
            	   logger.info("Creating table TRANSACTIONS");
            	   this.initDatabase();
               }
            } else {
            	logger.error("Problem with the database connection");
            }
    	} catch (ClassNotFoundException e) {
    		logger.error(e.getMessage());
    		e.printStackTrace();
    	} catch (AclFormatException e) {
    		logger.error(e.getMessage());
    		e.printStackTrace();
    	} catch (IOException e) {
    		logger.error(e.getMessage());
    		e.printStackTrace();
    	} catch (SQLException e) {
    		logger.error(e.getMessage());
    		e.printStackTrace();
    	}
    }
	
    public Hsqldb(String dbFileNamePrefix, String userName, String password) {  
    	try {
	        Class.forName("org.hsqldb.jdbcDriver");
	
	        this.conn = DriverManager.getConnection("jdbc:hsqldb:" + dbFileNamePrefix, 
	                                           userName, 
	                                           password); 
    	} catch (ClassNotFoundException e) {
    		logger.error(e.getMessage());
    		e.printStackTrace();
    	} catch (SQLException e) {
    		logger.error(e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    protected Server databaseServer() throws SQLException, IOException ,AclFormatException {
    	DriverManager.registerDriver(new org.hsqldb.jdbcDriver());
    	int hsqldbPort = Variables.DB_PORT;
    	System.setProperty("db.server.port", Integer.toString(hsqldbPort));
    	logger.info("Database is using port: " + Integer.toString(hsqldbPort));
    	HsqlProperties configProps = new HsqlProperties();
    	configProps.setProperty("server.port", hsqldbPort);
    	configProps.setProperty("server.database.0", Variables.DB_FILE_NAME);
    	configProps.setProperty("server.dbname.0", Variables.DB_NAME);
    	Server server = new org.hsqldb.Server();
    	server.setLogWriter(null);
    	server.setErrWriter(null);
    	server.setRestartOnShutdown(false);
    	server.setNoSystemExit(true);
    	server.setProperties(configProps);
    	server.start();
    	logger.info("Database server is started " + server.getAddress());
    	return server;
    }
    
    protected boolean validateTable(String tableName) {
    	boolean tableExists = false;
    	try {
	    	DatabaseMetaData meta = conn.getMetaData();
	    	ResultSet res = meta.getTables(null, null, tableName, new String[] {"TABLE"});
	    	while (res.next()) {
	    		tableExists = res.getString("TABLE_NAME").equalsIgnoreCase(tableName);
	    	}
    	} catch (SQLException e) {
    		logger.error(e.getMessage());
    		e.printStackTrace();
    	}
    	
    	return tableExists;
    	
    }
    
    public void shutdown() throws SQLException {
        Statement st = conn.createStatement();
        st.execute("SHUTDOWN");
        this.conn.close();
        this.server.stop();
    }
    
    public void initDatabase() {
         this.update(
                "CREATE TABLE transactions (transaction_id VARCHAR(36), user_id NUMERIC, description VARCHAR(256), date VARCHAR(10), amount DECIMAL(10,2))");	
    }
    
    public synchronized ResultSet query(String query) {
    	if (this.conn == null) {
    		return null;
    	}
    	try {
    		logger.info(query);
	        Statement st = conn.createStatement(); 
	        ResultSet rs = st.executeQuery(query);
	        st.close();
	        return rs;
    	} catch (SQLException e) {
    		logger.error(e.getMessage());
    		e.printStackTrace();
    	}
    	return null;
    }

    public synchronized void update(String query) {
    	if (this.conn == null) {
    		return;
    	}
    	try {
	        Statement st = null;
	        st = conn.createStatement(); 
	        int i = st.executeUpdate(query); 
	        if (i == -1) {
	        	logger.error("DB error : " + query);
	        }
	        st.close();
    	} catch (SQLException e) {
    		logger.error(e.getMessage());
    		e.printStackTrace();
    	}
    } 
    
    public synchronized PreparedStatement createStatement(String query) {
    	try {
    		return conn.prepareStatement(query);
    	} catch (SQLException e) {
    		logger.error(e.getMessage());
    		e.printStackTrace();
    	} 
    	return null;
    }
}
