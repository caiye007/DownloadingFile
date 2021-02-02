package com.Multithread.downloadfile;

/**
 * FileName: DownloadUtil
 * Author:   戴元川
 * Date:     2018/4/13 10:52
 * Description:多线程下载文件
 */

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtil {
	// 定义成员变量
	private String path; // 远程资源路径
	private String targetPath; // 本地存储路径
	private DownFileThread[] threads; // 线程list
	private int threadNum; // 线程数量
	private long length; // 下载的文件大小

	// 构造初始化
	public DownloadUtil(String path, String targetPath, int threadNum) {
		super();
		this.path = path;
		this.targetPath = targetPath;
		this.threads = new DownFileThread[threadNum];
		this.threadNum = threadNum;
	}

	// 多线程下载文件资源
	public void download() {
		URL url;
		try {
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5 * 1000); // 设置超时时间为5秒
			conn.setRequestMethod("GET");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("accept", "*/*");

			// 获取远程文件的大小
			length = conn.getContentLength();
			conn.disconnect();

			// 设置本地文件大小
			RandomAccessFile targetFile = new RandomAccessFile(targetPath, "rw");
			targetFile.setLength(length);

			// 每个线程下载大小
			long avgPart = length / threadNum + 1;
			// 下载文件
			for (int i = 0; i < threadNum; i++) {
				long startPos = avgPart * i;
				RandomAccessFile targetTmp = new RandomAccessFile(targetPath, "rw");
				targetTmp.seek(startPos); // 分段下载
				threads[i] = new DownFileThread(startPos, targetTmp, avgPart);
				threads[i].start();
			}
		} catch (Exception e) {
			if(String.valueOf(e).contains("java.lang.NumberFormatException")){
				System.out.println("线程读取错误");
			}else if(String.valueOf(e).contains("java.io.FileNotFoundException")){
				System.out.println("源URL错误");
				System.exit(0);
			}else{
				e.printStackTrace();
			}
		}
	}

	// 监控下载进度
	public double getDownRate() {
		int currentSize = 0;
		for (int i = 0; i < threadNum; i++) {
			currentSize += threads[i].length;
		}
		return currentSize * 1.0 / length;
	}

	// 定义线程类
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
				conn.setReadTimeout(5 * 1000); // 设置超时时间为5秒
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
					System.out.println("线程读取错误");
				}else if(String.valueOf(e).contains("java.io.FileNotFoundException")){
					System.out.println("源URL错误");
					System.exit(0);
				}else{
					e.printStackTrace();
				}
			}
		}
	}

	// 测试
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
			// 主线程负责下载文件，在启动一个线程负责监控下载的进度
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (download.getDownRate() < 1) {
						System.out.println(String.format("%.1f", download.getDownRate() * 100) + "%");
						try {
							Thread.sleep(1000); // 200毫秒扫描一次
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			}).start();
		} catch (Exception e) {
			if(String.valueOf(e).contains("java.lang.NumberFormatException")){
				System.out.println("线程读取错误");
			}else if(String.valueOf(e).contains("java.io.FileNotFoundException")){
				System.out.println("源URL错误");
				System.exit(0);
			}
		}
	}
}
