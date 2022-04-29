package com.danusys.web.socket;

import com.danusys.web.socket.thread.EchoThread;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class TcpApplication {
    static ServerSocket server;
    public static void main(String[] args){
        try{
            server = new ServerSocket(8500);
            log.info("접속을 기다립니다.");
            while(true){
                Socket sock = server.accept();
                EchoThread echothread = new EchoThread(sock);
                InetAddress inetaddr = sock.getInetAddress();
                String clientAddress = inetaddr.getHostAddress();
                log.info("{} 로 부터 접속하였습니다.", clientAddress);
                System.out.println();
                echothread.start();
                //threadList.add(echothread);
            } // while
        }catch(IOException e){
            System.out.println("서버에러 : "+e);
        }finally {
            try {
                if (server != null && server.isClosed() == false) {
                    server.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } // main
}
