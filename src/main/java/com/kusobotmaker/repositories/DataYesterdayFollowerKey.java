package com.kusobotmaker.repositories;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataYesterdayFollowerKey  implements Serializable{
	private Long userId;
	private Long followerId;

}
