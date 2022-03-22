/*
package com.danusys.web.tcp.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationStartupTask implements ApplicationListener<ApplicationReadyEvent> {

    private final ServerSocket serverSocket;

    //스프링부트 서비스를 시작 시 초기화하는 코드를 Bean으로 만들때 사용
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        serverSocket.start();
    }
}*/
