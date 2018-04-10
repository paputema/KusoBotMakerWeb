package com.kusobotmaker;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kusobotmaker.KusoBotMakerWebAppUsers.KbmUser;
import com.kusobotmaker.Data.DataAccountMode;
import com.kusobotmaker.Data.DataBotAccount;
import com.kusobotmaker.Data.DataPosttable;
import com.kusobotmaker.Data.DataSongList;
import com.kusobotmaker.Form.FromSongTextSort;
import com.kusobotmaker.repositories.DataAccountModeRepositories;
import com.kusobotmaker.repositories.DataBotAccountLastIdRepositories;
import com.kusobotmaker.repositories.DataBotAccountRepositories;
import com.kusobotmaker.repositories.DataLogtRepositories;
import com.kusobotmaker.repositories.DataNicknameRepositories;
import com.kusobotmaker.repositories.DataPosttableRepositories;
import com.kusobotmaker.repositories.DataSongListRepositories;
import com.kusobotmaker.repositories.DataSongTextRepositories;
import com.kusobotmaker.repositories.DataUserBotRepositories;
import com.kusobotmaker.repositories.DataUserRepositories;

import lombok.Getter;
import twitter4j.Status;
import twitter4j.User;
@Service
public class KusoBotMakerWebAppDataReps {
	@Autowired
	private EntityManager em;
	@Autowired
	public DataBotAccountRepositories dataBotAccountRepositories;
	@Autowired
	public DataPosttableRepositories dataPosttableRepositories;
	@Autowired
	public DataBotAccountLastIdRepositories dataBotAccountLastIdRepositories;
	@Autowired
	public DataSongTextRepositories dataSongTextRepositories;
	@Autowired
	public DataNicknameRepositories dataNicknameRepositories;
	@Autowired
	public DataLogtRepositories dataLogtRepositories;
	@Autowired
	public DataAccountModeRepositories dataAccountModeRepositories;
	@Autowired
	public DataUserRepositories dataUserRepositories;
	@Autowired
	public DataUserBotRepositories dataUserBotRepositories;
	@Autowired
	public FromSongTextSort fromSongTextSort;
	@Autowired
	public DataSongListRepositories dataSongListRepositories;
	@Getter
	private Set<DataBotAccount> failedBotAccount = new HashSet<DataBotAccount>();
	@Getter
	@Value("${kbm.debug}")
	private Boolean debug = false;

	public void addFailedBotAccount(DataBotAccount account) {
		failedBotAccount.add(account);
	}
	public void delFailedBotAccount(DataBotAccount account) {
		failedBotAccount.remove(account);
	}
	private Map<Long,User> users = new HashMap<>();
	public User getUser(Long userId) {
		return users.get(userId);
	}
	public void putUser(User user) {
		users.put(user.getId(), user);
	}

	final private Map<Long,Bot> bots = Collections.synchronizedMap(new HashMap<>());
	public Bot getBot(Long botId) {
		return  bots.get(botId);
	}
	public Collection<Bot> getBots() {
		return bots.values();
	}
	public void putBot(DataBotAccount dataBotAccount,KusoBotMakerWebAppDataReps reps) {
		Bot bot = bots.get(dataBotAccount.getBot_id());
		if(bot == null || bot.isBotEnable() == false || bot.equalsDataBotAccount(dataBotAccount) == false)
		{
			bot = new Bot(dataBotAccount, reps);
			bots.put(bot.getBotId(), bot);
		}
	}
	private ReplyHistory replyHistory = new  ReplyHistory();
	public void addReplyHistory(Status status) {
		replyHistory.addHistory(status);
	}




	@SuppressWarnings("unchecked")
	public List<DataAccountMode> getBotModeNomal(DataBotAccount dataBotAccount) {
		Query query = em.createNativeQuery("SELECT * FROM accountmode WHERE"
				+ " bot_id = :bot_id AND"
				+ " mode_type = '通常';",
				DataAccountMode.class);
		query.setParameter("bot_id", dataBotAccount.getBot_id());
		return query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public DataAccountMode getBotMode(DataBotAccount dataBotAccount) {
		Query query = em.createNativeQuery("SELECT * FROM accountmode WHERE"
				+ " bot_id = :bot_id AND"
				+ " (:mode_type1 LIKE mode_type OR"
				+ " :mode_type2 LIKE mode_type);",
				DataAccountMode.class);
		query.setParameter("bot_id", dataBotAccount.getBot_id());

		SimpleDateFormat dateFormatModeType01 = new SimpleDateFormat("MMdd");
		SimpleDateFormat dateFormatModeType02 = new SimpleDateFormat("MMWE");
		Date date = new Date();
		//:mode_type1 MMDD
		query.setParameter("mode_type1", dateFormatModeType01.format(date));
		//:mode_type2 w月第何曜日
		query.setParameter("mode_type2", "w" + dateFormatModeType02.format(date));
		List<DataAccountMode> dataAccountModes = query.getResultList();
		if(dataAccountModes.isEmpty())
		{
			dataAccountModes = getBotModeNomal( dataBotAccount);
		}
		if(dataAccountModes.isEmpty())
		{
			return null;
		}
		Random random = new SecureRandom();
		return dataAccountModes.get(random.nextInt(dataAccountModes.size()));
	}
	@SuppressWarnings("unchecked")
	public DataPosttable getNormalPost(DataBotAccount dataBotAccount){
		Query query = em.createNativeQuery("SELECT * FROM posttable where"
				+ " bot_id = :bot_id and"
				+ " mode_name = :mode_name and "
				+ " normalpost = true AND "
				+ " Last_use < current_timestamp() - INTERVAL 300 SECOND "
				+ " ORDER BY Last_use ASC;",
				DataPosttable.class);
		query.setParameter("bot_id", dataBotAccount.getBot_id());
		query.setParameter("mode_name", dataBotAccount.getMode_name());
		List<DataPosttable> dataPosttables = query.getResultList();
		if(!dataPosttables.isEmpty())
		{
			DataPosttable dataPosttable = dataPosttables.get(0);
			dataPosttable.setLastUse(java.sql.Timestamp.valueOf(LocalDateTime.now()));
			dataPosttableRepositories.saveAndFlush(dataPosttable);
			return dataPosttables.get(0);
		}
		return null;
	}
	public DataPosttable getReplyPost(DataBotAccount dataBotAccount,Status status) {
		Query query = em.createNativeQuery("SELECT * FROM posttable where"
				+ " bot_id = :bot_id and"
				+ " :Search_str regexp Search_str and "
				+ " mode_name = :mode_name and "
				+ " (((Tw4Tl | Tw4me << 1 | Tw4Other << 2) & :tw4mode ) > 0) AND "
				+ " looplimit > :looplimit AND "
				+ " Last_use < current_timestamp() - INTERVAL 300 SECOND "
				+ " ORDER BY Priority DESC ,Last_use ASC;"
				,DataPosttable.class);
		query.setParameter("bot_id", dataBotAccount.getBot_id());
		query.setParameter("Search_str", status.getText());
		query.setParameter("mode_name", dataBotAccount.getMode_name());
		query.setParameter("looplimit", replyHistory.countReplyHistory(status.getId()));

		Integer tw4mode = 1;
		Long replyToUserId = status.getInReplyToUserId();

		if(replyToUserId.equals(Long.valueOf(-1)))
		{
			tw4mode = 1;
		}else if(replyToUserId.equals(dataBotAccount.getBot_id()) == true)
		{
			tw4mode = 2;
		}else if(replyToUserId.equals(dataBotAccount.getBot_id()) == false)
		{
			tw4mode = 4;
		}
		query.setParameter("tw4mode",tw4mode);

		@SuppressWarnings("unchecked")
		List<DataPosttable> dataPosttables	= query.getResultList();
		if(!dataPosttables.isEmpty())
		{
			DataPosttable dataPosttable = dataPosttables.get(0);
			dataPosttable.setLastUse(java.sql.Timestamp.valueOf(LocalDateTime.now()));
			dataPosttableRepositories.saveAndFlush(dataPosttable);
			return dataPosttables.get(0);
		}
		return null;
	}
	public KusoBotMakerWebAppDataReps() {
	}
	@SuppressWarnings("unchecked")
	public DataAccountMode getBotModeStop(DataBotAccount dataBotAccount) {
		DataAccountMode mode = null;
		Query query = em.createNativeQuery("SELECT * FROM accountmode WHERE"
				+ " bot_id = :bot_id AND"
				+ " mode_type = '停止';",
				DataAccountMode.class);
		query.setParameter("bot_id", dataBotAccount.getBot_id());
		List<DataAccountMode> dataAccountModes = query.getResultList();
		Random random = new SecureRandom();
		if(dataAccountModes.isEmpty())
		{
			mode = null;
		}else
		{
			mode = dataAccountModes.get(random.nextInt(dataAccountModes.size()));
		}
		return mode;

	}
	@SuppressWarnings("unchecked")
	public List<DataAccountMode> getBotModeList(Long bot_id) {
		List<DataAccountMode> modeList = null;
		Query query = em.createNativeQuery("select * from accountmode"
										+ " where bot_id = :bot_id"
										+ " group by mode_name;",
										DataAccountMode.class);
		query.setParameter("bot_id", bot_id);
		modeList = query.getResultList();
		if(modeList.isEmpty())
		{
			modeList = null;
		}
		return modeList;
	}
	@SuppressWarnings("unchecked")
	public List<DataSongList> getSongListByBotId(Long bot_id) {
		List<DataSongList> songList = null;
		Query query = em.createNativeQuery("select song_list.* "
										+ "FROM song_list join song_text "
										+ "where song_list.Song_id = song_text.Song_id "
										+ "AND bot_id = :bot_id "
										+ "group by song_list.Song_id;",
										DataSongList.class);
		query.setParameter("bot_id", bot_id);
		songList = query.getResultList();
		DataSongList defaultData = new DataSongList();
		defaultData.setSongID(-1L);
		defaultData.setSongTitle("");
		songList.add(defaultData);
		return songList;
	}
	@SuppressWarnings("unchecked")
	public List<DataSongList> getSongListByUserId(Long user_id) {
		List<DataSongList> songList = null;
		Query query = em.createNativeQuery("SELECT song_list.* "
										+ "FROM song_list join song_text "
										+ "WHERE song_list.Song_id = song_text.Song_id "
										+ "AND bot_id "
										+ "IN (SELECT bot_id FROM user_bot_table WHERE user_id = :user_id) "
										+ "GROUP BY song_list.song_id",
										DataSongList.class);
		query.setParameter("user_id", user_id);
		songList = query.getResultList();

		return songList;
	}
	public Set<DataBotAccount> getFailedBotAccount(KbmUser kbmUser) {

		return getFailedBotAccount().stream().filter(c->c.getOwner_id().equals(kbmUser.getDataUser().getUserId())).collect(Collectors.toSet());
	}
	public void deleteBot(Bot bot) {
		bots.remove(bot.getBotId());
		dataBotAccountRepositories.delete(bot.getBotId());
		dataBotAccountRepositories.flush();
	}
}