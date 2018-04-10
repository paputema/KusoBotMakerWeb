package com.kusobotmaker.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kusobotmaker.Data.DataBotAccountLastId;

public interface DataBotAccountLastIdRepositories extends JpaRepository<DataBotAccountLastId, Long> {
	//public List<DataPosttable> findAllByBotIdAndModeName(Long BotID,String ModeName);

}
