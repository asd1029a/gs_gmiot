package com.danusys.web.commons.socket.config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/03/21
 * Time : 6:55 PM
 */

public class ServerThread extends Thread {
    Socket socket;
    int count = 1;
    HashMap<Integer,Socket> socketList = new HashMap<>();

    public ServerThread(Socket socket) {
        this.socket = socket;
    }
    public HashMap<Integer,Socket> getSocketList(){
        return socketList;
    }


    @Override
    public void run () {
        try {
            System.out.println("Thread " + count + "connected");
            //OutputStream outputStream = socket.getOutputStream();
            //outputStream.write("hello client \n".getBytes());
            //ClientThread testThread = new ClientThread(socket, count);
            //socketList.put(count,testThread.getSocket());
            socketList.put(count,socket);
            count++;
        } catch (Exception e) {
            System.out.println("    SERVER CLOSE    ");
            try {
                this.socket.close();
            } catch (IOException ex) {
                socketList.remove(this);
            }
            count--;
        }
    }
}
