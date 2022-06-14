package com.danusys.web.commons.api.scheduler.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;


/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/03/17
 * Time : 14:11
 */

@Slf4j
@Getter
public class YjMqttManager {
    private MqttClient mqttClient;
    private MqttConnectOptions option;

    public YjMqttManager() {
        this.init("tcp://1.235.55.18:1883");
    }

    public void init(String serverUri) {
        option = new MqttConnectOptions();
        option.setCleanSession(true);
        option.setKeepAliveInterval(30);
        try {
            mqttClient = new MqttClient(serverUri,"");
            mqttClient.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
