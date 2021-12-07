package com.danusys.web.drone.controller;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.net.Socket;
import java.util.Map;

@Controller
public class DroneInfoController {

	@Value("${tcp.server.host}")
	private String tcpServerHost;

	@Value("${tcp.server.port}")
	private int tcpServerPort;

	private final SimpMessagingTemplate simpMessagingTemplate;

	public DroneInfoController(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}


	@MessageMapping("/drone")
	@SendTo("/topic/drone")
	public void receiveSendTo(Map<String, Object> param) {
		System.out.println("~!!!");

		final String droneStart = String.valueOf(param.get("droneLog"));
		System.out.println("###" + droneStart);

//		if("start".equals(droneStart)) {
			try (Socket socket = new Socket(tcpServerHost, tcpServerPort)) {
				MavlinkConnection connection = MavlinkConnection.create(
						socket.getInputStream(),
						socket.getOutputStream());

				MavlinkMessage message;
				while ((message = connection.next()) != null) {
					Object p = message.getPayload();
					System.out.println("#" + message.getSequence() + " --> " + p);
					this.simpMessagingTemplate.convertAndSend("/topic/drone", HtmlUtils.htmlEscape(p.toString()));
					if("stop".equals(droneStart))
						break;

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("데이터 전송 종료~!!");
			}
//		}
	}
}
