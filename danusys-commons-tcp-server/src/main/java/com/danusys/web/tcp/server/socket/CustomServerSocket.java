package com.danusys.web.tcp.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class CustomServerSocket {
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        ServerSocket serverSocket = null;
        ServerThread serverThread;
        try {   // 서버소켓을 생성, 7777 포트와 binding
            serverSocket = new ServerSocket(7777); // 생성자 내부에 bind()가 있고, bind() 내부에 listen() 있음
            serverThread = new ServerThread(serverSocket);
            serverThread.start();

            int temp = input.nextInt(); // 스레드 생성 전에 숫자를 입력하면 바로 SERVER CLOSE!
        } catch (IOException e) {

        } finally {
            serverSocket.close();
        }
    }

//    static String getTime ()
//    {
//        SimpleDateFormat f = new SimpleDateFormat("[hh : mm : ss ]");
//        return f.format(new Date());
//    }
}