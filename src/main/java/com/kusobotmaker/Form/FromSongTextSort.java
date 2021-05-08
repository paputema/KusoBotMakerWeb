package com.kusobotmaker.Form;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kusobotmaker.Data.DataSongText;
import com.kusobotmaker.repositories.DataSongListRepositories;
import com.kusobotmaker.repositories.DataSongTextRepositories;

@Service
public class FromSongTextSort{
	@Autowired
	DataSongTextRepositories dataSongTextRepositories;
	@Autowired
	DataSongListRepositories dataSongListRepositories ;

	public void insert(List<DataSongText> songTexts,DataSongText dataSongText)
	{
		if(dataSongText.getSongsequence() > 0)
		{
			songTexts.stream().filter((c) -> (c.getSongsequence() >= dataSongText.getSongsequence()))
							.forEach((c)-> c.setSongsequence(c.getSongsequence()+1));
		}
		else
		{
			dataSongText.setSongsequence(new Long((songTexts.size() + 1)));
		}
		songTexts.add(dataSongText);
		update(songTexts);
	}
	public void delete(List<DataSongText> songTexts,Long id)
	{
		Optional<DataSongText> optional = dataSongTextRepositories.findById(id);


		List<DataSongText> retSongTexts = new ArrayList<DataSongText>();
		if (optional.isPresent()) {
			DataSongText deleteDataSongText = optional.get();
			dataSongTextRepositories.delete(deleteDataSongText);
			for (DataSongText dataSongText : songTexts) {
				if(!dataSongText.getID().equals(deleteDataSongText.getID()))
				{
					if(dataSongText.getSongsequence() >= deleteDataSongText.getSongsequence())
					{
						dataSongText.setSongsequence(dataSongText.getSongsequence() - 1);
					}
					retSongTexts.add(dataSongText);
				}
			}
		}
		update(retSongTexts);
	}
	public void update(List<DataSongText> list)
	{
		dataSongTextRepositories.saveAll(list);
		dataSongTextRepositories.flush();
	}
}