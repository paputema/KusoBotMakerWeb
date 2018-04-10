package com.kusobotmaker.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kusobotmaker.Data.DataUser;

public interface DataUserRepositories extends JpaRepository<DataUser, Long> {
	//public List<DataPosttable> findAllByBotIdAndModeName(Long BotID,String ModeName);

}
