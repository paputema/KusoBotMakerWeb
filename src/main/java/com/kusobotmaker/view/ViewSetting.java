package com.kusobotmaker.view;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kusobotmaker.Bot;
import com.kusobotmaker.KusoBotMakerWebAppDataReps;
import com.kusobotmaker.KusoBotMakerWebAppUsers.KbmUser;



@Controller
public class ViewSetting {

	@Autowired
	private HttpSession session;
	@Autowired
	private KusoBotMakerWebAppDataReps dataReps;

	@RequestMapping(value="/Setting/{botId}" , method = RequestMethod.GET)
	public ModelAndView twieetGet(@PathVariable Long botId, ModelAndView mav) {
		mav.setViewName("BotOAuth");
		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		if(kbmUser != null)
		{
			Bot bot = kbmUser.getBot(botId);
			if(bot != null)
			{
				mav.addObject("bot", bot);
				mav.addObject("logs", dataReps.dataLogtRepositories.findTop100ByBotIdOrderByIdDesc(bot.getBotId()));
			}
		}
		mav.setViewName("Setting");
		return mav;
	}
}
