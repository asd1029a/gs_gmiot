package com.danusys.web.commons.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/03/17
 * Time : 14:11
 */

@Slf4j
public class DanuMqttClient implements MqttCallback {
    private MqttClient mqttClient;
    private MqttConnectOptions option;

    // 메시지 도착 후 응답하는 함수
    private Consumer<HashMap<Object, Object>> FNC = null; // 메시지 도착 후 응답하는 함수
    private Consumer<HashMap<Object, Object>> FNC2 = null; //커넥션이 끊긴 후 응답하는 함수
    private Consumer<HashMap<Object, Object>> FNC3 = null; //커넥션이 끊긴 후 응답하는 함수

    public DanuMqttClient() {

        this.FNC = (arg)-> {
            arg.forEach((key, value)->{
                //System.out.println(String.format("메시지 도착, 키 -> %s, 값 -> %s", key, value));
            });
        };

        try {

            // init
            this.mqttClient("", "", "tcp://1.235.55.18:1883", "clientId1")
                    .subscribe(new String[] {"#"});

            this.initConnectionLost( (arg)->{  //콜백행위1, 서버와의 연결이 끊기면 동작
                arg.forEach((key, value)->{
                    System.out.println( String.format("커넥션 끊김~! 키 -> %s, 값 -> %s", key, value) );
                });
            });

            this.initDeliveryComplete((arg)-> {  //콜백행위2, 메시지를 전송한 이후 동작
                arg.forEach((key, value)->{
                    System.out.println( String.format("메시지 전달 완료~! 키 -> %s, 값 -> %s", key, value) );
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public DanuMqttClient mqttClient(String userName, String password, String serverUri, String clientId) {
        option = new MqttConnectOptions();
        option.setCleanSession(true);
        option.setKeepAliveInterval(30);
        option.setUserName(userName);
        option.setPassword(password.toCharArray());
        try {
            mqttClient = new MqttClient(serverUri, clientId);
            mqttClient.setCallback(DanuMqttClient.this);
            mqttClient.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return this;
    }

    /***
     * 구독 대상을 전달합니다.
     *
     * **/
    public boolean subscribe(String... topics){
        try {
            if(topics != null){
                for(String topic : topics){
                    mqttClient.subscribe(topic,0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    @Override
    public void connectionLost(Throwable throwable) {
        if(FNC2 != null){
            HashMap<Object, Object> result = new HashMap<>();
            result.put("result", throwable);
            FNC2.accept(result);
            throwable.printStackTrace();
        }
    }

    // 메시지 도착
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        if(FNC != null) {
            HashMap<Object,Object> result = new HashMap<>();
            result.put("topic", topic);
            result.put("message", new String(mqttMessage.getPayload(), "UTF-8"));
            FNC.accept(result); // 콜백 행위 실행
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        if(FNC3 != null){
            HashMap<Object, Object> result = new HashMap<>();
            try {
                result.put("result", iMqttDeliveryToken);
            } catch (Exception e) {
                e.printStackTrace();
                result.put("result", "ERROR");
                result.put("error", e.getMessage());
            }
            FNC3.accept(result);
        }
    }

    /**
     * 커넥션이 끊어진 이후의 콜백행위를 등록합니다.
     * 해쉬맵 형태의 결과에 키는 result, 값은 Throwable 객체를 반환 합니다.
     * **/
    public void initConnectionLost (Consumer<HashMap<Object, Object>> fnc){
        FNC2 = fnc;
    }

    /**
     * 커넥션이 끊어진 이후의 콜백행위를 등록합니다.
     * 해쉬맵 형태의 결과에 키는 result, 값은 Throwable 객체를 반환 합니다.
     * **/
    public void initDeliveryComplete (Consumer<HashMap<Object, Object>> fnc){
        FNC3 = fnc;
    }


    // 전송
    public boolean sender(String topic, String msg) throws MqttPersistenceException, MqttException{
        MqttMessage message = new MqttMessage();
        message.setPayload(msg.getBytes());
        mqttClient.publish(topic, message);
        return true;
    }

    public void mqttDisconnection(){
        if(mqttClient != null){
            try {
                boolean connected = mqttClient.isConnected();
                mqttClient.disconnect();
                mqttClient.close();
                System.out.println("@@@@@@@@@test: " + connected);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

}
