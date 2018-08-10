package com.kusobotmaker.view;
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
import com.kusobotmaker.Form.SettingForm;;


@Controller
public class ViewSetting {
	@Autowired
	private HttpSession session;
	private void addObjectBySession(ModelAndView mav,String attName,Object defobject)
	{
		Object object = session.getAttribute(attName);
		mav.addObject(attName,(object != null) ? object : defobject);
	}

	private void setDefaultObjectBySession(ModelAndView mav)
	{
		addObjectBySession(mav,"updateSettingForm", new SettingForm());
		addObjectBySession(mav,"botId", new Long(0));
	}

	@RequestMapping(value="/setting/updateSetting" , method = RequestMethod.POST)
	public ModelAndView updateMode(@Validated SettingForm updateSettingForm, BindingResult result, Locale locale, ModelAndView mav) {
		mav.setViewName("redirect:/setting/" + updateSettingForm.getDataFollowRequest().getBotId());
		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		Bot bot = kbmUser.getBot(updateSettingForm.getDataFollowRequest().getBotId());
		bot.setFollowRequest(updateSettingForm.getDataFollowRequest());
		return mav;
	}
	@RequestMapping(value="/setting/{botId}" , method = RequestMethod.GET)
	public ModelAndView modeGet(@PathVariable Long botId, ModelAndView mav) {
		mav.setViewName("BotOAuth");
		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		if(kbmUser != null)
		{
			Bot bot = kbmUser.getBot(botId);
			if(bot != null)
			{
				session.setAttribute("updateSettingForm",new SettingForm(bot.getFollowRequest()));
				setDefaultObjectBySession(mav);
				mav.setViewName("setting");
			}
		}
		return mav;
	}

}
