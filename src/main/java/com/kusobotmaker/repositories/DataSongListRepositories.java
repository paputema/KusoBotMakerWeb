package com.kusobotmaker.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kusobotmaker.Data.DataSongList;

public interface DataSongListRepositories extends JpaRepository<DataSongList, Long> {
	//public List<DataPosttable> findAllByBotIdAndModeName(Long BotID,String ModeName);
}
