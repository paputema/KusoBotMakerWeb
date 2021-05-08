package com.kusobotmaker;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kusobotmaker.Bot.FollowerIds;
import com.kusobotmaker.Data.DataFollower;
import com.kusobotmaker.Data.DataFollowerHistory;
import com.kusobotmaker.Data.DataYesterdayFollower;
import com.kusobotmaker.repositories.DataYesterdayFollowerKey;

import twitter4j.TwitterException;
import twitter4j.User;

public class FollowHistory {
	static final Log LOG = LogFactory.getLog(FollowHistory.class);
	private Bot bot;
	public void updateFollowHistory()
	{
		bot.reps.updateFollowe();

		List<DataYesterdayFollower> dataYesterdayFollowers =  bot.reps.dataYesterdayFollowerRepositories.findAllByUserId(bot.getBotId());
		List<Long> yesterdayFollowersId = dataYesterdayFollowers.stream().map(p->p.getFollowerId()). collect(Collectors.toList());

		FollowerIds nowFollowersId = bot.getFollowerIds();

		if (nowFollowersId.isRet()) {
			List<Long> a = ListUtils.subtract(yesterdayFollowersId, nowFollowersId);

			a.stream().forEach(c -> out(c));
			List<Long> b = ListUtils.subtract(nowFollowersId, yesterdayFollowersId);
			b.stream().forEach(c -> in(c));
		}

	}
	public FollowHistory(Bot bot) {
		super();
		this.bot = bot;
	}
	private void in(Long FollowerId)
	{
		 bot.reps.dataYesterdayFollowerRepositories.saveAndFlush(new DataYesterdayFollower(bot.getBotId(),FollowerId));
		 bot.reps.dataFollowerHistoryRepositories.saveAndFlush(new DataFollowerHistory(bot.getBotId(),FollowerId,DataFollowerHistory.InOrOut.In));
		 showUser(FollowerId);

	}

	private void out(Long FollowerId)
	{
		 bot.reps.dataYesterdayFollowerRepositories.deleteById(new DataYesterdayFollowerKey(bot.getBotId(),FollowerId));
		 bot.reps.dataFollowerHistoryRepositories.saveAndFlush(new DataFollowerHistory(bot.getBotId(),FollowerId,DataFollowerHistory.InOrOut.Out));
		 showUser(FollowerId);
	}

	private void showUser(Long FollowerId)
	{
		 try {
			 Optional<DataFollower> optional = bot.reps.dataFollowerRepositories.findById(FollowerId);
			DataFollower dataFollower;
			if(optional.isPresent() == false)
			{
				User Follower = bot.showUser(FollowerId);
				dataFollower = new DataFollower(FollowerId, Follower);
				bot.reps.dataFollowerRepositories.saveAndFlush(dataFollower);
			}
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			bot.onTwitterException(e);
		}
	}

}
