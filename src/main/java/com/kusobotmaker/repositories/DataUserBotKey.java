package com.kusobotmaker.repositories;

import java.io.Serializable;

public class DataUserBotKey implements Serializable {

	public DataUserBotKey() {
		super();
	}
	public DataUserBotKey(Long userId, Long botId) {
		super();
		this.userId = userId;
		this.botId = botId;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -8951538055641897238L;
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

	private Long userId;
	private Long botId;
}