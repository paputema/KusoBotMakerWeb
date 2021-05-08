package com.kusobotmaker.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.kusobotmaker.repositories.DataYesterdayFollowerKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "yesterday_follower")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(value=DataYesterdayFollowerKey.class)
public class DataYesterdayFollower {
	@Id
	@Column(name = "user_id")
	private Long userId;

	@Id
	@Column(name = "follower_id")
	private Long followerId;


}
