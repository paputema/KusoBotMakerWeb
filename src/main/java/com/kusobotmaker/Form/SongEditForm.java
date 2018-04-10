package com.kusobotmaker.Form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kusobotmaker.Data.DataSongList;
import com.kusobotmaker.Data.DataSongText;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public
class SongEditForm implements Serializable
{
	List<DataSongText> songTexts = new ArrayList<>();
	DataSongList songList;

	public SongEditForm()
	{
		songTexts = new ArrayList<>();
	}
	public SongEditForm(DataSongList songList,DataSongText dataSongText)
	{
		this.songList = songList;
		songTexts = new ArrayList<>();
		songTexts.add(dataSongText);
	}
	public SongEditForm(Collection<DataSongText> DataSongTexts,DataSongList songList)
	{
		this.songTexts= new ArrayList<>(DataSongTexts);
		this.songList = songList;
	}


}