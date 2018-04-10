package com.kusobotmaker.Data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import twitter4j.TwitterException;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "log")
public class DataLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@Getter@Setter
	private Long id;

	public DataLog(Long botId, TwitterException e) {
		super();
		this.botId = botId;
		this.time = new Date();
		if(e.getErrorCode() > 0)
		{
			this.error_code = e.getErrorCode();
			this.messege = e.getErrorMessage();
		}
		else
		{
			this.error_code = e.getStatusCode();
			this.messege = e.getMessage();
		}
	}
	@Column(name = "bot_id")
	@Getter@Setter
	private Long botId;

	@Column(name = "time")
	@Getter@Setter
	private Date time;

	@Column(name = "error_code")
	@Getter@Setter
	private Integer error_code;

	@Column(name = "messege")
	@Getter@Setter
	private String messege;

	public DataLog(Long bot_id, int i, String message) {
		super();
		this.botId = bot_id;
		this.time = new Date();
		this.error_code = i;
		this.messege = message;
	}






}
