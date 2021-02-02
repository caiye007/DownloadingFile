package com.Multithread.downloadfile;

/**
 * FileName: PropertiesUtil
 * Author:   戴元川
 * Date:     2018/4/13 10:52
 * Description:读取properties文件工具类
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {
	/**
	 * 功能：从userInfo.properties文件中读取出一个key对应的value
	 * 
	 * @param 接收一个key值
	 * @return 返回value值
	 */
	public static String getValue(String keyName) {
		String value = "";
		Properties p = new Properties();
		try {
			// 读取jdbc.properties文件,使用InputStreamReader字符流防止文件中出现中文导致乱码
			//p.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream("Download-configuration.properties"), "UTF-8"));
			
			p.load(new InputStreamReader(new FileInputStream(System.getProperty("user.dir") + "/Download-configuration.properties"), "UTF-8"));
			
			value = p.getProperty(keyName, "properties文件获取失败");
		} catch (Exception e) {
			if(String.valueOf(e).contains("Download-configuration.properties")){
				System.out.println("系统找不到   Download-configuration.properties 文件路径");
			}else{
				e.printStackTrace();
			}
		}
		return value;
	}
}
