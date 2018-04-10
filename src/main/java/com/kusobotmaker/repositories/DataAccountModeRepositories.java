package com.kusobotmaker.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusobotmaker.Data.DataAccountMode;

public interface DataAccountModeRepositories extends JpaRepository<DataAccountMode, Long> {
	//public List<DataPosttable> findAllByBotIdAndModeName(Long BotID,String ModeName);
	List<DataAccountMode> findByBotId(Long bot_id);
}
