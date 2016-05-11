package com.up.ftp.main;

import java.io.File;
import java.util.Properties;

import com.up.ftp.config.UserConfig;
import com.up.ftp.util.Logger;
import com.up.ftp.util.PropertiesUtil;

public class RunFtp {
	
	private static Logger log = Logger.getLogger(RunFtp.class);
	
	
	public static void main(String[] args) {
		//init log4j
//		BasicConfigurator.configure();
		
		File folder = new File("FTPConfig");
		File[] list = folder.listFiles();
		if(list!=null && list.length!=0)
		{
			for (File file : list) {
				try {
					Properties pt = PropertiesUtil.getProperties(file.getPath());
					UserConfig config = new UserConfig(pt,file.getName());
					FTPService service = new FTPService(config);
					service.start();
				} catch (Exception e) {
					log.error("run config file:"+file.getName()+" error, errorMessage: "+e.getMessage());
				}
			}
		}else
		{
			log.info("no file in FTPConfig folder!");
		}
	}
}
