package com.kusobotmaker;

import java.io.File;
import java.io.IOException;

import javax.activation.FileTypeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kusobotmaker.Data.DataAccountMode;
import com.kusobotmaker.Data.DataSongText;
import com.kusobotmaker.repositories.DataSongTextRepositories;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KusoBotMakerWebAppApplicationTests {
	@Autowired
	DataSongTextRepositories dataSongTextRepositories;
	@Autowired
	BotsScheduler botsScheduler;
	@Autowired
	KusoBotMakerWebAppDataReps kusoBotMakerWebAppDataReps;

	public void testDataSongTextRepositories() {
		for (int j = 0; j < 99; j++) {
			for (DataSongText dataSongText : dataSongTextRepositories.findBySongidOrderBySongsequenceAsc((long) j)) {
				System.out.println(dataSongText.getSongid() + ":" + dataSongText.getSongsequence() +":" + dataSongText.getPostStr());
			}
		}
	}
	@Test
	public void name() throws IOException {
		System.out.println(getClass());
		FileTypeMap fileTypeMap = FileTypeMap.getDefaultFileTypeMap();

		for (DataAccountMode dataAccountMode : kusoBotMakerWebAppDataReps.dataAccountModeRepositories.findAll()) {
			File file = dataAccountMode.getIconToFile() ;
			if(file != null)
			{
				System.out.println(file.getAbsolutePath());
				System.out.println(fileTypeMap.getContentType(file.getAbsoluteFile()));
				file.deleteOnExit();
			}

		}
		System.out.println(getClass());
	}

}
