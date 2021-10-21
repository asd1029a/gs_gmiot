package com.danusys.guardian.service.drone;

import com.danusys.guardian.common.util.GisUtil;
import com.danusys.guardian.model.BaseDao;
import com.danusys.guardian.socket.WebSocketVerticle;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.net.NetSocket;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DroneServiceImpl implements DroneService {

	private BaseDao baseDao;
	private WebSocketVerticle webSocketVerticle;
	private GisUtil gisUtil;

	@Autowired
	public DroneServiceImpl(BaseDao baseDao, WebSocketVerticle webSocketVerticle, GisUtil gisUtil) {
		this.baseDao = baseDao;
		this.webSocketVerticle = webSocketVerticle;
		this.gisUtil = gisUtil;
	}

	@Override
	public boolean moveDronePosition(NetSocket netSocket, String msg) {
		msg.replaceAll("\\r|\\n", "");
		
		String[] msgArray = msg.split(",");
		try {
			Map<String, Object> msgMap = new HashMap<String, Object>();
			
			String fcltId = msgArray[1].trim();
			String lon = msgArray[2].trim();
			String lat = msgArray[3].trim();

			msgMap.put("fcltId",fcltId);
			msgMap.put("lon",lon);
			msgMap.put("lat",lat);
			
			baseDao.baseUpdate("facility.updateDronePosition", msgMap);
			//Point2D.Double tp = new Point2D.Double();
			//tp = this.gisUtil.convertWGS842ETM(lon, lat);
			
			//msgMap.put("lon", Double.valueOf(tp.x));
			//msgMap.put("lat", Double.valueOf(tp.y));
			
			JsonObject obj = new JsonObject(msgMap);
			
			this.log.info(obj.toString());
			this.webSocketVerticle.getIo()
									.sockets()
									.emit("response", obj);
			
			netSocket.write("send ok");
			netSocket.write("\n");
			this.log
					.info("received socket message 4: OK");
		} catch (Exception e) {
			netSocket.write("send error");
			netSocket.write("\n");
			this.log
					.info("received socket message 6: 11111"
							+ e.getMessage());
			this.log.error(
					"handle Exception : {} ",
					new Object[] { e.getMessage() });
		}
		
		return false;
	}

}
