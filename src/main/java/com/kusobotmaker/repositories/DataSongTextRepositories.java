package com.kusobotmaker.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kusobotmaker.Data.DataSongText;

public interface DataSongTextRepositories extends JpaRepository<DataSongText, Long> {
	//public List<DataPosttable> findAllByBotIdAndModeName(Long BotID,String ModeName);
	List<DataSongText> findBySongidOrderBySongsequenceAsc(Long songID);
}
