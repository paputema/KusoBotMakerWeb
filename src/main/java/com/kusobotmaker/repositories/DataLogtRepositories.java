package com.kusobotmaker.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusobotmaker.Data.DataLog;

public interface DataLogtRepositories extends JpaRepository<DataLog, Long> {
	//public List<DataPosttable> findAllByBotIdAndModeName(Long BotID,String ModeName);
	List<DataLog> findTop100ByBotIdOrderByIdDesc(Long botId);

}
