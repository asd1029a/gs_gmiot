package com.danusys.web.platform.socket;

import com.danusys.web.commons.app.RestUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.extensions.IExtension;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.protocols.Protocol;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/03/24
 * Time : 18:12
 * Websocket Test Client
 */

@Slf4j
@Component
public class IntellivixWebsocketEventClient {
    private String localIp = "127.0.0.1";
    /*@Value("${server.port}")*/
    private int serverPort = 8400;
    public void connect() throws Exception {

        String url = getUrl();
        // This draft allows the specific Sec-WebSocket-Protocol and also provides a fallback, if the other endpoint does not accept the specific Sec-WebSocket-Protocol
        ArrayList<IProtocol> protocols = new ArrayList<IProtocol>();
        protocols.add(new Protocol("va-metadata"));
        Draft_6455 draft_ocppAndFallBack = new Draft_6455(Collections.<IExtension>emptyList(),
                protocols);
        WebSocketClient webSocketClient = new WebSocketClient(new URI(url), draft_ocppAndFallBack) {

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                log.info("connected");
                Runnable task = () -> {
                    Map<String, Object> param = null;
                    ResponseEntity responseEntity = null;
                    try {

                        param = new HashMap<>();
                        param.put("callUrl", "/intellivix/keepalive");
                        //param.put("api")
                        RestUtil.exchange("http://" + localIp + ":" + serverPort + "/api/call",HttpMethod.POST,MediaType.APPLICATION_JSON,param);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                scheduledExecutorService.scheduleAtFixedRate(task, 600, 600, TimeUnit.SECONDS);
            }

            @Override
            public void onMessage(String message) {
                System.out.println("received: " + message);
                // 맞으면 업데이트
                // 틀리면
            }


            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Connection closed by code: " + code);
                if(code != 1000) {
                    this.reconnect();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        };
        webSocketClient.connect();
    }

    private String getUrl() {
        String url = "";
        Map<String, Object> postParam;
        Map<String, Object> putParam;
        try {
            postParam = new HashMap<>();
            postParam.put("callUrl", "/intellivix/usersLogin");
            ResponseEntity responseEntity = RestUtil.exchange("http://" + localIp + ":" + serverPort + "/api/call",HttpMethod.POST,MediaType.APPLICATION_JSON,postParam);
            if(responseEntity.getStatusCodeValue() != 200) {
                log.debug("error api-key");
                throw new Exception();
            }
            putParam = new HashMap<>();
            String body = (String) responseEntity.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});
            String apiKey = resultBody.get("apiKey").toString();

            putParam.put("value", apiKey);
            RestUtil.exchange("http://"+localIp+":"+serverPort+"/intellivix/updateApiKey/130", HttpMethod.PUT, MediaType.APPLICATION_JSON,putParam);
            url = "ws://intellivix.iptime.org:2257/vaMetadata?api-key=" + apiKey + "&evtMeta=begun,ended,evtImg&faceMeta=recogRegistered";

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }
}
