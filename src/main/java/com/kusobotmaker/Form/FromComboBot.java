package com.kusobotmaker.Form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.kusobotmaker.Bot;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class FromComboBot implements Serializable  {

	private Long botId;
	private String botName;

	public FromComboBot(Bot bot) {
		this.botId = bot.getBotId() ;
		this.botName = bot.getBotName();
	}
	public static Collection<FromComboBot> getComboBotList(Collection<Bot> bots)
	{
		Collection<FromComboBot> retComboBots = new ArrayList<>();
		for (Bot bot : bots) {
			retComboBots.add(new FromComboBot(bot));
		}

		return retComboBots;
	}
}
