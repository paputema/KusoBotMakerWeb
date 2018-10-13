 package com.kusobotmaker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
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
import com.kusobotmaker.Form.Consumer;
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
	@PostConstruct
	@Scheduled(cron = "0 0 6,18 * * *")
	private void Construct() {
		exec.submit(new Runnable() {
				public void run() {
					for (DataBotAccount dataBotAccount : kusoBotMakerWebAppDataReps.dataBotAccountRepositories.findAll()) {
					kusoBotMakerWebAppDataReps.putBot(dataBotAccount,kusoBotMakerWebAppDataReps);

				}
			}
		});
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

	public User addBot(Twitter userTwitter, Twitter botTwitter, Consumer consumer) {
		DataBotAccount dataBotAccount;
		try {
			DataBotAccount optional = kusoBotMakerWebAppDataReps.dataBotAccountRepositories.findOne(botTwitter.getId());

			if(optional != null)
			{
				dataBotAccount = optional;
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
			dataBotAccount.setAccess_Token(botTwitter.getOAuthAccessToken().getToken());
			dataBotAccount.setAccess_Token_Secret(botTwitter.getOAuthAccessToken().getTokenSecret());
			dataBotAccount.setConsumer_Key(consumer.getConsumerKey());
			dataBotAccount.setConsumer_Secret(consumer.getConsumerSecret());
			if(dataBotAccount.getOwner_id() == null)
			{
				dataBotAccount.setOwner_id(userTwitter.getId());
			}
			kusoBotMakerWebAppDataReps.dataBotAccountRepositories.saveAndFlush(dataBotAccount);
			kusoBotMakerWebAppDataReps.putBot(dataBotAccount,kusoBotMakerWebAppDataReps);
			DataUserBot dataUserBot = kusoBotMakerWebAppDataReps.dataUserBotRepositories.findOne(new DataUserBotKey(userTwitter.getId(),botTwitter.getId()));
			if(dataUserBot == null) {
				dataUserBot = new DataUserBot(userTwitter.getId(),botTwitter.getId(), (dataBotAccount.getOwner_id() == userTwitter.getId()));
				kusoBotMakerWebAppDataReps.dataUserBotRepositories.saveAndFlush(dataUserBot);
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

	@Scheduled(initialDelay = 15 * 60 * 1000, fixedDelay = 10 * 60 * 1000)
	private void normalPostCron()
	{
		if(!debug)
		{
			LOG.info("通常ツイート開始");
			List<Future<?>> list = new ArrayList<Future<?>>();
			for (final Bot bot : kusoBotMakerWebAppDataReps.getBots()) {
				list.add(exec.submit(new Runnable() {
					@Override
					public void run() {
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
	@Scheduled(initialDelay = 5 * 60 * 1000, fixedDelay =  1 * 1000)
	private void replyPostCron()   {
		if (!debug) {
			LOG.info("リプライツイート開始");
			List<Future<?>> list = new ArrayList<Future<?>>();
			for (final Bot bot : kusoBotMakerWebAppDataReps.getBots()) {
					if(bot.isUser())
					{
						list.add(exec.submit(new Runnable() {

							@Override
							public void run() {
								if(bot.isPause() == false)
								{
									bot.replyPost();
								}
							}
						}));
					}

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
			LOG.info("リプライツイート終了");
		}
	}
	@Scheduled(cron = "0 0 0,12 * * *")
	private void modeUpdate()  {

		if (!debug) {
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
	}
}
