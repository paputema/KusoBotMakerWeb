package com.kusobotmaker.view;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kusobotmaker.Bot;
import com.kusobotmaker.KusoBotMakerWebAppDataReps;
import com.kusobotmaker.KusoBotMakerWebAppUsers.KbmUser;
import com.kusobotmaker.Data.DataPosttable;
import com.kusobotmaker.Form.TweetsForm;

@Controller
public class ViewTweet {

	@Autowired
	private HttpSession session;
	@Autowired
	private KusoBotMakerWebAppDataReps dataReps;

	private void addObjectBySession(ModelAndView mav,String attName,Object defobject)
	{
		Object object = session.getAttribute(attName);
		mav.addObject(attName,(object != null) ? object : defobject);
	}

	private void setDefaultObjectBySession(ModelAndView mav)
	{
		addObjectBySession(mav,"addTweet", new DataPosttable());
		addObjectBySession(mav,"songsCombo", new HashMap<Long,String>());
		addObjectBySession(mav,"modesCombo", new ArrayList<String>());
		addObjectBySession(mav,"tweetsForm", new TweetsForm());
		addObjectBySession(mav,"bots",new HashSet<>());
	}


	@RequestMapping(value="/tweet/addTweet" , method = RequestMethod.POST)
	public ModelAndView addTweet(@Validated DataPosttable addTweet, BindingResult result, Locale locale, ModelAndView mav) {
		mav.setViewName("redirect:/tweet/" + session.getAttribute("botId"));
		addTweet.setBotId((Long) session.getAttribute("botId"));
		dataReps.dataPosttableRepositories.saveAndFlush(addTweet);
		mav.addObject("addTweet", addTweet);
		return mav;
	}

	@RequestMapping(value="/tweet/updateTweet" , method = RequestMethod.POST)
	public ModelAndView updateTweet(@Validated TweetsForm tweetsForm, BindingResult result, Locale locale, ModelAndView mav) {
		mav.setViewName("redirect:/tweet/" + session.getAttribute("botId"));
		tweetsForm.getTweets().stream().filter((c) -> c.isSong() == false ).forEach((c) -> c.setSong_ID(-1L));

		dataReps.dataPosttableRepositories.saveAll(tweetsForm.getTweets());
		mav.addObject("tweetsForm", tweetsForm);
		return mav;
	}

	@RequestMapping(value="/tweet/deleteTweet/{tweetId}",method = RequestMethod.GET)
	public ModelAndView deleteTweet(@PathVariable Long tweetId, ModelAndView mav) {
		mav.setViewName("redirect:/BotOAuth");
		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		Long botId = (Long) session.getAttribute("botId");
		if(kbmUser != null)
		{
			Bot bot = kbmUser.getBot(botId);
			if(bot != null)
			{
				dataReps.dataPosttableRepositories.deleteByBotIDAndTweetId(botId,tweetId);
				setDefaultObjectBySession(mav);
				mav.setViewName("redirect:/tweet/" + botId);
			}
		}
		return mav;
	}

	@RequestMapping(value="/tweet/{botId}" , method = RequestMethod.GET)
	public ModelAndView twieetGet(@PathVariable Long botId, ModelAndView mav) {
		mav.setViewName("redirect:/BotOAuth");
		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		if(kbmUser != null)
		{
			Bot bot = kbmUser.getBot(botId);
			if(bot != null)
			{
				session.setAttribute("tweetsForm", new TweetsForm(dataReps.dataPosttableRepositories.findAllByBotId(bot.getBotId())));
				session.setAttribute("botId", bot.getBotId());
				session.setAttribute("modesCombo", dataReps.getBotModeList(bot.getBotId()));
				session.setAttribute("songsCombo", dataReps.getSongListByBotId(bot.getBotId()));
			}
		}
		setDefaultObjectBySession(mav);
		mav.setViewName("tweet");
		return mav;
	}
}
