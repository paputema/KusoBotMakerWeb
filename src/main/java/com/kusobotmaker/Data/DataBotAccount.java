package com.kusobotmaker.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
@Data
@Entity
@Table(name = "accesstokentable_test")
@Getter
public class DataBotAccount {
	@Id
	@Column(name = "bot_id")
	Long bot_id;
	@Column
	String bot_screen_name;
	@Column
	String Consumer_Key;
	@Column
	String Consumer_Secret;
	@Column
	String Access_Token;
	@Column
	String Access_Token_Secret;
	@Column
	boolean bot_enable;
	@Column
	String  mode_name;
	@Column
	Long normal_post_interval;
	@Column
	Long pause_time;
	@Column
	boolean replytoRT;
	@Column(name = "owner_id")
	Long owner_id;
	public Long getOwner_id() {
		return owner_id;
	}
	public void setOwner_id(Long owner_id) {
		this.owner_id = owner_id;
	}
	@Override
	public int hashCode() {
		return bot_id.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj != null)
		{
			return this.bot_id.equals(((DataBotAccount)obj).getBot_id());
		}
		return super.equals(obj);
	}
}
