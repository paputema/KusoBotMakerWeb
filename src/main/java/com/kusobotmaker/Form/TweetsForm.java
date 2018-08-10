package com.kusobotmaker.Form;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import com.kusobotmaker.Data.DataPosttable;

import lombok.Data;
@Data
public class TweetsForm implements Serializable
{
	public TweetsForm(List<DataPosttable> tweets) {
		super();
		this.tweets = tweets;
	}
	public TweetsForm() {
		super();
	}
	@Valid
	private List<DataPosttable> tweets;
}
