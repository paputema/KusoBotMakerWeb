package com.kusobotmaker.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.kusobotmaker.repositories.DataUserBotKey;

@Entity
@Table(name = "user_bot_table")
@IdClass(value=DataUserBotKey.class)
public class DataUserBot {
	@Id
	@Column(name = "user_id")
	private Long userId;

	@Id
	@Column(name = "bot_id")
	private Long botId;

	@Column(name = "owner")
	private boolean owner;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getBotId() {
		return botId;
	}

	public void setBotId(Long botId) {
		this.botId = botId;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public DataUserBot(Long userId, Long botId, boolean owner) {
		super();
		this.userId = userId;
		this.botId = botId;
		this.owner = owner;
	}

	public DataUserBot() {
		super();
	}
}
