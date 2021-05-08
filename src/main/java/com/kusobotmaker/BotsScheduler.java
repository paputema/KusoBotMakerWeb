 package com.kusobotmaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kusobotmaker.Data.DataBotAccount;
import com.kusobotmaker.Data.DataUserBot;
import com.kusobotmaker.repositories.DataUserBotKey;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;



@Service
@EnableScheduling
public class BotsScheduler {
	static final Log LOG = LogFactory.getLog(BotsScheduler.class);
	final private ExecutorService exec = Executors.newCachedThreadPool();


	@Value("${kbm.debug}")
	private boolean debug;
	@Autowired
	KusoBotMakerWebAppDataReps kusoBotMakerWebAppDataReps;

	@Scheduled(fixedDelay = 6 * 60 * 60 * 1000, initialDelay = 1 * 60 * 1000)
	private void Construct() {

		for (DataBotAccount dataBotAccount : kusoBotMakerWebAppDataReps.dataBotAccountRepositories.findAll()) {
			kusoBotMakerWebAppDataReps.putBot(dataBotAccount, kusoBotMakerWebAppDataReps);
		}

	}
	@PreDestroy
	private void preDestroy()
	{
		exec.shutdown();
		try {
			exec.awaitTermination(60, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			exec.shutdownNow();
		}
	}

	public User addBot(Twitter userTwitter, Twitter botTwitter) {
		DataBotAccount dataBotAccount;
		try {
			Optional<DataBotAccount> optional = kusoBotMakerWebAppDataReps.dataBotAccountRepositories.findById(botTwitter.getId());

			if(optional.isPresent() == true)
			{
				dataBotAccount = optional.get();
			}else
			{
				dataBotAccount = new DataBotAccount();
				dataBotAccount.setBot_id(botTwitter.getId());
				dataBotAccount.setReplytoRT(false);
				dataBotAccount.setNormal_post_interval(60L);
				dataBotAccount.setMode_name("通常");
				dataBotAccount.setPause_time(120L);
				dataBotAccount.setBot_enable(true);
				dataBotAccount.setOwner_id(userTwitter.getId());
			}
			dataBotAccount.setBot_enable(true);
			dataBotAccount.setBot_screen_name(userTwitter.getScreenName());
			dataBotAccount.setAccess_Token(botTwitter.getOAuthAccessToken().getToken());
			dataBotAccount.setAccess_Token_Secret(botTwitter.getOAuthAccessToken().getTokenSecret());
			dataBotAccount.setConsumer_Key(botTwitter.getConfiguration().getOAuthConsumerKey());
			dataBotAccount.setConsumer_Secret(botTwitter.getConfiguration().getOAuthConsumerSecret());
			if(dataBotAccount.getOwner_id() == null)
			{
				dataBotAccount.setOwner_id(userTwitter.getId());
			}
			kusoBotMakerWebAppDataReps.dataBotAccountRepositories.saveAndFlush(dataBotAccount);
			kusoBotMakerWebAppDataReps.putBot(dataBotAccount,kusoBotMakerWebAppDataReps);
			Optional<DataUserBot> dataUserBot = kusoBotMakerWebAppDataReps.dataUserBotRepositories.findById(new DataUserBotKey(userTwitter.getId(),botTwitter.getId()));
			if(dataUserBot.isPresent() == false) {
				kusoBotMakerWebAppDataReps.dataUserBotRepositories.saveAndFlush(new DataUserBot(userTwitter.getId(),botTwitter.getId(), (dataBotAccount.getOwner_id() == userTwitter.getId())));
			}
			return botTwitter.verifyCredentials();
		} catch (IllegalStateException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;

	}

	@Scheduled(initialDelay = 5 * 60 * 1000, fixedRate = 10 * 60 * 1000)
	private void normalPostCron()
	{
		if(!debug)
		{
			LOG.info("通常ツイート開始");
			List<Future<Bot>> list = new ArrayList<Future<Bot>>();
			for (final Bot bot : kusoBotMakerWebAppDataReps.getBots()) {
				list.add(exec.submit(new Callable<Bot>() {
					@Override
					public Bot call() {
						try {
							if(bot.isPause() == false)
							{
								bot.normalPost();
								bot.updateBotUser();
							}
						} catch (TwitterException e) {
							// TODO 自動生成された catch ブロック
							bot.onTwitterException(e);
						}
						return bot;

					}
				}));
			}
			for (Future<?> future : list) {
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			list = null;
			LOG.info("通常ツイート終了");
		}
	}
	@Scheduled(initialDelay = 15 * 60 * 1000, fixedRate =  1 * 60 * 1000)
	private void replyPostCron()   {
		if (!debug) {
			LOG.info("リプライツイート開始");
			List<Future<Bot>> list = new ArrayList<Future<Bot>>();
			for (final Bot bot : kusoBotMakerWebAppDataReps.getBots()) {
					if(bot.isUser())
					{
						list.add(exec.submit(new Callable<Bot>() {


							@Override
							public Bot call() {
								if(bot.isPause() == false)
								{
									bot.replyPost();
								}
								return bot;
							}
						}));
					}

			}
			for (Future<Bot> future : list) {
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			list = null;
			LOG.info("リプライツイート終了");
		}
	}
	@Scheduled(cron = "0 0 0 * * *" , zone = "Asia/Tokyo")
	private void modeUpdate()  {

		LOG.info("モード変更開始");
		List<Future<?>> list = new ArrayList<Future<?>>();

		for (final Bot bot : kusoBotMakerWebAppDataReps.getBots()) {
			list.add(exec.submit(new Runnable() {
				@Override
				public void run() {

					bot.modeUpdate();
				}

			}));

		}
		for (Future<?> future : list) {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		list = null;
		System.gc();
		LOG.info("モード変更終了");
	}
	@Scheduled(cron = "0 0 0 * * *" , zone = "Asia/Tokyo")
	private void deleteOldLog()
	{
		LOG.info("ログ削除開始");
		kusoBotMakerWebAppDataReps.dataLogtRepositories.deleteOld();
		kusoBotMakerWebAppDataReps.dataLogtRepositories.flush();
		LOG.info("ログ削除終了");
	}

}
