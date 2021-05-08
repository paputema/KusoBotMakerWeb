package com.kusobotmaker;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.kusobotmaker.Data.DataUser;
import com.kusobotmaker.Data.DataUserBot;
import com.kusobotmaker.repositories.DataUserBotKey;

import twitter4j.Twitter;
import twitter4j.TwitterException;

@Service
@EnableScheduling
public class KusoBotMakerWebAppUsers {
	static final Log LOG = LogFactory.getLog(KusoBotMakerWebAppUsers.class);
	@Autowired
	private KusoBotMakerWebAppDataReps kusoBotMakerWebAppDataReps;
	public KbmUser getUser(Twitter twitter) throws TwitterException {
		return new KbmUser(twitter);
	}
	public class KbmUser{
		private boolean verify;
		private DataUser dataUser;
		public KbmUser(Twitter twitter) throws TwitterException {
			Optional<DataUser> optional = kusoBotMakerWebAppDataReps.dataUserRepositories.findById(twitter.getId());

			if(optional.isPresent())
			{
				this.dataUser = optional.get();
			}
			else
			{
				this.dataUser = new DataUser(twitter.getId(), false);
			}
			this.dataUser.setLastLogin(new Date());
			kusoBotMakerWebAppDataReps.dataUserRepositories.saveAndFlush(this.dataUser);
			this.verify = this.dataUser.isVerify();
		}
		public DataUser getDataUser() {
			return dataUser;
		}
		public boolean isVerify() {
			return verify;
		}
		public void setVerify(boolean verify) {
			this.verify = verify;
		}
		public void setDataUser(DataUser dataUser) {
			this.dataUser = dataUser;
		}
		public Set<Bot> getBots() {
			Set<Bot> bots = new HashSet<>();
			for(DataUserBot dataUserBot : kusoBotMakerWebAppDataReps.dataUserBotRepositories.findAllByUserId(dataUser.getUserId()))
			{
				Bot bot = kusoBotMakerWebAppDataReps.getBot(dataUserBot.getBotId());
				if(bot != null)
				{
					bots.add(bot);
				}
			}
			return bots;
		}
		public Bot getBot(Long botId) {
			Optional<DataUserBot> dataUserBot = kusoBotMakerWebAppDataReps.dataUserBotRepositories.findById(new DataUserBotKey(dataUser.getUserId(), botId));
			if(dataUserBot.isPresent())
			{
				return kusoBotMakerWebAppDataReps.getBot(dataUserBot.get().getBotId());
			}
			else
			{
				return null;
			}

		}
	}
}
