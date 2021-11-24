package com.danusys.web.platform.service.drone;

import org.vertx.java.core.net.NetSocket;

public interface DroneService {
	boolean moveDronePosition(NetSocket netSocket, String msg);
}
