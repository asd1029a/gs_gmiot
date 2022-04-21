package com.danusys.web.drone.config;


import com.danusys.web.drone.socket.CustomServerSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServerSocketConfig implements ApplicationRunner {

    private final CustomServerSocket ServerSocket;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        ServerSocket.connectServer(8600);
        log.info("TCP SERVER ON");
    }
}
