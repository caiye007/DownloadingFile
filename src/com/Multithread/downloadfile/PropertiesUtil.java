package com.Multithread.downloadfile;

/**
 * FileName: PropertiesUtil
 * Author:   ��Ԫ��
 * Date:     2018/4/13 10:52
 * Description:��ȡproperties�ļ�������
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {
	/**
	 * ���ܣ���userInfo.properties�ļ��ж�ȡ��һ��key��Ӧ��value
	 * 
	 * @param ����һ��keyֵ
	 * @return ����valueֵ
	 */
	public static String getValue(String keyName) {
		String value = "";
		Properties p = new Properties();
		try {
			// ��ȡjdbc.properties�ļ�,ʹ��InputStreamReader�ַ�����ֹ�ļ��г������ĵ�������
			//p.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream("Download-configuration.properties"), "UTF-8"));
			
			p.load(new InputStreamReader(new FileInputStream(System.getProperty("user.dir") + "/Download-configuration.properties"), "UTF-8"));
			
			value = p.getProperty(keyName, "properties�ļ���ȡʧ��");
		} catch (Exception e) {
			if(String.valueOf(e).contains("Download-configuration.properties")){
				System.out.println("ϵͳ�Ҳ���   Download-configuration.properties �ļ�·��");
			}else{
				e.printStackTrace();
			}
		}
		return value;
	}
}
