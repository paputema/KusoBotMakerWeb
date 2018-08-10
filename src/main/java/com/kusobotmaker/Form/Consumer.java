package com.kusobotmaker.Form;
import java.io.Serializable;

import lombok.Data;

@Data
public class Consumer implements Serializable
{
	public Consumer() {
		this.consumerKey = "";
		this.consumerSecret = "";
	}
	public Consumer(String consumerKey, String consumerSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
	}
	private String consumerKey;
	private String consumerSecret;
}