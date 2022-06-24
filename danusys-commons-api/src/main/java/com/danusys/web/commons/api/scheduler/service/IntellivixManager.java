package com.danusys.web.commons.api.scheduler.service;

import com.danusys.web.commons.app.RestUtil;
import com.danusys.web.commons.app.StrUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.extensions.IExtension;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.protocols.Protocol;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/03/24
 * Time : 18:12
 * Websocket Test Client
 */

@Profile(value="bsng")
@Slf4j
@Service
public class IntellivixManager {
    private final ObjectMapper objectMapper;

    public IntellivixManager(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void userlogin() throws Exception {
        Map<String, Object> postParam;
        postParam = new HashMap<>();
        String loginUrl = "http://1.213.164.187:7681/users/login";
        postParam.put("id", "intellivix");
        postParam.put("pw", "pass0001!");
        ResponseEntity responseEntity = RestUtil.exchange(loginUrl, HttpMethod.POST, MediaType.APPLICATION_JSON, postParam, String.class);

        if (responseEntity.getStatusCodeValue() != 200) {
            log.debug("error api-key");
            throw new Exception();
        }
        String body = responseEntity.getBody().toString();

        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});
        String apiKey = StrUtils.getStr(resultBody.get("api-key"));

        log.info("apikey : {}",apiKey);
        //String tcpUrl = "ws://1.213.164.187:7681/vaMetadata?api-key=" + apiKey + "&evtMeta=begun,ended,evtImg&faceMeta";
        String tcpUrl = "ws://1.213.164.187:7681/vaMetadata?api-key=" + apiKey + "&faceMeta&evtMeta";
        this.connect(tcpUrl);
    }

    public void connect(String tcpUrl) throws Exception {
        ArrayList<IProtocol> protocols = new ArrayList<IProtocol>();
        protocols.add(new Protocol("va-metadata"));
        Draft_6455 draft_ocppAndFallBack = new Draft_6455(Collections.<IExtension>emptyList(),
                protocols);
        WebSocketClient webSocketClient = new WebSocketClient(new URI(tcpUrl), draft_ocppAndFallBack) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                log.info("connected success");
            }

            @Override
            public void onMessage(String message) {
                //log.info(message);

            }

            @Override
            public void onClose(int i, String s, boolean b) {

            }

            @Override
            public void onError(Exception e) {

            }
        };

        webSocketClient.connect();

    }
    /*
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

            //TODO pattr(보행자속성) 값 not null 체크완료 //face metadata 들어올때 체크 해 봐야함
            @Override
            public void onMessage(String message) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    *//*Map<String, Object> resultMessage = objectMapper.readValue(message, new TypeReference<Map<String, Object>>(){});
                    Map<String,Object> obj;*//*


                    //obj = (Map<String, Object>) ((Map<String,Object>)resultMessage.get("evt")).get("obj");
                    *//*String pattr = "";
                    if(!obj.containsKey("pattr")) {
                        return;
                    }
                    pattr = obj.get("pattr").toString();
                    System.out.println(pattr);*//*

                } catch (Exception e) {
                    e.printStackTrace();
                }

                log.info("receiver : {}" , message);
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
//            postParam.put("id", "intellivix");
//            postParam.put("pw", "pass0001!");
//            String loginUrl = "http://1.213.164.187:5204/users/login";
            String callUrl = "/intellivix/usersLogin";

            ResponseEntity responseEntity = RestUtil.exchange(callUrl,HttpMethod.POST,MediaType.APPLICATION_JSON,postParam);
            if(responseEntity.getStatusCodeValue() != 200) {
                log.debug("error api-key");
                throw new Exception();
            }
//            putParam = new HashMap<>();
//            String body = (String) responseEntity.getBody();
//            ObjectMapper objectMapper = new ObjectMapper();
            //Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});
            //String apiKey = StrUtils.getStr(resultBody.get("api-key"));

            //putParam.put("value", apiKey);
            //RestUtil.exchange("http://"+localIp+":"+serverPort+"/intellivix/updateApiKey/183", HttpMethod.PUT, MediaType.APPLICATION_JSON,putParam);
            //url = "ws://1.213.164.187:7681/vaMetadata?api-key=" + apiKey + "evtMeta=begun,ended,evtImg&faceMeta=recogRegistered";

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("connected url : {}", url);
        return url;
    }*/
}
