package com.danusys.web.platform.socket;


import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@Repository
public class SocketClient {

	@Value("${danusys.target.ip}")
	private static String wTargetIp;
	
	public static void msgSend(Map<String, Object> param) throws SQLException, IOException {
		  
		  log.debug(" --------------전문 전송 시작--------------- ");
		  ObjectMapper objectMapper = new ObjectMapper();
		    Socket socket = null;
		    BufferedWriter oos = null;
			String respStr = "00000";

			URL url = new URL(wTargetIp);
			URLConnection conn = url.openConnection();

			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

			try {
				String value = objectMapper.writeValueAsString(param);
			    value = new String(value.getBytes("utf-8"), "utf-8");
			    wr.write(value);
			    wr.flush();

			    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
			    String line;
			    while((line = rd.readLine()) != null){
			    	System.out.println(line);
			    }

			    wr.close();
			    rd.close();
//			    System.out.println("================="+value);
//			    socket = new Socket();
//			    socket.connect(new InetSocketAddress(wTargetIp, wTargetPort), wConnTimeout);		// 소켓 연결 타입아웃 시간 설정
//			    socket.setSoTimeout(wRespTimeout);
//
//			    oos = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
//			    oos.write(value);
//			    oos.flush();

			}catch(SocketException ce){
				ce.printStackTrace();
				log.error(" ==== SocketException >>>> [{}]",ce );
				try { Thread.sleep(300); } catch (InterruptedException e) {}

			}catch(SocketTimeoutException ste) {
				log.error(" ==== SocketTimeoutException >>>> [{}]",ste );
				ste.printStackTrace();

			}catch(Exception ex){
				log.error(" ==== Exception >>>> [{}]",ex );
				ex.printStackTrace();

			}finally{
				try{
					if(oos != null)	oos.close();
					if(socket != null) socket.close();
//					return respStr;
				}catch(Exception e){
					log.error(" ==== Exception >>>> [{}]",e );
					e.printStackTrace();
//					return respStr;
				}
			}
		 	
		 
		}
}
