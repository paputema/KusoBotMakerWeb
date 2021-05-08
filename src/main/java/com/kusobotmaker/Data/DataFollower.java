package com.kusobotmaker.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import twitter4j.User;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "follower")
public class DataFollower {
	@Id
	@Column(name = "follower_id")
	private Long followerId = new Long(0);

	@Column(name = "follower")
	private User follower;
}
