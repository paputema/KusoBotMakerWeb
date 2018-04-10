package com.kusobotmaker.Data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_table")
public class DataUser {
	public DataUser() {
		super();
		this.lastLogin = new Date();
	}

	public DataUser(Long userId, boolean verify) {
		super();
		this.userId = userId;
		this.verify = verify;
		this.lastLogin = new Date();
	}

	@Id
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "verify")
	private boolean verify;

	@Column(name = "last_login")
	private Date lastLogin;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean isVerify() {
		return verify;
	}

	public void setVerify(boolean verify) {
		this.verify = verify;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
}
