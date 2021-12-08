package com.danusys.web.drone.controller;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Slf4j
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

	Map<String, Object> messages = new HashMap<>();

	@MessageMapping("/drone")
	@SendTo("/topic/drone")
	public void topicDroneSendTo(Map<String, Object> param) {
		final String droneStart = String.valueOf(param.get("droneLog"));
		log.info("###topicDroneSendTo : {}" + droneStart);


//		if("start".equals(droneStart)) {
			try (Socket socket = new Socket(tcpServerHost, tcpServerPort)) {
				MavlinkConnection connection = MavlinkConnection.create(
						socket.getInputStream(),
						socket.getOutputStream());

				MavlinkMessage message;
				while ((message = connection.next()) != null) {
					Object p = message.getPayload();
					messages.put(p.getClass().getSimpleName(), p);
					this.simpMessagingTemplate.convertAndSend("/topic/drone", HtmlUtils.htmlEscape(p.toString()));
					if("stop".equals(droneStart))
						break;
				}

				log.info("###topicDroneSendTo last : {}" +
						messages.values().stream()
								.map(String::valueOf)
								.collect(joining("\n"))
				);

			this.simpMessagingTemplate.convertAndSend("/topic/drone", HtmlUtils.htmlEscape(messages.values().stream()
					.map(String::valueOf)
					.collect(joining("\n"))));

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				log.info("###topicDroneSendTo : 데이터 전송 종료~");
			}
//		}
	}
}
