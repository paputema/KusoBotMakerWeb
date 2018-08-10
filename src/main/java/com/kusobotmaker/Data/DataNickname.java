package com.kusobotmaker.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import twitter4j.User;

@Entity
@Table(name = "nickname")
@Data
public class DataNickname {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="bot_id")
	final private Long botId;
	@Column(name="friends_id")
	final private Long friendsId;
	@Column(name="nickname")
	private String nickName;
	
	public DataNickname() {
		super();
		this.botId = 0L;
		this.friendsId = 0l;
	}
	public DataNickname(Long botId, User friends) {
		super();
		this.botId = botId;
		this.friendsId = friends.getId();
		this.nickName = friends.getName();
	}
	

}
