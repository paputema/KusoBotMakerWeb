package com.kusobotmaker.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "song_list")
@Data
public class DataSongList {
	public DataSongList(String songTitle2) {
		this.songTitle =songTitle2;
	}
	public DataSongList() {
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "song_id")
	private Long songID;

	@Column(name = "song_title")
	private String songTitle;
}
