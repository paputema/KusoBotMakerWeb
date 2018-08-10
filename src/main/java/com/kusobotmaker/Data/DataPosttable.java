package com.kusobotmaker.Data;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "posttable")
public class DataPosttable {
	public DataPosttable() {
		super();
		setLastUse(new Timestamp(new Date().getTime()));
		setAir(false);
		setDelay(10L);
		setFav(false);
		setFollow(false);
		setLoopLimit(10L);
		setModeName("通常");
		setPriority(10L);
		setRT(false);
		setTw4Me(true);
		setTw4Other(false);
		setTw4Tl(true);
		setSong(false);
		setSong_ID(-1L);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@Column(name = "id")
	private Long id;

	@Column(name = "bot_id")
	private Long botId;

	@Column(name = "last_use")
	private java.sql.Timestamp LastUse;
	//Data

	@Column(name = "search_str")
	private String SearchStr;

	@Column(name = "post_str")
	private String PostStr;

	@Column(name = "air")
	private Boolean Air;

	@Column(name = "delay")
	private Long Delay;

	@Column(name = "fav")
	private Boolean Fav;

	@Column(name = "follow")
	private Boolean Follow;

	@Column(name = "normalpost")
	private Boolean NormalPost;

	@Column(name = "looplimit")
	private Long LoopLimit;

	@Column(name = "mode_name")
	private String ModeName;

	@Column(name = "priority")
	private Long Priority;

	@Column(name = "rt")
	private Boolean RT;

	@Column(name = "song")
	private boolean Song;

	@Column(name = "song_id")
	private Long Song_ID;

	@Column(name = "tw4me")
	private Boolean Tw4Me;

	@Column(name = "tw4other")
	private Boolean Tw4Other;

	@Column(name = "tw4tl")
	private Boolean Tw4Tl;


}
