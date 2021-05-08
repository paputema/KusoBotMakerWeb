package com.kusobotmaker.repositories;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.kusobotmaker.Data.DataLog;

public interface DataLogtRepositories extends JpaRepository<DataLog, Long> {
	//public List<DataPosttable> findAllByBotIdAndModeName(Long BotID,String ModeName);
	List<DataLog> findTop100ByBotIdOrderByIdDesc(Long botId);
	List<DataLog> findAllByTimeBefore(Date date);
	default void deleteOld()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		Date date = calendar.getTime();
		//List<DataLog> dataLogs = findByTimeBefore(date);
		//delete(dataLogs);
		deletedeleteOld(date);
	}
	List<DataLog> findByTimeBefore(Date time);


	@Transactional
	@Modifying
	@Query("delete from DataLog l where l.time < ?1")
	void deletedeleteOld(Date date);

}
