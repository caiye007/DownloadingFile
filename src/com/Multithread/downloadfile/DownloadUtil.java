package com.Multithread.downloadfile;

/**
 * FileName: DownloadUtil
 * Author:   ��Ԫ��
 * Date:     2018/4/13 10:52
 * Description:���߳������ļ�
 */

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtil {
	// �����Ա����
	private String path; // Զ����Դ·��
	private String targetPath; // ���ش洢·��
	private DownFileThread[] threads; // �߳�list
	private int threadNum; // �߳�����
	private long length; // ���ص��ļ���С

	// �����ʼ��
	public DownloadUtil(String path, String targetPath, int threadNum) {
		super();
		this.path = path;
		this.targetPath = targetPath;
		this.threads = new DownFileThread[threadNum];
		this.threadNum = threadNum;
	}

	// ���߳������ļ���Դ
	public void download() {
		URL url;
		try {
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5 * 1000); // ���ó�ʱʱ��Ϊ5��
			conn.setRequestMethod("GET");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("accept", "*/*");

			// ��ȡԶ���ļ��Ĵ�С
			length = conn.getContentLength();
			conn.disconnect();

			// ���ñ����ļ���С
			RandomAccessFile targetFile = new RandomAccessFile(targetPath, "rw");
			targetFile.setLength(length);

			// ÿ���߳����ش�С
			long avgPart = length / threadNum + 1;
			// �����ļ�
			for (int i = 0; i < threadNum; i++) {
				long startPos = avgPart * i;
				RandomAccessFile targetTmp = new RandomAccessFile(targetPath, "rw");
				targetTmp.seek(startPos); // �ֶ�����
				threads[i] = new DownFileThread(startPos, targetTmp, avgPart);
				threads[i].start();
			}
		} catch (Exception e) {
			if(String.valueOf(e).contains("java.lang.NumberFormatException")){
				System.out.println("�̶߳�ȡ����");
			}else if(String.valueOf(e).contains("java.io.FileNotFoundException")){
				System.out.println("ԴURL����");
				System.exit(0);
			}else{
				e.printStackTrace();
			}
		}
	}

	// ������ؽ���
	public double getDownRate() {
		int currentSize = 0;
		for (int i = 0; i < threadNum; i++) {
			currentSize += threads[i].length;
		}
		return currentSize * 1.0 / length;
	}

	// �����߳���
	class DownFileThread extends Thread {
		private long startPos;
		private RandomAccessFile raf;
		private long size;
		private long length;

		public DownFileThread(long startPos, RandomAccessFile raf, long size) {
			super();
			this.startPos = startPos;
			this.raf = raf;
			this.size = size;
		}

		@Override
		public void run() {
			URL url;
			try {
				url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(5 * 1000); // ���ó�ʱʱ��Ϊ5��
				conn.setRequestMethod("GET");
				conn.setRequestProperty("connection", "keep-alive");
				conn.setRequestProperty("accept", "*/*");

				InputStream in = conn.getInputStream();
				in.skip(this.startPos);
				byte[] buf = new byte[1024];
				int hasRead = 0;
				while (length < size && (hasRead = in.read(buf)) != -1) {
					raf.write(buf, 0, hasRead);
					length += hasRead;
				}
				raf.close();
				in.close();
			} catch (Exception e) {
				if(String.valueOf(e).contains("java.lang.NumberFormatException")){
					System.out.println("�̶߳�ȡ����");
				}else if(String.valueOf(e).contains("java.io.FileNotFoundException")){
					System.out.println("ԴURL����");
					System.exit(0);
				}else{
					e.printStackTrace();
				}
			}
		}
	}

	// ����
	public static void main(String[] args) {
		try {
			int threadNum = Integer.valueOf(PropertiesUtil.getValue("threadNum"));
			if(threadNum<1){
				threadNum = 1;
			}
			if(threadNum>10){
				threadNum = 10;
			}
			final DownloadUtil download = new DownloadUtil(PropertiesUtil.getValue("Path"),PropertiesUtil.getValue("targetPath"), threadNum);
			download.download();
			// ���̸߳��������ļ���������һ���̸߳��������صĽ���
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (download.getDownRate() < 1) {
						System.out.println(String.format("%.1f", download.getDownRate() * 100) + "%");
						try {
							Thread.sleep(1000); // 200����ɨ��һ��
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			}).start();
		} catch (Exception e) {
			if(String.valueOf(e).contains("java.lang.NumberFormatException")){
				System.out.println("�̶߳�ȡ����");
			}else if(String.valueOf(e).contains("java.io.FileNotFoundException")){
				System.out.println("ԴURL����");
				System.exit(0);
			}
		}
	}
}
