package com.kusobotmaker.Form;

import java.io.Serializable;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public
class SongIdForm implements Serializable
{
	public SongIdForm() {};
	private Long songID;
	private String songTitle;
}