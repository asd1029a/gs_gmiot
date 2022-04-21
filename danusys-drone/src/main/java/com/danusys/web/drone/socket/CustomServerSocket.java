package com.danusys.web.drone.socket;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.service.DroneSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;

@Component
@RequiredArgsConstructor
public class CustomServerSocket {


        public ServerSocket serverSocket =null;
        private ServerThread serverThread;

        private final DroneSocketService droneSocketService;
        public void connectServer(int port) throws IOException {
            try {   // 서버소켓을 생성, 7777 포트와 binding
                serverSocket = new ServerSocket(port); // 생성자 내부에 bind()가 있고, bind() 내부에 listen() 있음
                serverThread = new ServerThread(serverSocket,droneSocketService);
                serverThread.start();




            } catch (IOException e) {
                System.out.println("서버 종료..");
                serverSocket.close();
            }
        }
        public ServerThread getServerThread(){
            return this.serverThread;
        }

}
