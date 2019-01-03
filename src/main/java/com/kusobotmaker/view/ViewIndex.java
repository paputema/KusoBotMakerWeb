package com.kusobotmaker.view;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kusobotmaker.Bot;
import com.kusobotmaker.BotsScheduler;
import com.kusobotmaker.KusoBotMakerWebAppDataReps;
import com.kusobotmaker.KusoBotMakerWebAppUsers;
import com.kusobotmaker.KusoBotMakerWebAppUsers.KbmUser;
import com.kusobotmaker.Form.Consumer;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


@Controller
public class ViewIndex {


	@Autowired
	private HttpSession session;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private BotsScheduler appCron;
	private String domain;

	@Autowired
	private KusoBotMakerWebAppUsers users;
	@Autowired
	KusoBotMakerWebAppDataReps reps;

	private void addObjectBySession(ModelAndView mav,String attName,Object defobject)
	{
		Object object = session.getAttribute(attName);
		mav.addObject(attName,(object != null) ? object : defobject);
	}

	private void setDefaultObjectBySession(ModelAndView mav)
	{
		addObjectBySession(mav,"consumers", new Consumer());
		addObjectBySession(mav,"bots",new HashSet<>());
		addObjectBySession(mav,"faileds",reps.getFailedBotAccount());
	}


	@RequestMapping({"/","index"})
	public ModelAndView index(ModelAndView mav) {
		domain = request.getRequestURL().toString();
		setDefaultObjectBySession(mav);
		mav.setViewName("index");
		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		if(kbmUser != null)
		{
			mav.setViewName("redirect:/BotOAuth");
		}
		return mav;
	}
	@RequestMapping("/userlogin")
	public ModelAndView userLogin(ModelAndView mav) {
		setDefaultObjectBySession(mav);
		try {
			//mav.addObject("force_login", true);
			ConfigurationBuilder builder = new ConfigurationBuilder();
			OAuthAuthorization oauth = new OAuthAuthorization(builder.build());
			String callbackURL = domain +"accessTokenUser";
			session.setAttribute("requestTokenUser", oauth.getOAuthRequestToken(callbackURL));
			RequestToken requestToken = (RequestToken) session.getAttribute("requestTokenUser");
			mav.setViewName("redirect:" + requestToken.getAuthenticationURL());
		} catch (TwitterException e) {
			e.printStackTrace();
			mav.addObject("error", e.toString());
			mav.setViewName("index");
		}
		return mav;
	}
	@RequestMapping(value ="accessTokenUser")
	public ModelAndView accessToken(ModelAndView mav) {
		try {
			setDefaultObjectBySession(mav);
			RequestToken requestToken = (RequestToken) session.getAttribute("requestTokenUser"); //$NON-NLS-1$
			AccessToken accessToken = new AccessToken(requestToken.getToken(), requestToken.getTokenSecret());
			OAuthAuthorization oath = createOAuthAuthorization();
			oath.setOAuthAccessToken(accessToken);
			String verifier = request.getParameter("oauth_verifier");
			accessToken = oath.getOAuthAccessToken(verifier);
			Twitter twitter = new TwitterFactory(createConfiguration()).getInstance(accessToken);
			KbmUser kbmUser = users.getUser(twitter);
			session.setAttribute("kbmUser", kbmUser);
			session.setAttribute("userTwitter", twitter);
			User user = twitter.verifyCredentials();
			session.setAttribute("user",user.getName() + "(@" + user.getScreenName() + "/" + user.getId());
			if(kbmUser.isVerify())
			{
				mav.setViewName("redirect:BotOAuth");
			}else
			{
				mav.setViewName("redirect:/");
			}
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			mav.addObject("error", e.toString());
			mav.setViewName("redirect:/");
		}
		return mav;
	}
	@RequestMapping(value ="requestTokenBot", method = RequestMethod.POST)
	public ModelAndView releaseRequestToken(ModelAndView mav) {


		try {
			setDefaultObjectBySession(mav);
			mav.addObject("force_login", true);
			ConfigurationBuilder builder = new ConfigurationBuilder();
			OAuthAuthorization oauth = new OAuthAuthorization(builder.build());
			session.setAttribute("oauth", oauth);
			String callbackURL = domain +"accessTokenBot";
			session.setAttribute("requestToken", oauth.getOAuthRequestToken(callbackURL));
			RequestToken requestToken = (RequestToken) session.getAttribute("requestToken");
			mav.setViewName("redirect:" + requestToken.getAuthenticationURL());
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			mav.addObject("error", e.toString());
			mav.setViewName("BotOAuth"); //$NON-NLS-1$
		}

		return mav;
	}
	@RequestMapping(value ="accessTokenBot")
	public ModelAndView releaseaccessToken(ModelAndView mav) {
		setDefaultObjectBySession(mav);
		try {
			session.setMaxInactiveInterval(36000);
			RequestToken requestToken = (RequestToken) session.getAttribute("requestToken"); //$NON-NLS-1$
			AccessToken accessToken = new AccessToken(requestToken.getToken(), requestToken.getTokenSecret());
			OAuthAuthorization oauth = (OAuthAuthorization)session.getAttribute("oauth");
			ConfigurationBuilder builder = new ConfigurationBuilder();
			String verifier = request.getParameter("oauth_verifier");

			builder.setOAuthAccessToken(accessToken.getToken());
			builder.setOAuthAccessTokenSecret(accessToken.getTokenSecret());

			accessToken = oauth.getOAuthAccessToken(verifier);
			Twitter botTwitter = new TwitterFactory(builder.build()).getInstance(accessToken);
			Twitter userTwitter = (Twitter)session.getAttribute("userTwitter");
			appCron.addBot(userTwitter,botTwitter);
			KbmUser kbmUser = users.getUser(userTwitter);
			session.setAttribute("kbmUser", kbmUser);
			mav.setViewName("redirect:BotOAuth");
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			mav.addObject("error", e.toString());
			mav.setViewName("index");
		}
		return mav;
	}
	@RequestMapping(value ="BotOAuth")
	public ModelAndView botOAuth(ModelAndView mav) {
		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		if(kbmUser != null)
		{
			session.setMaxInactiveInterval(36000);
			session.setAttribute("bots",kbmUser.getBots());
			session.setAttribute("faileds",reps.getFailedBotAccount(kbmUser));
			mav.setViewName("BotOAuth");
		}else
		{
			mav.setViewName("redirect:/");
		}
		setDefaultObjectBySession(mav);
		return mav;
	}
	@RequestMapping(value="/deletebot/{botId}" , method = RequestMethod.GET)
	public ModelAndView twieetGet(@PathVariable Long botId, ModelAndView mav) {
		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		if(kbmUser != null)
		{
			Bot bot = kbmUser.getBot(botId);

			if(bot != null)
			{
				bot.delete();
			}
		}
		mav.setViewName("redirect:/BotOAuth");
		return mav;
	}

	private OAuthAuthorization createOAuthAuthorization() {
		return new OAuthAuthorization(createConfiguration());
	}

	private Configuration createConfiguration() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthAccessToken(null);
		builder.setOAuthAccessTokenSecret(null);
		return (builder.build());
	}

}
