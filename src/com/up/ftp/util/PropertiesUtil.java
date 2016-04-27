package com.up.ftp.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesUtil {
	
	private static Logger log = Logger.getLogger(PropertiesUtil.class);
	
	/**
	 * get config file 
	 * @param configPath 
	 * @return
	 */
	public static Properties getProperties(String configPath) {
		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(configPath);
			prop.load(in);
		} catch (Exception e1) {
			log.error("load config file error: "+e1.getMessage());
			e1.printStackTrace();
			System.exit(0);
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return prop;
	}
}
