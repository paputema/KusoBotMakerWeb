package com.kusobotmaker.view;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kusobotmaker.KusoBotMakerWebAppUsers.KbmUser;
import com.kusobotmaker.KusoBotMakerWebAppDataReps;
import com.kusobotmaker.Data.DataSongList;
import com.kusobotmaker.Data.DataSongText;
import com.kusobotmaker.Form.FromComboBot;
import com.kusobotmaker.Form.SongEditForm;
import com.kusobotmaker.Form.SongIdForm;


@Controller
public class ViewSong {
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
		addObjectBySession(mav,"songIdForm", new SongIdForm());
		addObjectBySession(mav,"songsCombo", new ArrayList<DataSongList>());
		addObjectBySession(mav,"id",new Long(0L));
		addObjectBySession(mav,"songsCombo", new ArrayList<DataSongList>());
		addObjectBySession(mav,"botCombo", new ArrayList<FromComboBot>());
		addObjectBySession(mav,"songEditForm", new SongEditForm());
		addObjectBySession(mav,"songAddForm", new SongEditForm());

	}
	@RequestMapping(value="/dialogue/update" , method = RequestMethod.POST)
	public ModelAndView songsUpdate(@Validated SongEditForm songEditForm, BindingResult result, Locale locale, ModelAndView mav) {
		dataReps.fromSongTextSort.update(songEditForm.getSongTexts());
		songEditForm.setSongTexts(dataReps.dataSongTextRepositories.findBySongidOrderBySongsequenceAsc(songEditForm.getSongList().getSongID()));
		session.setAttribute("songEditForm", songEditForm);

		SongEditForm songAddForm = new SongEditForm(songEditForm.getSongList(),new DataSongText(songEditForm));
		session.setAttribute("songAddForm", songAddForm);

		//KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		mav.setViewName("dialogueEdit");
		setDefaultObjectBySession(mav);
		return mav;
	}
	@RequestMapping(value="/dialogue/delete" , method = RequestMethod.POST)
	public ModelAndView songsDelete(@RequestParam Long id, ModelAndView mav) {

		//KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		SongEditForm songEditForm = (SongEditForm)session.getAttribute("songEditForm");
		dataReps.fromSongTextSort.delete(songEditForm.getSongTexts(),id);
		List<DataSongText> texts =  dataReps.dataSongTextRepositories.findBySongidOrderBySongsequenceAsc(songEditForm.getSongList().getSongID());
		songEditForm = new SongEditForm(texts,dataReps.dataSongListRepositories.findOne(songEditForm.getSongList().getSongID()));
		session.setAttribute("songEditForm", songEditForm);

		SongEditForm songAddForm = new SongEditForm(songEditForm.getSongList(),new DataSongText(songEditForm));
		session.setAttribute("songAddForm", songAddForm);
		mav.setViewName("dialogueEdit");
		setDefaultObjectBySession(mav);
		return mav;
	}
	@RequestMapping(value="/dialogue/select" , method = RequestMethod.POST)
	public ModelAndView modeSelectForm(@Validated SongIdForm songIdForm, BindingResult result, Locale locale, ModelAndView mav) {
		List<DataSongText> texts =  dataReps.dataSongTextRepositories.findBySongidOrderBySongsequenceAsc(songIdForm.getSongID());

		SongEditForm songEditForm =  new SongEditForm(texts,dataReps.dataSongListRepositories.findOne(songIdForm.getSongID()));
		session.setAttribute("songEditForm", songEditForm);

		SongEditForm songAddForm = new SongEditForm(songEditForm.getSongList(),new DataSongText(songEditForm));
		session.setAttribute("songAddForm", songAddForm);

		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		List<FromComboBot> bots = new ArrayList<FromComboBot>(FromComboBot.getComboBotList(kbmUser.getBots()));
		session.setAttribute("botCombo", bots);

		setDefaultObjectBySession(mav);
		mav.setViewName("dialogueEdit");
		return mav;
	}
	@RequestMapping(value="/dialogue/add" , method = RequestMethod.POST)
	public ModelAndView modeAddForm(@Validated SongEditForm songAddForm, BindingResult result, Locale locale, ModelAndView mav) {
		SongEditForm songEditForm = (SongEditForm)session.getAttribute("songEditForm");
		dataReps.fromSongTextSort.insert(songEditForm.getSongTexts(), songAddForm.getSongTexts().get(0));

		songEditForm.setSongTexts(dataReps.dataSongTextRepositories.findBySongidOrderBySongsequenceAsc(songEditForm.getSongList().getSongID()));
		session.setAttribute("songEditForm", songEditForm);

		songAddForm = new SongEditForm(songEditForm.getSongList(),new DataSongText(songEditForm));
		session.setAttribute("songAddForm", songAddForm);

		setDefaultObjectBySession(mav);
		mav.setViewName("dialogueEdit");
		return mav;
	}
	@RequestMapping(value="/dialogue/new" , method = RequestMethod.POST)
	public ModelAndView modNewForm(@Validated SongIdForm songIdForm, BindingResult result, Locale locale, ModelAndView mav) {
		DataSongList dataSongList = new DataSongList(songIdForm.getSongTitle());
		dataSongList = dataReps.dataSongListRepositories.saveAndFlush(dataSongList);

		SongEditForm songEditForm =  new SongEditForm();
		songEditForm.setSongList(dataSongList);
		session.setAttribute("songEditForm", songEditForm);

		SongEditForm songAddForm = new SongEditForm(songEditForm.getSongList(),new DataSongText(songEditForm));
		session.setAttribute("songAddForm", songAddForm);

		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		List<FromComboBot> bots = new ArrayList<FromComboBot>(FromComboBot.getComboBotList(kbmUser.getBots()));
		session.setAttribute("botCombo", bots);

		setDefaultObjectBySession(mav);
		mav.setViewName("dialogueEdit");
		return mav;
	}
	@RequestMapping(value="/dialogue" , method = RequestMethod.GET)
	public ModelAndView dialogue(ModelAndView mav) {
		mav.setViewName("BotOAuth");
		KbmUser kbmUser = (KbmUser) session.getAttribute("kbmUser");
		if(kbmUser != null)
		{
			session.setAttribute("songsCombo", dataReps.getSongListByUserId(kbmUser.getDataUser().getUserId()));
			session.setAttribute("songIdForm", new SongIdForm());
			mav.setViewName("dialogue");
		}
		setDefaultObjectBySession(mav);
		return mav;
	}
}
