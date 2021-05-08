package com.kusobotmaker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusobotmaker.Data.DataYesterdayFollower;

public interface DataYesterdayFollowerRepositories extends JpaRepository<DataYesterdayFollower, DataYesterdayFollowerKey> {
	List<DataYesterdayFollower> findAllByUserId(Long userId);
}
