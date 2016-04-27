package com.up.ftp.main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.up.ftp.config.UserConfig;

public class FTPService {

	private static Logger log = Logger.getLogger(FTPService.class);

	private static FTPClient ftp;

	public static void main(String[] args) {
		BasicConfigurator.configure();
		UserConfig config = new UserConfig();
		System.out.println("start: ---"+new Date().getTime());
		if (!connect(config)) {
			log.error("connect ftp server fail!");
		}
		try {
			if("upload".equals(config.getRunType())){
				upload(config);
			}else if("download".equals(config.getRunType())){
				downLoad(config);
			}
			
		} catch (Exception e) {
			log.error("upload error :" + e.getMessage());
		}finally{
			ftpLogOut();
			System.out.println("end: ---"+new Date().getTime());
			
		}
	}

	/**
	 * connect ftp server;
	 * 
	 * @param config
	 * @return
	 */
	public static boolean connect(UserConfig config) {
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
			log.error("ftp connect fail :" + e.getMessage());
			return false;
		}
		log.info("ftp connect success");
		return true;
	}
	
	/**
	 * up load file
	 * @param config
	 * @throws Exception
	 */
	public static void upload(UserConfig config) throws Exception {
		File file = new File(config.getLocalPath());
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				try{
					FileInputStream input = new FileInputStream(f);
					ftp.storeFile(f.getName(), input);
					input.close();
					if(config.isDelete())
					{
						f.delete();
					}
				}catch (Exception e) {
					log.error("up load file "+ f.getName() +" fail ! errorMessage:"+e.getMessage());
				}
			}
		} else {
			FileInputStream input = new FileInputStream(file);
			ftp.storeFile(file.getName(), input);
			input.close();
		}
	}
	//
	public static void downLoad(UserConfig config)throws Exception{
		
		FTPFile[] listFiles = ftp.listFiles();
		if(listFiles!=null && listFiles.length!=0){
			for (FTPFile file : listFiles) {
				String fileName = file.getName();
				try{
					BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(config.getLocalPath()+File.separator+fileName));
					boolean rf = ftp.retrieveFile(fileName, os);
					os.close();
					if(!rf)
					{
						log.info("file :"+fileName+" download fail!");
						File f = new File(config.getLocalPath()+File.separator+fileName);
						if(f.exists())
						{
							f.delete();
						}
						continue;
					}
					if(config.isDelete())
					{
						boolean deleteFile = ftp.deleteFile(fileName);
						if(!deleteFile)
						{
							log.info("delete file "+fileName+" fail");
						}
					}
				}catch (Exception e) {
					log.info("download  file "+fileName+" fail! errorMessage: "+e.getMessage());
				}
			}
		}
	}
	
	public static void ftpLogOut() {
		if (null != ftp && ftp.isConnected()) {
			try {
				boolean reuslt = ftp.logout();// 退出FTP服务器
				if (reuslt) {
					log.info("成功退出服务器");
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.warn("退出FTP服务器异常！" + e.getMessage());
			} finally {
				try {
					ftp.disconnect();// 关闭FTP服务器的连接
				} catch (Exception e) {
					e.printStackTrace();
					log.warn("关闭FTP服务器的连接异常！");
				}
			}
		}
	}

}
