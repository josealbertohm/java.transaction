package com.pay.clip;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.pay.clip.formats.Json;

public class Total {
	private BigInteger user_id = null;
	private BigDecimal amount = null;
	
	public Total() {
	}
	public Total(BigInteger user_id, BigDecimal amount) {
		this.user_id = user_id;
		this.amount = amount;
	}

	public BigInteger getUser_id() {
		return user_id;
	}
	public void setUser_id(BigInteger user_id) {
		this.user_id = user_id;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String toJson() {
		return Json.convert(this, false);
	}
}
