package com.pay.clip;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay.clip.formats.Inputs;
import com.pay.clip.formats.Json;

public class Transaction {
	private static final Logger logger = LoggerFactory.getLogger(Transaction.class);
	
	private String transaction_id = null;
	private BigInteger user_id = null;
	private String description = null;
	private String date = null;
	private BigDecimal amount = null;
	
	protected void setId() {
		this.transaction_id = String.valueOf( UUID.randomUUID() );
	}
	public Transaction() {
		this.setId();
	}	
	public Transaction(BigInteger user_id, String description, String date, BigDecimal amount) {
		this.setId();
		this.user_id = user_id;
		this.description = description;
		this.date = date;
		this.amount = amount;
	}
	public Transaction(String transaction_id, BigInteger user_id, String description, String date, BigDecimal amount) {
		this.transaction_id = transaction_id;
		this.user_id = user_id;
		this.description = description;
		this.date = date;
		this.amount = amount;
	}
	public String getTransaction_id() {
		return this.transaction_id;
	}
	public BigInteger getUser_id() {
		return user_id;
	}
	public void setUser_id(BigInteger user_id) {
		this.user_id = user_id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public void fromJson(String inputFile) {
		String data = Inputs.getContenT(inputFile);
		JSONObject obj = new JSONObject(data);
		logger.info(obj.toString());
		this.user_id = obj.getBigInteger("user_id");
		this.description = obj.getString("description");
		this.date = obj.getString("date");
		this.amount = obj.getBigDecimal("amount");
		logger.info("Transaction added for the user id: " + this.user_id);
	}
	
	public String toJson() {
		return Json.convert(this, false);
	}
}
