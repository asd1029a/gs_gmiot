package com.danusys.web.platform.socket;

import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;

@Slf4j
//@Component
public class WebSocketVerticle extends DefaultEmbeddableVerticle {


	@Value("${danusys.websocket.port}")
	private int websocket_port;
	private SocketIOServer io;

	public void start(Vertx vertx) {
//		try {
//			this.websocket_port = Integer.parseInt();
//		} catch (Exception e) {
//			logger.info(
//					"websocket_port exception : {}===={}",
//					new Object[] {
//							this.config.getProperty("websocket_port").trim(),
//							e.getMessage() });
//			this.websocket_port = 8020;
//		}
		HttpServer server = vertx.createHttpServer();

//		logger.info(" ==== WebSocket Server Start ==== {}", new Object[] { Integer.valueOf(this.websocket_port) });
		log.info(" ==== WebSocket Server Start ==== {}", this.websocket_port );

		this.io = new DefaultSocketIOServer(vertx, server);
		this.io.sockets().onConnection(new Handler<SocketIOSocket>() {
			public void handle(final SocketIOSocket socket) {
				socket.emit("welcome");
				socket.on("msg", new Handler<JsonObject>() {
					public void handle(JsonObject msg) {
						socket.emit("msg", msg);
						WebSocketVerticle.log.info("get message : " + msg.getString("msg"));
					}
				});
			}
		});
		server.listen(this.websocket_port);
	}

	public SocketIOServer getIo() {
		return this.io;
	}

	public void sendMsg(JsonObject msg) {
		if (this.io != null) {
			log.info("get message : " + msg.getString("msg"));
			this.io.sockets().emit("response", msg);
		}
	}

}
