package com.danusys.web.commons.socket.config;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

@Component
public class CustomServerSocket {
        public ServerSocket serverSocket =null;
        public Socket socket = null;
        public ServerThread serverThread;
        ArrayList<ServerThread> threadList = new ArrayList<ServerThread>();


    public void connectServer(int port) throws IOException {
            try {   // 서버소켓을 생성, 7777 포트와 binding
                serverSocket = new ServerSocket(port); // 생성자 내부에 bind()가 있고, bind() 내부에 listen() 있음
                while (true) {
                    socket = serverSocket.accept();
                    serverThread = new ServerThread(socket);
                    threadList.add(serverThread);
                    serverThread.start();
                }
            } catch (IOException e) {
                System.out.println("통신소켓 생성불가");
                if(!serverSocket.isClosed()) {
                    stopServer();
                }

            }catch(Exception e) {
                System.out.println("서버소켓 생성불가");
                if(!serverSocket.isClosed()) {
                    stopServer();
                }
            }
            }
    public void stopServer() {
        try {
            Iterator<ServerThread> iterator = threadList.iterator();
            //chatlist에 있는 스레드 전체를 가져오기 위해 iterator 객체 생성
            while (iterator.hasNext()) { //다음 객체가 있는 동안
                ServerThread list = iterator.next(); // 다음 객체를 스레드에 대입
                list.socket.close(); //해당 스레드 통신소켓제거
                iterator.remove(); //스레드 제거
            }
            if(serverSocket!=null && !serverSocket.isClosed()) {
                serverSocket.close(); //서버소켓 닫기
            }
            System.out.println("서버종료");
        }catch (Exception e) {}
    }
}

