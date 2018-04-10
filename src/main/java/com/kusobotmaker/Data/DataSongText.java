package com.kusobotmaker.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kusobotmaker.Form.SongEditForm;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "song_text")
public class DataSongText {

	@Id
	@Setter@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long iD;
	public DataSongText() {
	}
	public DataSongText(SongEditForm songEditForm) {
		songid = songEditForm.getSongList().getSongID();
		songsequence = new Long( songEditForm.getSongTexts().size() + 1);
		Delay = 0L;
	}
	@Setter@Getter
	@Column(name = "song_id")
	private Long songid;
	@Setter@Getter
	@Column(name = "song_sequence")
	private Long songsequence;
	@Setter@Getter
	@Column(name = "bot_id")
	private Long BotId;
	@Setter@Getter
	@Column(name = "delay")
	private Long Delay;
	@Setter@Getter
	@Column(name = "post_str")
	private String PostStr;


}
