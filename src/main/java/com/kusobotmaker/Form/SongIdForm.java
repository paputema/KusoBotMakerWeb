package com.kusobotmaker.Form;

import java.io.Serializable;

import lombok.Data;

@Data
public
class SongIdForm implements Serializable
{
	public SongIdForm() {};
	private Long songID;
	private String songTitle;
}