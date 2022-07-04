package com.danusys.web.commons.api.scheduler;

import com.danusys.web.commons.api.scheduler.socket.FacilityTcpServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile(value = {"local", "bsng"})
@RequiredArgsConstructor
public class CctvTcpServer {
//    private FacilityTcpServer facilityTcpServer;
    /**
     * 시설물 수신 데이터 tcp 서버
     */
//    @Scheduled(fixedDelay = 60 * 1000 * 4)
//    public void cctvTcpServerStart() {
//        log.trace("---------------------cctv tcp server start---------------------");
//        facilityTcpServer = new FacilityTcpServer();
//        try {
//            facilityTcpServer.startServer();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        log.trace("---------------------cctv tcp server end  ---------------------");
//    }
}
