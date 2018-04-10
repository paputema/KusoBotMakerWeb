package com.kusobotmaker.Form;

import java.util.ArrayList;
import java.util.Collection;

import com.kusobotmaker.Bot;
import lombok.Getter;
import lombok.Setter;

public class FromComboBot {
	public FromComboBot(Bot bot) {
		botId = bot.getBotId() ;
		botName = bot.getBotName();
	}
	public static Collection<FromComboBot> getComboBotList(Collection<Bot> bots)
	{
		Collection<FromComboBot> retComboBots = new ArrayList<>();
		for (Bot bot : bots) {
			retComboBots.add(new FromComboBot(bot));
		}

		return retComboBots;
	}

	@Setter
	@Getter
	private Long botId;
	@Setter
	@Getter
	private String botName;
}
