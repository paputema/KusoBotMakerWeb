package com.kusobotmaker.repositories;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataFollowerHistoryKey implements Serializable{


	private Long userId;
	private Long followerId;
	private Date date;


}
