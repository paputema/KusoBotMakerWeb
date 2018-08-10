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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "global_search")
@Data
@NoArgsConstructor
public class DataGlobalSearch {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "bot_id")
	private Long botId;
	@Column
	private String searchstr= new String();
	@Column
	private String regtext= new String();
	@Column
	private String posttext = new String();
	@Column
	private Boolean rt = false;
	@Column
	private Boolean fav = false;
	@Column
	private Long sinceid = new Long(0);
	@Column(name = "last_use")
	private java.sql.Timestamp lastUse = new Timestamp(new Date().getTime());	
}
