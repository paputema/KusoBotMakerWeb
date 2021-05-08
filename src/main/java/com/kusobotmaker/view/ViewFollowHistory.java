package com.kusobotmaker.view;
import java.util.List;

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
import com.kusobotmaker.Data.DataFollowerHistory;



@Controller
public class ViewFollowHistory {

	@Autowired
	private HttpSession session;
	@Autowired
	private KusoBotMakerWebAppDataReps dataReps;





	@RequestMapping(value="/FollowHistory/{botId}" , method = RequestMethod.GET)
	public ModelAndView twieetGet(@PathVariable Long botId, ModelAndView mav) {
		mav.setViewName("BotOAuth");
		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		if(kbmUser != null)
		{
			Bot bot = kbmUser.getBot(botId);
			if(bot != null)
			{
				mav.addObject("bot", bot);
				List<DataFollowerHistory> FollowHistory = dataReps.dataFollowerHistoryRepositories.findAllByUserIdOrderByDateDesc(bot.getBotId());

				mav.addObject("FollowHistory",FollowHistory );
			}
		}
		mav.setViewName("FollowHistory");
		return mav;
	}
}
