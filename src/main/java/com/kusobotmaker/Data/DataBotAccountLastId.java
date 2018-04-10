package com.kusobotmaker.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import twitter4j.Status;

@Entity
@Table(name = "accountlastid")
@Getter@Setter@NoArgsConstructor
public class DataBotAccountLastId {
	public DataBotAccountLastId(Long botID,Status status) {
		super();
		this.botID = botID;
		if(status != null)
		{
			sinceIdStatusMentionsTimeLine = status.getId();
			sinceIdHomeTimeLine = status.getId();
		}else
		{
			sinceIdStatusMentionsTimeLine = -1L;
			sinceIdHomeTimeLine  = -1L;
		}
	}

	@Id
	@Column(name="bot_id")
	private Long botID;
	@Column(name="since_id_status_mentionst_timeline")
	private Long sinceIdStatusMentionsTimeLine;
	@Column(name="since_id_home_timeline")
	private Long sinceIdHomeTimeLine;

}
