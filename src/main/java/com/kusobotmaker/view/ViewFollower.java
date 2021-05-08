package com.kusobotmaker.view;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kusobotmaker.Bot;
import com.kusobotmaker.KusoBotMakerWebAppUsers.KbmUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import twitter4j.PagableResponseList;
import twitter4j.RateLimitStatus;
import twitter4j.TwitterException;
import twitter4j.User;

@Controller
public class ViewFollower {
	@Autowired
	private HttpSession session;

	@CrossOrigin(origins = {"http://localhost:8081","http://localhost:8080"})
	@RequestMapping(value = "/getFollower", method = { RequestMethod.GET })
	public ResponseEntity<Followers> getFollower(
			@RequestParam(name = "cursor", defaultValue = "-1") Long cursor) { // ←ポイントです
		Followers ret = null;

		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		if(kbmUser != null)
		{
			Long botId = (Long) session.getAttribute("botId");
			Bot bot = kbmUser.getBot(botId);
			if(bot != null)
			{
				try {
					 ret = new Followers(bot.getTwitter().friendsFollowers().getFollowersList(botId, cursor,200));
				} catch (TwitterException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
					ret = new Followers(e.getRateLimitStatus());
				}
			}
		}
		return ResponseEntity.ok(ret);

	}
	@CrossOrigin(origins = {"http://localhost:8081","http://localhost:8080"})
	@RequestMapping(value = "/block", method = { RequestMethod.GET })
	public ResponseEntity<BlockResult> Block(
			@RequestParam(name = "target") Long target) { // ←ポイントです
		BlockResult ret = null;
		ResponseEntity<BlockResult> retRes = null;

		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		if(kbmUser != null)
		{
			Long botId = (Long) session.getAttribute("botId");
			Bot bot = kbmUser.getBot(botId);
			if(bot != null)
			{
				try {
					User resurlt=  bot.getTwitter().createBlock(target);
					ret.setResurlt(resurlt);
					retRes = ResponseEntity.ok(ret);
					for(Bot botOther: kbmUser.getBots())
					{
						botOther.getTwitter().createBlock(target);
					}

				} catch (TwitterException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
					ret.setE(e);
					retRes = ResponseEntity.ok(ret);
					retRes.notFound();
				}
			}
		}

		return retRes;

	}
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	private class BlockResult{
		User resurlt;
		TwitterException e;
	}


	@RequestMapping(value="/follower/{botId}" , method = RequestMethod.GET)
	public ModelAndView twieetGet(@PathVariable Long botId, ModelAndView mav) {
		mav.setViewName("redirect:/BotOAuth");
		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		Bot bot = kbmUser.getBot(botId);
		session.setAttribute("botId", botId);
		mav.setViewName("Follower/index");
		mav.addObject("bot", bot);
		return mav;
	}
	@Data
	@AllArgsConstructor
	private class Followers{
		public Followers(PagableResponseList<User> followersList) {
			// TODO 自動生成されたコンストラクター・スタブ
			this.users = followersList;
			this.cursor = followersList.getNextCursor();
			this.rateLimitStatus =  followersList.getRateLimitStatus();
		}
		public Followers(RateLimitStatus rateLimitStatus) {
			this.rateLimitStatus = rateLimitStatus;
		}
		PagableResponseList<User> users;
		Long cursor;
		RateLimitStatus rateLimitStatus;
	}
}
