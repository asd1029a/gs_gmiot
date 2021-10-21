package com.danusys.guardian.socket;

import com.danusys.guardian.service.cctv.CctvService;
import com.danusys.guardian.service.drone.DroneService;
import com.danusys.guardian.service.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.net.NetServer;
import org.vertx.java.core.net.NetSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class NetSocketVerticle extends DefaultEmbeddableVerticle {

	@Value("${danusys.event.port}")
	private int event_port;// = 8010

	private BaseService baseService;
	private CctvService cctvService;
	private DroneService droneService;
	private WebSocketVerticle webSocketVerticle;

	@Autowired
	public NetSocketVerticle(BaseService baseService, CctvService cctvService, DroneService droneService, WebSocketVerticle webSocketVerticle) {
		this.baseService = baseService;
		this.cctvService = cctvService;
		this.droneService = droneService;
		this.webSocketVerticle = webSocketVerticle;
	}

	private NetServer netServer;
	private String targetIp;
	private int targetPort;
	private int connTimeout;
	private int respTimeout;
	//public static final String API_KEY = "KakaoAK 674f28727ad8bf7f447ccbc6b1233801";
	private int wTargetPort;
	private int wConnTimeout;
	private int wRespTimeout;
	

	public void start(Vertx vertx) {
//		try {
//			this.event_port = Integer.parseInt(this.config.getProperty("event_port").trim());
//		} catch (Exception e) {
//			logger.info(
//					"event_port exception : {}===={}",
//					new Object[] {
//							this.config.getProperty("event_port").trim(),
//							e.getMessage() });
//			this.event_port = 8010;
//		}
//		logger.info(" ==== NetSocket Server Start ==== {}", new Object[] { Integer.valueOf(this.event_port) });
		log.info(" ==== NetSocket Server Start ==== {}", this.event_port);
		this.netServer = vertx.createNetServer();
		this.netServer.connectHandler(new Handler<NetSocket>() {
			public void handle(final NetSocket netSocket) {
				NetSocketVerticle.log.info("connect : " + netSocket.remoteAddress());

				netSocket.dataHandler(new Handler<Buffer>() {
					public void handle(Buffer buffer) {
						String msg = buffer.toString("UTF-8");
						// msg = msgParse(msg).toString();
							checkEvent(msg, netSocket);
					}
				});
				netSocket.closeHandler(new Handler<Void>() {
					public void handle(Void arg0) {
						NetSocketVerticle.log.info("closed : " + netSocket.remoteAddress());
					}
				});
			}
		}).listen(this.event_port);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				synchronized (netServer) {
					netServer.close();
				}
			}
		});

	}
	
	public String checkEvent(String msg, NetSocket netSocket){
		String result = "";
		msg = msg.replaceAll("\\r|\\n", "");
		String[] msgArray = msg.split(",");
		System.out.println(msg);
		if ("EVENT".equals(msgArray[0])) {
			result = msg;
			emitEBoard(msg, netSocket);
			//emitEvent(msg, netSocket);
		} else if("Safety".equals(msgArray[0])) {
			result = msg;
			//safetyEvent(result, netSocket);
		} else if("Drone".equals(msgArray[0])) {
			//emitDrone(msg, netSocket);
			droneService.moveDronePosition(netSocket, msg);
		}
		/*else {
			System.out.println("2");
			result = msgParseDuc(msg);
			emitEventDuc(result, netSocket);
		}*/
		
		return result;
	}
	
	public void emitEventDuc(String msg, NetSocket netSocket) {
		netSocket.write(msg);
	}
	
	public void emitEBoard(String msg, NetSocket netSocket) {
		msg.replaceAll("\\r|\\n", "");
		
		String[] msgArray = msg.split(",");
		try {
			Map<String, Object> event = new HashMap<String, Object>();
			Map<String, Object> msgMap = new HashMap<String, Object>();
			
			msgMap.put("evtOcrNo",msgArray[1].trim());
			msgMap.put("evtPrgrsCd",msgArray[2].trim());
			msgMap.put("lon",msgArray[3].trim());
			msgMap.put("lat",msgArray[4].trim());
			
			msgMap.put("rowNm","5");
			msgMap.put("recordCountPerPage","-1");
			
			
			//List<Map<String, Object>> resList = baseService.baseSelectList("event.selectEventList",msgMap);
			List<Map<String, Object>> eventInfoList = baseService.baseSelectList("event.eventInfo", msgMap);
			Map<String, Object> eventInfo = eventInfoList.get(0);
			
			eventInfo.put("rowNm","5");
			eventInfo.put("recordCountPerPage","-1");
			
			//event = cctvService.makeCode(eventInfo);
			//msgSend(event);
			if(msgArray[2].equals("10")) {
				JsonObject obj = new JsonObject(eventInfo);
				
				NetSocketVerticle.log.info(obj.toString());
				NetSocketVerticle.this.webSocketVerticle.getIo()
														.sockets()
														.emit("evtStart", obj);
			} else if(msgArray[2].equals("91")){
				NetSocketVerticle.this.webSocketVerticle.getIo()
					.sockets()
					.emit("evtEnd", "");
				return;
			}
			netSocket.write("send ok");
			netSocket.write("\n");
			NetSocketVerticle.log
					.info("received socket message 4: OK");
		} catch (Exception e) {
			netSocket.write("send error");
			netSocket.write("\n");
			NetSocketVerticle.log
					.info("received socket message 6: 11111"
							+ e.getMessage());
			NetSocketVerticle.log.error(
					"handle Exception : {} ",
					new Object[] { e.getMessage() });
		}
	}
	
	@SuppressWarnings("unused")
	public void emitEvent(String msg, NetSocket netSocket) {
		msg.replaceAll("\\r|\\n", "");
		/*String[] msgArray = msg
				.split(NetSocketVerticle.delimeterMsg);*/
		
		String[] msgArray = msg.split(",");
		try {
			List<Map<String, Object>> resList =  new ArrayList<Map<String, Object>>();
			 
			Map<String,Object> event = new HashMap<String,Object>();
			Map<String,Object> smsUser = new HashMap<String,Object>();
			String message = "";
			System.out.println("evtNo"+msgArray[1]);
			event.put("evtNo",msgArray[1]);
//			event.put("evtPrgrsCd",msgArray[2]);
//			event.put("lat", msgArray[3]);
//			event.put("lon", msgArray[4]);
			resList = baseService.baseSelectList("common.getEventUser",event);
			if(resList.size() > 0){
				for(int a=0;resList.size() > a;a++){
					message = (String)resList.get(a).get("evtNm")+"이벤트가 발생하였습니다.";
					smsUser.put("phoneNum","0^"+resList.get(a).get("phoneNum"));
					smsUser.put("callBack",resList.get(a).get("phoneNum"));
					smsUser.put("message",message);
					//baseService.baseSmsInsert("sms.smsInsert", smsUser);
				}
			}
			JsonObject obj = new JsonObject(event);
			NetSocketVerticle.log.info(obj
					.toString());
			
//			NetSocketVerticle.this.webSocketVerticle
//					.getIo().sockets()
//					.emit("response", obj);

			netSocket.write("00000");
			netSocket.write("\n");
			NetSocketVerticle.log
					.info("received socket message 4: 00000");
		
		} catch (Exception e) {
			netSocket.write("11111");
			netSocket.write("\n");
			NetSocketVerticle.log
					.info("received socket message 6: 11111"
							+ e.getMessage());
			NetSocketVerticle.log.error(
					"handle Exception : {} ",
					new Object[] { e.getMessage() });
		}

	}
	
	/*
	 * 창원시 안심귀가 연동 프로토콜
	 * 
	 */
//	public void safetyEvent(String msg, NetSocket netSocket){
//		msg.replaceAll("\\r|\\n", "");
//		/*String[] msgArray = msg
//				.split(NetSocketVerticle.delimeterMsg);*/
//		try {
//			
//			List<Map<String, Object>> resList =  new ArrayList<Map<String, Object>>();
//			 
//			Map<String,Object> event = new HashMap<String,Object>();
//			Map<String,Object> test = new HashMap<String,Object>();
//			//Map<String,Object> smsUser = new HashMap<String,Object>();
//			String message = "";
//			String[] msgArray = msg.split(",",25);
//			String flag = msgArray[1];
//			int cnt = 0;
//			event.put("flag",flag);
//			event.put("evtId","SAFETY");
//			event.put("evtNm","안심귀가");
//			event.put("evtGrad","20");
//			event.put("rowNm","5");
//			if("0".equals(flag)) {
//				String userId = msgArray[2];
//				String userNm = msgArray[3];
//				String userAge = msgArray[4];
//				String userSex = msgArray[5];
//				String userTel = msgArray[6];
//				String userAdres = msgArray[7];
//				String lat = msgArray[8];
//				String lon = msgArray[9];
//				String speed = msgArray[10];
//				String evtOcrHms = msgArray[11];
//				String guardianNm = msgArray[12];
//				String guardianTel = msgArray[13];
//				String guardianAdres = msgArray[14];
//				String userAcc = msgArray[15];
//				String userHair = msgArray[16];
//				String userShirt = msgArray[17];
//				String userShirtCol = msgArray[18];
//				String userPants = msgArray[19];
//				String userPantsCol = msgArray[20];
//				String userShose = msgArray[21];
//				String userEtc = msgArray[22];
//				String evtOcrNo = msgArray[23];
//				String evtDtl = userNm + "," + userAge + "," + userSex + "," + userTel + "," + userAcc + "," + userHair + "," + userShirt + "," + userShirtCol + "," + userPants + "," + userPantsCol + "," + userShose + "," + userEtc;
//				String code = agentService.getGisCode(lon, lat);
//				String[] gisCode = code.split(",",2);
//				event.put("evtOcrNo",evtOcrNo.trim());
//				event.put("evtPrgrsCd","10");
//				event.put("lat",lat);
//				event.put("lon",lon);
//				event.put("evtDtl",evtDtl);
//				event.put("evtPlace",gisCode[1]);
//				event.put("areaCd",gisCode[0]);
//				event.put("evtOcrYmdHms",evtOcrHms.replace(".","").trim());
//				cnt = Integer.parseInt(baseService.baseSelectOne("event.selectEvtOctCount", event));
//			}
//			else {
//				String evtEndHms = msgArray[11];
//				String evtOcrNo = msgArray[23];
//				event.put("evtPrgrsCd","91");
//				event.put("evtOcrNo",evtOcrNo.trim());
//				event.put("evtEndYmdHms",evtEndHms.replace(".","").trim());
//				cnt = Integer.parseInt(baseService.baseSelectOne("event.selectEvtOctCount", event));
//			}
//			if((cnt > 0 && !"0".equals(flag)) || (cnt < 1 && "0".equals(flag))) {
//				baseService.baseInsert("event.saveSafetyEvent", event);
//				makeCode(event);
//				//msgSend(makeCode(event));
//			} else {
//				throw new Exception();
//			}
//			JsonObject obj = new JsonObject(event);
//			NetSocketVerticle.logger.info(obj
//					.toString());
//			
//			NetSocketVerticle.this.webSocketVerticle
//					.getIo().sockets()
//					.emit("response", obj);
//
//			netSocket.write("00000");
//			netSocket.write("\n");
//			NetSocketVerticle.logger
//					.info("received socket message 4: 00000");
//		
//		} catch (Exception e) {
//			netSocket.write("11111");
//			netSocket.write("\n");
//			NetSocketVerticle.logger
//					.info("received socket message 6: 11111"
//							+ e.getMessage());
//			NetSocketVerticle.logger.error(
//					"handle Exception : {} ",
//					new Object[] { e.getMessage() });
//		}
//	}

}