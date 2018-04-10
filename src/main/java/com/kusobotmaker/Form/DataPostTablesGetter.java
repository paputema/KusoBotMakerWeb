package com.kusobotmaker.Form;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;


import com.kusobotmaker.Data.DataPosttable;


public class DataPostTablesGetter
{
    /**
	 *
	 */
	@Valid
	private List<DataPosttable> postData =  new ArrayList<>();
	public DataPostTablesGetter( ) {
		super();
	}

	public List<DataPosttable> getPostData() {
		return postData;
	}

	public void setPostData(List<DataPosttable> postData) {
		this.postData = postData;
	}

	public void addAll(List<DataPosttable> dataPosttables)
	{
		this.postData.addAll(dataPosttables);
	}
}