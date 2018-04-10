package com.kusobotmaker.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Entity
@Data
@Table(name = "accountmode")
public class DataAccountMode {
	static final Log LOG = LogFactory.getLog(DataAccountMode.class);

	public DataAccountMode() {
		super();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getBotId() {
		return botId;
	}
	public void setBotId(Long botId) {
		this.botId = botId;
	}
	public String getModeName() {
		return modeName;
	}
	public void setModeName(String modeName) {
		this.modeName = modeName;
	}
	public String getModeType() {
		return modeType;
	}
	public void setModeType(String modeType) {
		this.modeType = modeType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserUrl() {
		return userUrl;
	}
	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}
	public String getUserLocation() {
		return userLocation;
	}
	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}
	public String getUserDescription() {
		return userDescription;
	}
	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}
	public DataAccountMode(Long bot_id, String mode_name, String mode_type, String user_name, String user_url,
			String user_location, String user_description, File icon) {
		super();
		init(bot_id, mode_name, mode_type, user_name, user_url, user_location, user_description);
		setIcon(icon);
	}
	public DataAccountMode(Long bot_id, String mode_name, String mode_type, String user_name, String user_url,
			String user_location, String user_description, URL iconUrl) {
		super();
		init(bot_id, mode_name, mode_type, user_name, user_url, user_location, user_description);
		setIcon(iconUrl);
	}
	private void init(Long bot_id, String mode_name, String mode_type, String user_name, String user_url,
			String user_location, String user_description)	{
		this.botId = bot_id;
		this.modeName = mode_name;
		this.modeType = mode_type;
		this.userName = user_name;
		this.userUrl = user_url;
		this.userLocation = user_location;
		this.userDescription = user_description;
	}


	static ResourceLoader resourceLoader = new DefaultResourceLoader();
	public File getIconToFile() {
		File retFile = null;
		if (icon != null) {
			try {
				File resourcesFile = null;
				try {
					resourcesFile = new File (resourceLoader.getResource("classpath:").getFile(),"boticon");
					resourcesFile.mkdirs();
				} catch (IOException e) {
					LOG.error(e.getMessage());
					e.printStackTrace();
				}
				retFile = new File(resourcesFile, botId + "_" + id + ".png");
				retFile.createNewFile();
				FileOutputStream fileOutputStream;
				fileOutputStream = new FileOutputStream(retFile);
				fileOutputStream.write(icon);
				fileOutputStream.close();
				retFile.deleteOnExit();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		return retFile;
	}
	public String getIcon() {
		String data = null;
		if (icon != null) {
			data = "data:image/png;base64,";
			data += new String(Base64.encodeBase64(icon), Charset.defaultCharset());
		}
		return data;
	}
	public void setIcon(byte[] icon) {
		this.icon = icon;
	}
	public void setIcon(String icon) {
		icon = icon.replaceFirst("data:image/png;base64,", "");
		this.icon = Base64.decodeBase64(icon);
	}
	public void setIcon(File iconFile) {
		try {
			this.icon = IOUtils.toByteArray(new FileInputStream(iconFile));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	public void setIcon(URL iconUrl) {
		try {
			File resourcesFile = new File (resourceLoader.getResource("classpath:").getFile(),"boticon");
			resourcesFile.mkdirs();
			File iconFile = new File(resourcesFile,iconUrl.getFile());
			FileUtils.copyURLToFile(iconUrl, iconFile);
			this.icon = IOUtils.toByteArray(new FileInputStream(iconFile));
			iconFile.delete();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	Long id;
	@Column(name = "bot_id")
	Long botId;
	@Column(name = "mode_name")
	String modeName;
	@Column(name = "mode_type")
	String modeType;
	@Column(name = "user_name")
	String userName;
	@Column(name = "user_url")
	String userUrl;
	@Column(name = "user_location")
	String userLocation;
	@Column(name = "user_description")
	String userDescription;
	@Column(name = "icon")
	byte[] icon;
	@Transient
	private MultipartFile iconFile;

	public void setIconFile(MultipartFile file) {
		try {
			if (!file.isEmpty()) {
				iconFile = file;
				icon = iconFile.getBytes();
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	public MultipartFile getIconFile() {
		return iconFile;
	}
}
