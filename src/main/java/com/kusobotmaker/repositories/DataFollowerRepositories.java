package com.kusobotmaker.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kusobotmaker.Data.DataFollower;

import twitter4j.Status;

public interface DataFollowerRepositories  extends JpaRepository<DataFollower, Long> {


	default DataFollower save(Status status)
	{
		Optional<DataFollower> optional = findById(status.getUser().getId());
		DataFollower dataFollower;
		if(optional.isPresent())
		{
			dataFollower = optional.get();
			dataFollower.setFollower(status.getUser());
		} else
		{
			dataFollower = new DataFollower(status.getUser().getId(),status.getUser());
		}

		DataFollower ret = save(dataFollower);
		flush();
		return ret;
	}

}
