package com.kusobotmaker.Form;

import java.io.Serializable;

import com.kusobotmaker.Data.DataFollowRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public class SettingForm implements Serializable
	{
		@Setter@Getter
		private DataFollowRequest dataFollowRequest = new DataFollowRequest();
	}

