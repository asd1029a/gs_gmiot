package com.danusys.guardian.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

@Repository
public class TestSocketClient {
	private static Logger LOGGER = LogManager
			.getLogger(TestSocketClient.class);
	public String msgSend(String msg, String targetIp, int targetPort) {
		String rMsg = "00000";
		int connectTimeout = 5000;
		int responseTimeout = 5000;
		Socket socketServer = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		String respStr = "";
		try {
			LOGGER.info(" ==== event msg ==== >>>> " + msg);
			System.out.println("msgSend Data Send Message : " + msg);
			byte[] body = msg.getBytes("utf-8");
			socketServer = new Socket();
			socketServer.connect(new InetSocketAddress(targetIp, targetPort), connectTimeout); // 소켓 연결 타입아웃 시간 설정
			socketServer.setSoTimeout(responseTimeout);
			bos = new BufferedOutputStream(socketServer.getOutputStream());
			bos.write(body);
			bos.flush();
			bis = new BufferedInputStream(socketServer.getInputStream());
			byte[] b = new byte[1000];
			int offset = 0;
			offset = bis.read(b, 0, b.length);
			respStr = new String(b, 0, offset);
			LOGGER.debug("msgSend Data Send resultMsg : " + respStr);
			LOGGER.info(" ==== event resultMsg ==== >>>> " + respStr);
			rMsg = respStr;
		} catch (SocketException ce) {
			LOGGER.error(" SocketClient SocketException : {}", ce.getMessage());
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {

			}
		} catch (SocketTimeoutException ste) {
			LOGGER.error("SocketClient SocketTimeoutException : {}", ste.getMessage());
		} catch (Exception ex) {
			LOGGER.error("SocketClient Exception : {}", ex.getMessage());
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
				if (bis != null) {
					bis.close();
				}
				if (socketServer != null) {
					socketServer.close();
				}
			} catch (Exception e) {
				LOGGER.error("SocketClient finally Exception : {}", e.getMessage());
			}
		}
		return rMsg;
	}
}
