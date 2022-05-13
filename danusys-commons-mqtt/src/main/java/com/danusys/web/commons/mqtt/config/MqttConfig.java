package com.danusys.web.commons.mqtt.config;

import com.danusys.web.commons.mqtt.DanuMqttClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/05/10
 * Time : 19:40
 */

//TODO 영주 버전만 실행 해야함
@Configuration
public class MqttConfig {
    @Bean
    public DanuMqttClient danuMqttClient() {
        return new DanuMqttClient();
    }
}
