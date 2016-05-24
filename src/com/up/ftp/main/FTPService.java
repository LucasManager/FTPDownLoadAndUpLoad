package com.up.ftp.main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.up.ftp.config.UserConfig;
import com.up.ftp.util.Logger;

public class FTPService extends Thread {

	private static Logger log = Logger.getLogger(FTPService.class);

	private FTPClient ftp;
	private UserConfig config;

	public FTPService(UserConfig config)
	{
		this.config = config;
	}
	
	public void run(){
		if (!connect(config)) {
			log.error(config.getConfigFileName()+" :connect ftp server fail!");
		}
		try {
			if("upload".equals(config.getRunType())){
				upload(config);
			}else if("download".equals(config.getRunType())){
				downLoad(config);
			}
			
		} catch (Exception e) {
			log.error(config.getConfigFileName()+" :upload error :" + e.getMessage());
		}finally{
			ftpLogOut();
//			System.out.println(config.getConfigFileName()+" end: ---"+new Date().getTime());
			
		}
	}

	/**
	 * connect ftp server;
	 * 
	 * @param config
	 * @return
	 */
	private boolean connect(UserConfig config) {
		ftp = new FTPClient();
		try {
			
			ftp.connect(config.getIpAddress(), Integer
					.valueOf(config.getPort()));
			if (config.getBufferSize() != null
					&& !(config.getBufferSize().equals("") || config
							.getBufferSize().equals("0"))) {
				ftp.setBufferSize(Integer.valueOf(config.getBufferSize()));
			}
			ftp.login(config.getUserName(), config.getPassword());
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();
				return false;
			}
			ftp.changeWorkingDirectory(config.getRemotePath());
			if(!"active".equals(config.getFtpModel()))
			{
				ftp.enterLocalPassiveMode();
			}
		} catch (Exception e) {
			log.error(config.getConfigFileName()+" : ftp connect fail :" + e.getMessage());
			return false;
		}
		log.info(config.getConfigFileName()+" : ftp connect success");
		return true;
	}
	
	/**
	 * up load file
	 * @param config
	 * @throws Exception
	 */
	private void upload(UserConfig config) throws Exception {
		File file = new File(config.getLocalPath());
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				try{
					System.out.println("upload file:["+f.getName()+"]");
					FileInputStream input = new FileInputStream(f);
					boolean result = ftp.storeFile(f.getName(), input);
					input.close();
					if(config.isDelete() && result)
					{
						f.delete();
					}else if(!result)
					{
						log.info(this.config.getConfigFileName()+" : file :"+f.getName()+" upload fail!");
					}
				}catch (Exception e) {
					log.error(this.config.getConfigFileName()+" : up load file "+ f.getName() +" fail ! errorMessage:"+e.getMessage());
				}
			}
		} else {
			FileInputStream input = new FileInputStream(file);
			ftp.storeFile(file.getName(), input);
			input.close();
		}
	}
	//
	private void downLoad(UserConfig config)throws Exception{
		
		FTPFile[] listFiles = ftp.listFiles();
		if(listFiles!=null && listFiles.length!=0){
			for (FTPFile file : listFiles) {
				String fileName = file.getName();
				try{
					System.out.println("downLoad file:["+fileName+"]");
					BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(config.getLocalPath()+File.separator+fileName));
					boolean rf = ftp.retrieveFile(fileName, os);
					os.close();
					if(!rf)
					{
						log.info(this.config.getConfigFileName()+" : file :"+fileName+" download fail!");
						File f = new File(config.getLocalPath()+File.separator+fileName);
						if(f.exists())
						{
							f.delete();
						}
						continue;
					}
					if(config.isDelete() && rf)
					{
						boolean deleteFile = ftp.deleteFile(fileName);
						if(!deleteFile)
						{
							log.info(this.config.getConfigFileName()+" : delete file "+fileName+" fail");
						}
					}
				}catch (Exception e) {
					log.info(this.config.getConfigFileName()+" : download  file "+fileName+" fail! errorMessage: "+e.getMessage());
				}
			}
		}
	}
	
	private void ftpLogOut() {
		if (null != ftp && ftp.isConnected()) {
			try {
				boolean reuslt = ftp.logout();// 退出FTP服务器
				if (reuslt) {
					log.info(this.config.getConfigFileName()+" : logout ftp server success!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.warn(this.config.getConfigFileName()+" : logout FTP error！" + e.getMessage());
			} finally {
				try {
					ftp.disconnect();// 关闭FTP服务器的连接
				} catch (Exception e) {
					e.printStackTrace();
					log.warn(this.config.getConfigFileName()+" : shut down FTP connection error"+e.getMessage());
				}
			}
		}
	}

	public void setConfig(UserConfig config) {
		this.config = config;
	}

	public UserConfig getConfig() {
		return config;
	}

}
