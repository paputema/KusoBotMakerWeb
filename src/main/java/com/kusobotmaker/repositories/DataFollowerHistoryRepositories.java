package com.kusobotmaker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusobotmaker.Data.DataFollowerHistory;

public interface DataFollowerHistoryRepositories extends JpaRepository<DataFollowerHistory, DataFollowerHistoryKey>{
	List<DataFollowerHistory> findAllByUserIdOrderByDateDesc(Long userId);
}
