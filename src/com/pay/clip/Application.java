package com.pay.clip;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.pay.clip.db.Hsqldb;

public class Application {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
   
	// Input parameters
	@Parameter(names = "-debug", description = "Debug mode")
    private boolean debug = false;
    @Parameter(names = "-help", help = true)
    private boolean help = false;
	@Parameter(names = {"-user", "-u"}, description = "Debug mode-")
    private String user_id = null;
	@Parameter(names = {"-command", "-c"}, description = "Kind of action")
    private String command = null;

	@Parameter(names = {"-transaction"}, description = "Transaction detail to be added")
    private String transaction = null;
	@Parameter(names = {"-id"}, description = "Transaction Id for showing details")
    private String transaction_id = null;
	
	protected boolean saveTransaction(Transaction tx) {
		Hsqldb db = new Hsqldb();
		db.update("INSERT INTO transactions VALUES ('" +
				tx.getTransaction_id() + "'," +
				tx.getUser_id() + ",'" +
				tx.getDescription() + "','" +
				tx.getDate() + "'," +
				tx.getAmount() + ")"
		);
		return true;
	}
	
	protected void showTransactions(ResultSet records, BigInteger user_id) {
        try {
        	if (user_id == null) {
        		System.out.println("[");
        	}
			while (records.next()) {
	        	Transaction tx = new Transaction(
	        			records.getString("transaction_id"),
	        			new BigInteger(records.getString("user_id")),
	        			records.getString("description"),
	        			records.getString("date"),
	        			new BigDecimal(records.getString("amount"))
	        			);
	        	if (user_id != null) {
	        		if (user_id.equals(tx.getUser_id())) {
	        			System.out.println(tx.toJson());
	        		} else {
	        			System.out.println("The user id " + user_id + " is not the owner of the transaction id " + tx.getTransaction_id());
	        		}
	        	} else {
	        		System.out.println(tx.toJson());
	        	}
	        }
        	if (user_id == null) {
        		System.out.println("]");
        	}
			records.close();
        } catch (SQLException e) {
        	logger.error(e.getMessage());
        	e.printStackTrace();
        }
	}
	
	protected ResultSet getUserTransactions(BigInteger user_id) {
		Hsqldb db = new Hsqldb();
		return db.query("SELECT * FROM transactions WHERE user_id=" + user_id + " ORDER BY date");
	}
	
	public BigInteger getUserIdAsBigInteger() {
		if (this.user_id != null) {
			return new BigInteger(this.user_id);
		}
		return null;
	}
	
	// Main methods for the cases
	public Transaction add_transaction(String inputFile) {
		Transaction tx = new Transaction();
		tx.fromJson(inputFile);
		if (tx != null) {
			this.saveTransaction(tx);
		}
		return tx;
	}

	public void show_transaction(String transaction_id, BigInteger user_id) {
		Hsqldb db = new Hsqldb();
		ResultSet records = db.query("SELECT * FROM transactions WHERE transaction_id='" + transaction_id + "'");
		if (records != null) {
			this.showTransactions(records, user_id);
			try {
				records.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void show_transactions(BigInteger user_id) {
		ResultSet records = this.getUserTransactions(user_id);
		if (records != null) {
			this.showTransactions(records, null);
			try {
				records.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void show_total_amount(BigInteger user_id) {
		ResultSet records = this.getUserTransactions(user_id);
		BigDecimal amount = new BigDecimal(0);
        try {
			while (records.next()) {
				amount = amount.add(new BigDecimal(records.getString("amount")));
	        }
			Total total = new Total(user_id, amount);
			System.out.println(total.toJson());
        } catch (SQLException e) {
        	logger.error(e.getMessage());
        	e.printStackTrace();
        }
	}
	
	// Help method to show application usage
	public static void usage() {
		System.out.println("\nThe application for processing transaction has the next usage:");
		System.out.println("\nAdd user transaction:");
		System.out.println("application <user_id> add <transaction_json>");
		System.out.println("\nShow a specific user transaction:");
		System.out.println("application <user_id> <transaction_id>");
		System.out.println("\nShow all the user transactions:");
		System.out.println("application <user_id> list");
		System.out.println("\nSum the amounts of the user transactions:");
		System.out.println("application <user_id> list");
	}
	
	// Application main method
	public static void main(String[] args) {
		Application app = new Application();
        @SuppressWarnings("deprecation")
		JCommander jCommander = new JCommander(app, args);
        jCommander.setProgramName("JCommanderExample");
        if (app.help) {
            jCommander.usage();
            return;
        }
        if (args.length < 2) {
        	logger.error("Missing parameter(s)");
        	Application.usage();
        	System.exit(-1);
        }
        
        if (app.user_id == null) {
        	logger.error("The user id parameter is missing or empty");
        	System.exit(-1);
        }
        
        logger.debug("User id to be used: " + app.user_id);
        if (app.command == null) {
        	app.command = "";
        }
        
        switch (app.command) {
        // Add a transaction
        case "add":
        	if (app.transaction != null) {
        		logger.info("Adding transaction for user id " + app.getUserIdAsBigInteger());
        		Transaction tx = app.add_transaction(app.transaction);
        		if (tx != null) {
        			System.out.println( tx.toJson() );
        		}
        	} else {
        		logger.error("Transaction detail is missing or empty");
        	}
        	break;
        // List the user transaction
        case "list":
        	logger.info("Looking transactions for user id " + app.getUserIdAsBigInteger());
        	app.show_transactions(app.getUserIdAsBigInteger());
        	break;
        
        // Show the total amount sum 
        case "sum":
        	logger.info("Getting total transactions amount for user id " + app.getUserIdAsBigInteger());
        	app.show_total_amount(app.getUserIdAsBigInteger());
        	break;
        
        // Default case is to show a transaction detail
        default:
        	app.show_transaction(app.transaction_id, app.getUserIdAsBigInteger());
        	break;
        }
        
        System.exit(0);
	}
}
