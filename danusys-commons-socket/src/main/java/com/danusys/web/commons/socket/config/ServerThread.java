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
    ServerSocket serverSocket;
    HashMap<Integer,Socket> socketList = new HashMap<>();
    public Socket socket = null;
    int count = 1;

    public ServerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public HashMap<Integer,Socket> getSocketList(){
        return socketList;
    }

    @Override
    public void run () {
        try {
            while (true) {
                socket = serverSocket.accept();
                //OutputStream outputStream = socket.getOutputStream();
                //outputStream.write("hello client \n".getBytes());
                System.out.println("Thread " + count + "connected");
                //ClientThread testThread = new ClientThread(socket, count);
                //socketList.put(count,testThread.getSocket());
                socketList.put(count,socket);
                count++;
            }
        } catch(IOException e) {

            System.out.println("통신소켓 생성불가");
            if(!socket.isClosed()) {
                try {
                    System.out.println("소켓 삭제");
                    socketList.remove(this);
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
