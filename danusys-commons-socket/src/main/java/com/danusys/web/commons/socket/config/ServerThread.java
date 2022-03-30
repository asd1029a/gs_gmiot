package com.danusys.web.commons.socket.config;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/03/21
 * Time : 6:55 PM
 */

public class ServerThread extends Thread {
    ServerSocket serverSocket;
    HashMap<Integer, Socket> socketList = new HashMap<>();
    int count = 1;
    boolean flag = true;

    public ServerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public HashMap<Integer, Socket> getSocketList() {
        return socketList;
    }

    public boolean destroySocket(int index) {
        if (index != -1) {
            System.out.println("index : "+ (index));
            socketList.remove(index);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void run() {
        Socket socket = null;
        while (flag) {
            try {

                socket = serverSocket.accept();
                //OutputStream outputStream = socket.getOutputStream();
                //outputStream.write("hello client \n".getBytes());
                System.out.println("Thread " + count + "connected");
                //ClientThread testThread = new ClientThread(socket, count);
                //socketList.put(count,testThread.getSocket());
                socketList.put(count, socket);
                Integer currentIndex = count;

//
//                AtomicReference<Integer> a = new AtomicReference<>(currentIndex);
//                AtomicReference<Socket> b = new AtomicReference<>(socket);
//                new Thread(() -> {
//                    try {
//                        while (b.get().getInputStream()!=null) {
//                        InputStream inputStream=b.get().getInputStream();
//                            //   System.out.println("Thread " + inputStream + " >  " + output);
//                            System.out.println("please : "+b.get().getInputStream());
//                            if(b.get().getInputStream()==null){
//                                System.out.println("count : "+count);
//                                destroySocket(count);
//                            }
//                        }
//                        if(b.get().getInputStream()==null){
//                            System.out.println("count : "+count);
//                            destroySocket(count);
//                        }
//                    } catch (StringIndexOutOfBoundsException | IOException e) {
//                        System.out.println("count : " + count);
//                        destroySocket(count);
//                    }
//                }).start();

            } catch (IOException e) {
                System.out.println("통신소켓 생성불가");
                if (!socket.isClosed()) {
                    try {
                        System.out.println("소켓 삭제");
                        socket.close();
                        socketList.remove(count);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            count++;
        }
    }
}
