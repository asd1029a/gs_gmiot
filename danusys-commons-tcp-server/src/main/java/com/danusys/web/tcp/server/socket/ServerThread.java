package com.danusys.web.tcp.server.socket;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/03/21
 * Time : 6:55 PM
 */
public class ServerThread extends Thread {
    ServerSocket serverSocket;
    int count = 1;

    public ServerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run () {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write("hello client \n".getBytes());
                System.out.println("    Thread :" + count);
                ClientThread clientThread = new ClientThread(socket, count);
                clientThread.start();
                count++;
//                    socket.close();
            }
        } catch (Exception e) {
            System.out.println("    SERVER CLOSE    ");
            count--;
        }
    }
}
