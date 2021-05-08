package com.kusobotmaker.Data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.kusobotmaker.repositories.DataFollowerHistoryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "follower_history")
@Getter
@IdClass(value=DataFollowerHistoryKey.class)
@NoArgsConstructor
@AllArgsConstructor
public class DataFollowerHistory implements Serializable{

	@PrePersist
	private void dateUpdate()
	{
		setDate(new Date());
	}

	public DataFollowerHistory(Long userId, Long followerId, InOrOut in) {
		setUserId(userId);
		setFollowerId(followerId);
		setInOrOut(in);
	}

	@AllArgsConstructor
	public enum InOrOut
	{
		In("フォロー"),
		Out("リムーブ");
		@Getter
		private final String property;
	}

	@Id
	@Column(name = "user_id")
	private Long userId;

	@Id
	@Column(name = "follower_id")
	private Long followerId;

	@Id
	@Column(name = "update_date")
	private Date date;

	@Column(name = "in_or_out")
	private InOrOut inOrOut;
}
