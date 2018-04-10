package com.kusobotmaker.Form;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public class FromGetConsumer implements Serializable
{
	public FromGetConsumer() {
		super();
		//consumers = new ArrayList<Consumer>();
	}

	public FromGetConsumer(List<Consumer> test) {
		consumers = test;
		}

	@Valid
	private List<Consumer> consumers;
	private String testText;
}