package com.kusobotmaker.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "accesstokentable_test")
public class DataBotAccount {

	public String getConsumer_Key() {
		return Consumer_Key;
	}
	public void setConsumer_Key(String consumer_Key) {
		Consumer_Key = consumer_Key;
	}
	public String getConsumer_Secret() {
		return Consumer_Secret;
	}
	public void setConsumer_Secret(String consumer_Secret) {
		Consumer_Secret = consumer_Secret;
	}
	public String getAccess_Token() {
		return Access_Token;
	}
	public void setAccess_Token(String access_Token) {
		Access_Token = access_Token;
	}
	public String getAccess_Token_Secret() {
		return Access_Token_Secret;
	}
	public void setAccess_Token_Secret(String access_Token_Secret) {
		Access_Token_Secret = access_Token_Secret;
	}
	public boolean isBot_enable() {
		return bot_enable;
	}
	public void setBot_enable(boolean bot_enable) {
		this.bot_enable = bot_enable;
	}
	public String getMode_name() {
		return mode_name;
	}
	public void setMode_name(String mode_name) {
		this.mode_name = mode_name;
	}
	public Long getNormal_post_interval() {
		return normal_post_interval;
	}
	public void setNormal_post_interval(Long normal_post_interval) {
		this.normal_post_interval = normal_post_interval;
	}
	public Long getPause_time() {
		return pause_time;
	}
	public void setPause_time(Long pause_time) {
		this.pause_time = pause_time;
	}
	public boolean isReplytoRT() {
		return replytoRT;
	}
	public void setReplytoRT(boolean replytoRT) {
		this.replytoRT = replytoRT;
	}

	public Long getBot_id() {
		return bot_id;
	}
	public void setBot_id(Long bot_iD) {
		this.bot_id = bot_iD;
	}
	@Id
	@Column(name = "bot_id")
	Long bot_id;
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
