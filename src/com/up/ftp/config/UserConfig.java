package com.up.ftp.config;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.up.ftp.util.PropertiesUtil;

public class UserConfig {

	private static Logger log = Logger.getLogger(UserConfig.class);
	private String userName;
	private String password;
	private String ipAddress;
	private String port;//ftp port: default 21;
	
	private String bufferSize; // et the internal buffer size for buffered data streams.
	
	private String remotePath;
	private String localPath;
	
	private String runType;//upload or download;
	
	private String ftpModel;// the server is "active" Mode or "Passive" mode 
	
	private boolean isDelete = false;//if delete when finish
	
	private String configFileName;
	
	public UserConfig(Properties pt,String fileName) throws Exception{
		//parse config file:
		configFileName = fileName;
		userName = pt.getProperty("userName");
		password = pt.getProperty("password");
		ipAddress = pt.getProperty("ftpserver");
		port = pt.getProperty("port");
		bufferSize = pt.getProperty("bufferSize");
		remotePath = pt.getProperty("ftp.filePath");
		localPath = pt.getProperty("local.filePath");
		runType = pt.getProperty("RunType");
		ftpModel = pt.getProperty("FTPMode");
		String str = pt.getProperty("isDeleteFile");
		if(str!=null && !str.equals(""))
		{
			isDelete = str.equals("true")?true:false;
		}
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getPort() {
		return port;
	}
	
	public void setBufferSize(String bufferSize) {
		this.bufferSize = bufferSize;
	}

	public String getBufferSize() {
		return bufferSize;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setRunType(String runType) {
		this.runType = runType;
	}

	public String getRunType() {
		return runType;
	}

	public void setFtpModel(String ftpModel) {
		this.ftpModel = ftpModel;
	}

	public String getFtpModel() {
		return ftpModel;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public String getConfigFileName() {
		return configFileName;
	}

}
