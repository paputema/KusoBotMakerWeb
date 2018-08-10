package com.kusobotmaker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusobotmaker.Data.DataGlobalSearch;

public interface DataGlobalSearchRepositories extends JpaRepository<DataGlobalSearch, Long> {
	List<DataGlobalSearch> findAllByBotIdOrderByLastUseAsc(Long botid);
	List<DataGlobalSearch> findAllByBotId(Long botid);

}
