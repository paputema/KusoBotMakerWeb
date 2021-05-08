package com.kusobotmaker.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusobotmaker.Data.DataPosttable;

public interface DataPosttableRepositories extends JpaRepository<DataPosttable, Long> {

	public List<DataPosttable> findAllByBotId(Long botId);
	public List<DataPosttable> findAllByBotIdAndId(Long BotId,Long Id);
	public default void deleteByBotIDAndTweetId(Long BotId,Long Id)
	{
		deleteAll(findAllByBotIdAndId(BotId, Id));
	}

}
