package com.kusobotmaker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
@Service
public class KeitaisoKaiseki {

	private NameFinderME finderME;

	private Tokenizer tokenizerKuromoji;
	static ResourceLoader resourceLoader = new DefaultResourceLoader();

	final private ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Autowired
	public KeitaisoKaiseki() throws IOException {
		TokenNameFinderModel model /*= new TokenNameFinderModel(new FileInputStream( )*/;


		//File resourcesFile = new File (resourceLoader.getResource("classpath:").getFile(),"rondhuit-ja-ner-1.0.0.bin");
		File resourcesFile = new File (resourceLoader.getResource("classpath:").getFile(),"config/rondhuit-ja-ner-1.0.0.bin");
		model = new TokenNameFinderModel(resourcesFile);

		finderME = new NameFinderME(model);

		tokenizerKuromoji = new Tokenizer();
	}


	/**
	 * @param status
	 * @return 人名+敬称　見つからなければ null
	 */
	List<String> findName(Status status)
	{
		try {
			return executorService.submit(new Callable<List<String>>() {

				@Override
				public List<String> call() throws Exception {
					// TODO 自動生成されたメソッド・スタブ
					List<String> ret = new ArrayList<>();
					java.util.List<Token> tokens = tokenizerKuromoji.tokenize(getStatus(status));
					String[] sentens = tokens.stream().map(s->s.getSurface()).collect(Collectors.toList()).toArray(new String[tokens.size()]);

					List<Span> spans = Arrays.asList(finderME.find(sentens));

					for (Span pasen : spans.stream().filter(s->s.getType().equals("PERSON")).collect(Collectors.toList()))
					{
						String name = new String();
						name = name.concat(Span2String(pasen, sentens));
						Optional<Span> title = spans.stream().filter(s->s.getType().equals("TITLE") && s.getStart() == pasen.getEnd()).findFirst();
						if(title.isPresent())
						{
							name = name.concat(Span2String(title.get(), sentens));
						}
						ret.add(name);
					}
					return ret;
				}
			}).get();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return null;
	}

	private String Span2String(Span span,String[] sentens)
	{
		String ret = new String();
		for (int i = span.getStart(); i < span.getEnd(); i++) {
			ret = ret.concat(sentens[i]);
		}
		return ret;
	}
	/**
	 * @param status
	 * @return 名詞 見つからなければ null
	 */
	List<String> findMeishi(Status status)
	{
		List<String> ret = new ArrayList<>();
		java.util.List<Token> tokens = tokenizerKuromoji.tokenize(getStatus(status));
		for(Token token : tokens.stream().filter(c->findBySpeechLevel(c, "名詞")
					&& !findBySpeechLevel(c, "数")
					&& !findBySpeechLevel(c, "代名詞")
					&& !findBySpeechLevel(c, "副詞可能")
					&& c.getSurface().length() > 2).collect(Collectors.toList()))
		{
			ret.add(token.getSurface());
			//System.out.println(token.getAllFeatures());
		}
		return ret;
	}
	/**
	 * @param status
	 * @return 動詞 見つからなければ null
	 */
	List<String> findDoushi(Status status)
	{
		List<String> ret = new ArrayList<>();
		java.util.List<Token> tokens = tokenizerKuromoji.tokenize(getStatus(status));
		for(Token token : tokens.stream().filter(c->findBySpeechLevel(c, "動詞")
					&& c.getSurface().length() > 2).collect(Collectors.toList()))
		{
			ret.add(token.getBaseForm());
		}
		return ret;
	}















	private boolean findBySpeechLevel(Token tokens,String speechLevel)
	{
		return (tokens.getPartOfSpeechLevel1().equals(speechLevel)
				|| tokens.getPartOfSpeechLevel2().equals(speechLevel)
				|| tokens.getPartOfSpeechLevel3().equals(speechLevel)
				|| tokens.getPartOfSpeechLevel4().equals(speechLevel)
				);
	}
	private String getStatus(Status status)
	{
		Status i_status = status;
		if(status.isRetweet())
		{
			i_status = status.getRetweetedStatus();
		}

		String ret = i_status.getText();
		for (URLEntity urlEntity : i_status.getURLEntities()) {
			ret = ret.replaceAll(urlEntity.getURL(), "");
			ret = ret.replaceAll(urlEntity.getDisplayURL(), "");
			ret = ret.replaceAll(urlEntity.getExpandedURL(), "");
		}
		for (MediaEntity mediaEntity : i_status.getMediaEntities()) {
			ret = ret.replaceAll(mediaEntity.getDisplayURL(), "");
			ret = ret.replaceAll(mediaEntity.getExpandedURL(), "");
			ret = ret.replaceAll(mediaEntity.getURL(), "");
			ret = ret.replaceAll(mediaEntity.getMediaURL(), "");
			ret = ret.replaceAll(mediaEntity.getMediaURLHttps(), "");
		}
		return ret;

	}
}
