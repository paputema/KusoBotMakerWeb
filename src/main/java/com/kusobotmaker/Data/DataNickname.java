package com.kusobotmaker.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nickname")
public class DataNickname {

	@Id
	@Column
	Long id;
	@Column(name="bot_id")
	Long botId;
	@Column(name="friends_id")
	Long friendsId;
	@Column(name="nickname")
	String nickName;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getBotId() {
		return botId;
	}
	public void setBotId(Long botId) {
		this.botId = botId;
	}
	public Long getFriendsId() {
		return friendsId;
	}
	public void setFriendsId(Long friendsId) {
		this.friendsId = friendsId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}


}
