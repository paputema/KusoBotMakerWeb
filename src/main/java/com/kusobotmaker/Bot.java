package com.kusobotmaker;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.kusobotmaker.Data.DataAccountMode;
import com.kusobotmaker.Data.DataBotAccount;
import com.kusobotmaker.Data.DataBotAccountLastId;
import com.kusobotmaker.Data.DataLog;
import com.kusobotmaker.Data.DataNickname;
import com.kusobotmaker.Data.DataPosttable;
import com.kusobotmaker.Data.DataSongText;

import lombok.Getter;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class Bot{
	static final Log LOG = LogFactory.getLog(Bot.class);
	KusoBotMakerWebAppDataReps reps;
	@PreDestroy
	private void destroy()
	{
		if(!execOnstatus.isShutdown())
		{
			execOnstatus.shutdownNow();
		}
		if(!execStatusPost.isShutdown())
		{
			execStatusPost.shutdownNow();
		}
	}

	public void delete() {
		reps.deleteBot(this);
	}

	private Twitter twitter;
	private DataBotAccount dataBotAccount;
	public Boolean equalsDataBotAccount(DataBotAccount dataBotAccount) {
		return (this.dataBotAccount.getBot_id().equals(dataBotAccount.getBot_id()) &&
				this.dataBotAccount.getConsumer_Key().equals(dataBotAccount.getConsumer_Key()) &&
				this.dataBotAccount.getConsumer_Secret().equals(dataBotAccount.getConsumer_Secret()) &&
				this.dataBotAccount.getAccess_Token().equals(dataBotAccount.getAccess_Token()) &&
				this.dataBotAccount.getAccess_Token_Secret().equals(dataBotAccount.getAccess_Token_Secret())
				);
	}

	private RateLimitStatus rateLimitStatusHomeTimeLine;
	private RateLimitStatus rateLimitStatusMentionsTimeLine;
	private DataBotAccountLastId lastId;

	private Date lastNomalPostTime = new Date(0);
	static private ExecutorService execOnstatus = Executors.newCachedThreadPool();
	static private ScheduledExecutorService execStatusPost = Executors.newSingleThreadScheduledExecutor();
	private User botUser;

	@Getter
	private String iconUrl;
	@Getter
	private String botScreenName;

	@Getter
	private String botName;
	@Getter
	private Long botId;

	private Pause pause = new Pause();
	class Pause{
		private boolean pauseFlag = false;
		private Date resetPauseTime = new Date();
		public void setPause()
		{
			pauseFlag = true;
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, dataBotAccount.getPause_time().intValue());
			resetPauseTime = calendar.getTime();
		}
		public Boolean isPause()
		{
			if(pauseFlag && resetPauseTime.before(new Date()))
			{
				pauseFlag = false;
				modeUpdate();
			}
			return pauseFlag;
		}
	}

	public void updateBotUser() throws TwitterException
	{
		botUser = twitter.verifyCredentials();
		botId = botUser.getId();
		botScreenName = botUser.getScreenName();
		botName = botUser.getName();
		iconUrl = botUser.getBiggerProfileImageURLHttps();

	}
	public Long getOwnerId() {
		return dataBotAccount.getOwner_id();
	}
	public void setOwnerId(Long ownerId) {
		this.dataBotAccount.setOwner_id(ownerId);
	}
	private Set<Long> SetNoReplyList = new HashSet<>();
	private User showUser(Long id) throws TwitterException
	{
		User user = reps.getUser(id);
		if(user == null)
		{
			user = twitter.showUser(id);
		}
		return user;
	}
	public boolean isBotEnable() {
		return dataBotAccount.isBot_enable();
	}
	public Bot(DataBotAccount dataBotAccount,KusoBotMakerWebAppDataReps reps)
	{
		this.reps = reps;
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(dataBotAccount.getConsumer_Key());
		builder.setOAuthConsumerSecret(dataBotAccount.getConsumer_Secret());
		builder.setOAuthAccessToken(dataBotAccount.getAccess_Token());
		builder.setOAuthAccessTokenSecret(dataBotAccount.getAccess_Token_Secret());
		this.twitter = new TwitterFactory(builder.build()).getInstance();
		this.dataBotAccount = dataBotAccount;
		
		try {
			updateBotUser();
			DataBotAccountLastId optLastId = reps.dataBotAccountLastIdRepositories.findOne(dataBotAccount.getBot_id()); 
			if(optLastId != null)
			{
				this.lastId = optLastId;
			}else
			{
				this.lastId = new DataBotAccountLastId(dataBotAccount.getBot_id(),botUser.getStatus());
			} 
			dataBotAccount.setBot_enable(true);
			modeUpdate();
			reps.delFailedBotAccount(dataBotAccount);
		} catch (TwitterException e) {
			onTwitterException(e);
			BotsScheduler.LOG.info(e.getErrorMessage() + ":" + e.getErrorCode());
			dataBotAccount.setBot_enable(false);
			this.lastId = new DataBotAccountLastId(dataBotAccount.getBot_id(),null);
			reps.dataBotAccountRepositories.saveAndFlush(dataBotAccount);
			reps.addFailedBotAccount(dataBotAccount);
		}

	}

	public void replyPost() {
		if(!pause.isPause())
		{
			BotsScheduler.LOG.debug("TL取得開始:" + dataBotAccount.getBot_id());
			getHomeTimeLine();
			getMentionsTimeLine();
			BotsScheduler.LOG.debug("TL取得終了:" + dataBotAccount.getBot_id());
			dataBotAccount.setBot_enable(true);
		}
	}
	private void onStatus(Status status)
	{
		reps.addReplyHistory(status);
		reps.putUser(status.getUser());
		if	(pause.isPause() || SetNoReplyList.contains(status.getId()) || botId.equals(status.getUser().getId()))
		{
			return;
		}
		if (status.isRetweet() == false || (status.isRetweet() && dataBotAccount.isReplytoRT())) {
			//DataPosttable dataPosttable = getPost(status);
			//execStatusPost.submit(task);

			DataPosttable posttable = reps.getReplyPost(dataBotAccount, status);
			if(posttable != null)
			{
				ReplyThread replyThread = getListStatusUpdate(posttable,status);
				execOnstatus.execute(replyThread);
			}
		}
	}
	private String getNickname(Status status){
		DataNickname dataNickname = reps.dataNicknameRepositories.findTopByBotIdAndFriendsId(getBotId(), status.getUser().getId());
		String nickname = status.getUser().getName();
		if(dataNickname != null && dataNickname.getNickName() != null)
		{
			nickname = dataNickname.getNickName();
		}
		return nickname;
	}
	private String getReplyNickname(Status status) {
		Long repToId = status.getInReplyToUserId();
		if(repToId == -1)
		{
			return "";
		}else{
			DataNickname dataNickname = reps.dataNicknameRepositories.findTopByBotIdAndFriendsId(getBotId(), repToId);
			if(dataNickname != null)
			{
				return dataNickname.getNickName();
			}else
			{
				try {
					return twitter.showUser(repToId).getName();
				} catch (TwitterException e) {
					// TODO 自動生成された catch ブロック
					onTwitterException(e);
					e.printStackTrace();
				}
			}

		}
		return "";
	}
	private String getReplyName(Status status)
	{
		return "@"+ status.getInReplyToScreenName();

	}
	private ReplyThread getListStatusUpdate(DataPosttable dataPosttable,Status status) {
		ReplyThread ret = null;
		if(dataPosttable != null)
		{
			ret = new ReplyThread(dataPosttable,status);
		}
		return ret;
	}

	class ReplyThread extends PostThread {
		DataPosttable dataPosttable;
		Status status;
		ReplyThread(DataPosttable dataPosttable, Status status) {
			this.dataPosttable = dataPosttable;
			this.status = status;
			if (dataPosttable.isSong()) {
				for (DataSongText songText : reps.dataSongTextRepositories.findBySongidOrderBySongsequenceAsc(dataPosttable.getSong_ID())) {
					updateStatus.add(new UpdateStatus(reps.getBot(songText.getBotId()), songText, dataPosttable, status));
					memberBot.add(reps.getBot(songText.getBotId()));
				}
			} else if (dataPosttable.getPostStr() != null && !dataPosttable.getPostStr().equals("") && !dataPosttable.getPostStr().contains("#stop#")) {
				for (String postText : dataPosttable.getPostStr().split("#next#")) {
					updateStatus.add(new UpdateStatus(reps.getBot(dataBotAccount.getBot_id()), postText, dataPosttable, status));
					memberBot.add(reps.getBot(dataBotAccount.getBot_id()));
				}
			}
		}
		@Override
		public void run() {
			if (dataPosttable.getRT() && !status.getUser().isProtected()) {
				try {
					twitter.retweetStatus(status.getId());
				} catch (TwitterException e) {
					onTwitterException(e);
				}
			}
			if (dataPosttable.getFav()) {
				try {
					twitter.createFavorite(status.getId());
				} catch (TwitterException e) {
					onTwitterException(e);
				}
			}
			if (dataPosttable.getFollow()) {
				try {
					createFriendship(status.getUser().getId());
				} catch (TwitterException e) {
					onTwitterException(e);
				}
			}
			super.run();
		}
	}
	private void createFriendship(long id) throws TwitterException
	{
		Relationship relation = twitter.showFriendship(twitter.getId(), id);
		if(relation.isSourceFollowingTarget() == false)
		{
			twitter.createFriendship(id);
		}
	}
	
	
	class UpdateStatus implements Callable<Status>
	{
		public UpdateStatus(Bot bot, String post,DataPosttable dataPosttable,Status status) {
			super();
			this.initReply(bot, post, dataPosttable, status,dataPosttable.getDelay());
		}

		public UpdateStatus(Bot bot,DataSongText songText,DataPosttable dataPosttable,Status status) {
			super();
			this.initReply(bot, songText.getPostStr(), dataPosttable, status,songText.getDelay());
		}
		private void initReply(Bot bot, String post,DataPosttable dataPosttable,Status status,Long delay)
		{
			this.status = status;
			this.bot = bot;
			this.delay = delay;

			post = post.replaceAll("#user_name#", getNickname(status));
			post = post.replaceAll("#reply_name#", getReplyNickname(status));
			post = post.replaceAll("@", "あっとまーく");
			post = post.replaceAll("#reply_at#", getReplyName(status));
			Pattern pattern = Pattern.compile(dataPosttable.getSearchStr());
			Matcher Matcher = pattern.matcher(status.getText());
			if (Matcher.find()) {
				int g = Matcher.groupCount();
				for (int i = 0; i <= g; i++) {
					if (Matcher.group(i) != null) {
						post = post.replaceAll("#group_" + i + "#", Matcher.group(i));
					}
				}
			}
			post = post.replaceAll("#group_[0-9]+#", "");
			if(!dataPosttable.getAir())
			{
				post = "@" + status.getUser().getScreenName() + " " + post;
			}

			this.statusUpdate = new StatusUpdate(post);
			if(!dataPosttable.getAir())
			{
				statusUpdate.setInReplyToStatusId(status.getId());
			}
		}
		public UpdateStatus(Bot bot, DataSongText songText, DataPosttable dataPosttable) {
			// TODO 自動生成されたコンストラクター・スタブ
			super();
			initNormal(bot,songText.getPostStr(),dataPosttable,songText.getDelay());

		}

		public UpdateStatus(Bot bot, String postText, DataPosttable dataPosttable) {
			// TODO 自動生成されたコンストラクター・スタブ
			super();
			initNormal(bot,postText,dataPosttable,dataPosttable.getDelay());
		}

		private void initNormal(Bot bot, String post,DataPosttable dataPosttable,Long delay)
		{
			this.status = null;
			this.bot = bot;
			this.delay = delay;

			post = post.replaceAll("#reply_name#", "");
			post = post.replaceAll("#reply_at#", "");
			post = post.replaceAll("@", "あっとまーく");
			post = post.replaceAll("#group_[0-9]+#", "");

			if(post.contains("#user_name#"))
			{
				try {
					long[] followrIds;

					followrIds = twitter.getFollowersIDs(-1L).getIDs();
					Random random = new Random(new Date().getTime());
					long followrId = followrIds[random.nextInt(followrIds.length)];
					DataNickname  targetUserNickname = reps.dataNicknameRepositories.findTopByBotIdAndFriendsId(getBotId(), followrId);
					User targetUser = showUser(followrId);
					if(targetUserNickname == null)
					{
						post = post.replaceAll("#user_name#", targetUser.getName());
					}else
					{
						post = post.replaceAll("#user_name#", targetUserNickname.getNickName());
					}
					if(!dataPosttable.getAir())
					{
						post = "@" + targetUser.getScreenName() + " " + post;
					}
					this.statusUpdate = new StatusUpdate(post);
					if(!dataPosttable.getAir() && status != null)
					{
						statusUpdate.setInReplyToStatusId(status.getId());
					}
				} catch (TwitterException e) {
					post = post.replaceAll("#user_name#", "");
					this.statusUpdate = new StatusUpdate(post);
				}
			}else{
				post = post.replaceAll("#user_name#", "");
				this.statusUpdate = new StatusUpdate(post);
			}
		}
		private Status status;
		private StatusUpdate statusUpdate;
		private Bot bot;
		private Long delay;

		@Override
		public Status call()  {
			return bot.updateStatus(statusUpdate);
		}
	}
	public Status updateStatus(StatusUpdate statusUpdate) {
		// TODO 自動生成されたメソッド・スタブ
		Status ret = null;
		try {
			ret = twitter.updateStatus(statusUpdate);
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			onTwitterException(e);
		}
		return ret;
	}

	public void normalPost(){
		if(!pause.isPause())
		{
			execOnstatus.submit(new normalPostThread());
		}
	}
	public void onTwitterException(TwitterException e) {

		reps.dataLogtRepositories.saveAndFlush(new DataLog(dataBotAccount.getBot_id(), e));
		switch (e.getErrorCode()) {
		case -1:
			break;
		case 88:
			rateLimitStatusHomeTimeLine = e.getRateLimitStatus() ;
			break;
		case 32 | 261 | 89 | 326:
			this.dataBotAccount.setBot_enable(false);
			reps.dataBotAccountRepositories.saveAndFlush(this.dataBotAccount);
			pause.setPause();
			break;
		case 187 | 188:
			reps.dataBotAccountRepositories.saveAndFlush(this.dataBotAccount);
			pause.setPause();
			break;
		default:
			reps.dataBotAccountRepositories.saveAndFlush(this.dataBotAccount);
			break;
		}
	}
	class PostThread extends Thread
	{
		List<UpdateStatus> updateStatus = new ArrayList<>();
		Set<Bot> memberBot = new HashSet<>();
		@Override
		public void run() {
			// TODO 自動生成されたメソッド・スタブ
			Long repid = null;
			super.run();
			for (UpdateStatus reply_i : updateStatus) {
				if(repid != null)
				{
					reply_i.statusUpdate.setInReplyToStatusId(repid);
				}
				Future<Status> future = execStatusPost.schedule(reply_i, reply_i.delay, TimeUnit.SECONDS);
				try {
					Status status;
					if((status = future.get()) != null)
					{
						SetNoReplyList.add(status.getId());
						repid = status.getId();
						for (Bot bot : memberBot) {
							bot.SetNoReplyList.add(status.getId());
							if(bot.twitter.getId() != status.getUser().getId())
							{
								bot.twitter.retweetStatus(status.getId());
							}
						}
						BotsScheduler.LOG.debug(future.get());
					}
				} catch (InterruptedException |ExecutionException e) {
					// TODO 自動生成された catch ブロック
					onException(e);
				} catch (TwitterException e) {
					// TODO 自動生成された catch ブロック
					onTwitterException(e);
				}
			}
		}
	}

	class normalPostThread extends PostThread
	{

		normalPostThread(){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lastNomalPostTime);
			calendar.add(Calendar.MINUTE,dataBotAccount.getNormal_post_interval().intValue());
			LOG.debug(calendar.getTime() + "/" + new Date());
			if(calendar.getTime().before(new Date()))
			{
				DataPosttable dataPosttable = reps.getNormalPost(dataBotAccount);
				if (dataPosttable != null) {
					if(dataPosttable.isSong()){
						for (DataSongText songText : reps.dataSongTextRepositories.findBySongidOrderBySongsequenceAsc(dataPosttable.getSong_ID())) {
							updateStatus.add(new UpdateStatus(reps.getBot(songText.getBotId()),songText,dataPosttable));
							memberBot .add(reps.getBot(songText.getBotId()));
						}
					}
					else if(dataPosttable.getPostStr() != null && !dataPosttable.getPostStr().equals("") && !dataPosttable.getPostStr().contains("#stop#"))
					{
						for (String postText  : dataPosttable.getPostStr().split("#next#")) {
							updateStatus.add(new UpdateStatus(reps.getBot(dataBotAccount.getBot_id()),postText, dataPosttable));
							memberBot.add(reps.getBot(dataBotAccount.getBot_id()));
						}
					}
				}
				lastNomalPostTime = new Date();
			}
		}
	}






	private void getHomeTimeLine() {
		if(checkRateLimit(rateLimitStatusHomeTimeLine))
		{
			Paging paging = new Paging(1,200);
			ResponseList<Status> statusList = null ;
			do {
				try {
					if(lastId.getSinceIdHomeTimeLine() > Long.valueOf(0L))
					{
						paging.sinceId(lastId.getSinceIdHomeTimeLine());
					}
					statusList = twitter.getHomeTimeline(paging);
					for (Status status : statusList) {
						onStatus(status);

						BotsScheduler.LOG.debug(twitter.getScreenName()  + ":" + status.getUser().getName() + "「" + status.getText() + "」");
						lastId.setSinceIdHomeTimeLine(Long.max(lastId.getSinceIdHomeTimeLine() , Long.valueOf(status.getId())));
					}
					rateLimitStatusHomeTimeLine = statusList.getRateLimitStatus();
				} catch (TwitterException e) {
					// TODO 自動生成された catch ブロック
					onTwitterException(e);
				}
			} while (statusList != null && statusList.size() >= paging.getCount() && checkRateLimit(rateLimitStatusHomeTimeLine));
		}
		reps.dataBotAccountLastIdRepositories.saveAndFlush(lastId);
	}
	private void getMentionsTimeLine() {
		if(checkRateLimit(rateLimitStatusMentionsTimeLine))
		{
			Paging paging = new Paging(1,200);
			ResponseList<Status> statusList = null ;
			do {
				try {
					if(lastId.getSinceIdStatusMentionsTimeLine() > Long.valueOf(0L))
					{
						paging.sinceId(lastId.getSinceIdStatusMentionsTimeLine());
					}
					statusList = twitter.getMentionsTimeline(paging);
					for (Status status : statusList) {
						onStatus( status );

						BotsScheduler.LOG.debug(twitter.getScreenName()  + ":" + status.getUser().getName() + "「" + status.getText() + "」");
						lastId.setSinceIdStatusMentionsTimeLine(Long.max(lastId.getSinceIdStatusMentionsTimeLine() , new Long(status.getId())));
					}
					rateLimitStatusMentionsTimeLine = statusList.getRateLimitStatus();
				} catch (TwitterException e) {
					onTwitterException(e);
				}
			} while (statusList != null && statusList.size() >= paging.getCount() && checkRateLimit(rateLimitStatusMentionsTimeLine));
		}
		reps.dataBotAccountLastIdRepositories.saveAndFlush(lastId);
	}


	boolean checkRateLimit(RateLimitStatus rateLimitStatus) {
		if(rateLimitStatus != null){
			Date dateNow = new Date();
			Date dateResetTime = new Date(((rateLimitStatus.getResetTimeInSeconds() + 60) * 1000L));
			LOG.debug(rateLimitStatus.toString() + "NOW:" + dateNow + "RESR:" + dateResetTime);
			if (rateLimitStatus != null && rateLimitStatus.getRemaining() <= 0 && dateNow.before(dateResetTime)) {
				return false;
			}
		}
		return true;
	}
	@SuppressWarnings("unused")
	private void SleepRateLimit(RateLimitStatus rateLimitStatus) {
		if (rateLimitStatus != null && rateLimitStatus.getRemaining() <= 0) {
			long time = rateLimitStatus.getSecondsUntilReset();
			try {
				if (time < 0) {
					time += 120;
				}
				Thread.sleep(time * 1000);
			} catch (InterruptedException | IllegalArgumentException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		return;
	}
	private void modeUpdate(DataAccountMode mode)
	{
		if(mode != null && !reps.getDebug())
		{
			try {
				twitter.updateProfile(mode.getUserName(),mode.getUserUrl(),mode.getUserLocation() ,mode.getUserDescription());
				File icon = mode.getIconToFile();
				if(icon != null && icon.length() > 0)
				{
					twitter.updateProfileImage(icon);
				}else {
					mode.setIcon(new URL (twitter.verifyCredentials().getOriginalProfileImageURLHttps()));
					reps.dataAccountModeRepositories.saveAndFlush(mode);
				}
				dataBotAccount.setMode_name(mode.getModeName());
				reps.dataBotAccountRepositories.saveAndFlush(dataBotAccount);
				updateBotUser();


			} catch (TwitterException e) {
				onTwitterException(e);
			} catch (Exception e) {
				onException(e);
			}
		}
	}
	private void onException(Exception e) {
		// TODO 自動生成されたメソッド・スタブ
		reps.dataLogtRepositories.saveAndFlush(new DataLog(dataBotAccount.getBot_id(), -1, e.getMessage()));

	}
	public void modeUpdateStop() {
		DataAccountMode mode = reps.getBotModeStop(dataBotAccount);
		if(mode != null)
		{
			modeUpdate(mode);
		}
	}
	public void modeUpdate() {
		DataAccountMode mode = reps.getBotMode(dataBotAccount);
		if(mode != null)
		{
			modeUpdate(mode);
		}else
		{
			User botUser;
			try {
				botUser = twitter.verifyCredentials();
				mode = new DataAccountMode(twitter.getId(),
						"通常", "通常", botUser.getName(), botUser.getURL()
						, botUser.getLocation(), botUser.getDescription()
						, new URL(botUser.getOriginalProfileImageURLHttps()));
				reps.dataAccountModeRepositories.saveAndFlush(mode);
			} catch (TwitterException e) {
				onTwitterException(e);
			} catch (Exception e) {
				onException(e);
			}
		}
	}
	@Override
	public String toString() {
		// TODO 自動生成されたメソッド・スタブ
		return botName + "(" + botScreenName + "/" + botId + ")";
	}
}
