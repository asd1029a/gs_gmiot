package com.danusys.web.commons.mqtt;

import java.util.HashMap;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/03/23
 * Time : 12:07
 */
public class DanuMqttClientTest {

    @org.junit.Test
    public static void main(String [] args) {
        final Consumer<HashMap<Object, Object>> pdk = (arg)-> {
            arg.forEach((key, value)->{
                System.out.println(String.format("메시지 도착, 키 -> %s, 값 -> %s", key, value));
            });
        };

        // pdk 전달
        DanuMqttClient testClient = new DanuMqttClient(pdk);

        try {

            // init
            testClient.mqttClient("", "", "tcp://1.235.55.18:1883", "clientId1")
                    .subscribe(new String[] {"#"});

            testClient.initConnectionLost( (arg)->{  //콜백행위1, 서버와의 연결이 끊기면 동작
                arg.forEach((key, value)->{
                    System.out.println( String.format("커넥션 끊김~! 키 -> %s, 값 -> %s", key, value) );
                });
            });

            testClient.initDeliveryComplete((arg)-> {  //콜백행위2, 메시지를 전송한 이후 동작
                arg.forEach((key, value)->{
                    System.out.println( String.format("메시지 전달 완료~! 키 -> %s, 값 -> %s", key, value) );
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*new Thread( ()-> {
            try {
                Thread.sleep(9000);
                testClient.sender("#", "");
                testClient.testClose();  //종료는 이렇게!
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
*/
    }

}