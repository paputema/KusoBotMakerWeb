package com.kusobotmaker.view;
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

import com.kusobotmaker.KusoBotMakerWebAppUsers.KbmUser;
import com.kusobotmaker.Bot;
import com.kusobotmaker.KusoBotMakerWebAppDataReps;
import com.kusobotmaker.Data.DataAccountMode;
import com.kusobotmaker.Form.ModesForm;


@Controller
public class ViewMode {
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
		addObjectBySession(mav,"modeAddForm", new DataAccountMode());
		addObjectBySession(mav,"modesForm", new ModesForm());
		addObjectBySession(mav,"bots",new HashSet<>());
	}

	@RequestMapping(value="/mode/updateMode" , method = RequestMethod.POST)
	public ModelAndView updateMode(@Validated ModesForm modesForm, BindingResult result, Locale locale, ModelAndView mav) {
		mav.setViewName("redirect:/mode/" + session.getAttribute("botId"));
		for (DataAccountMode mode : modesForm.getModes()) {
			mode.toString();
			dataReps.dataAccountModeRepositories.saveAndFlush(mode);
		}

		return mav;
	}
	@RequestMapping(value="/mode/modeAddForm" , method = RequestMethod.POST)
	public ModelAndView modeAddForm(@Validated DataAccountMode modeAddForm, BindingResult result, Locale locale, ModelAndView mav) {
		mav.setViewName("redirect:/mode/" + session.getAttribute("botId"));
		modeAddForm.setBotId((Long) session.getAttribute("botId"));
		dataReps.dataAccountModeRepositories.saveAndFlush(modeAddForm);
		return mav;
	}
	@RequestMapping(value="/mode/{botId}" , method = RequestMethod.GET)
	public ModelAndView modeGet(@PathVariable Long botId, ModelAndView mav) {
		mav.setViewName("BotOAuth");
		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		if(kbmUser != null)
		{
			Bot bot = kbmUser.getBot(botId);
			if(bot != null)
			{
				session.setAttribute("modesForm", new ModesForm(dataReps.dataAccountModeRepositories.findByBotId(bot.getBotId())));
				session.setAttribute("botId", botId);
				session.setAttribute("bot", bot);
				session.setAttribute("modeAddForm", new DataAccountMode());
			}
		}
		setDefaultObjectBySession(mav);
		mav.setViewName("mode");
		return mav;
	}
}
