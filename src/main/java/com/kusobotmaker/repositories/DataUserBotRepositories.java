package com.kusobotmaker.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusobotmaker.Data.DataUserBot;

public interface DataUserBotRepositories extends JpaRepository<DataUserBot, DataUserBotKey> {
	//public List<DataPosttable> findAllByBotIdAndModeName(Long BotID,String ModeName);
	List<DataUserBot> findAllByBotId(Long botId);
	List<DataUserBot> findAllByUserId(Long userId);
	List<DataUserBot> findAllByBotIdAndOwnerIsTrue(Long botId);
}
