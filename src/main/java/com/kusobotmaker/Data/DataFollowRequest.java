package com.kusobotmaker.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "followrequest")
@NoArgsConstructor
@AllArgsConstructor
public class DataFollowRequest {
	@Id
	@Column(name = "bot_id")
	Long botId;
	@Column(name = "followrequesttext")
	String followRequestText = "フォローして";
	@Column(name = "removerequesttext")
	String removeRequestText = "フォロー解除して";
	@Column(name = "nicknamerequesttext")
	String nicknameRequestText = "「(.*)」って呼んで";
	@Column(name = "nicknamegroupnum")
	Long nicknameGroupNum = 1L;
	public DataFollowRequest (Long botId) {
		this.botId = botId;
	}
	public String getUserDescription() {
		return "「" + followRequestText + "」でフォロー " + 
				"「" + removeRequestText + "」でフォロー解除 " +
				"「" + nicknameRequestText.replaceAll("\\(\\.\\*\\)", "○○") + "」で名前を覚えます。 ";
		
	}
}
