package com.danusys.tcp.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class testsocket {
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

    static String getTime ()
    {
        SimpleDateFormat f = new SimpleDateFormat("[hh : mm : ss ]");
        return f.format(new Date());
    }
}

class ClientThread extends Thread {
    Socket socket;
    int id;

    ClientThread (Socket socket, int id) {
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void run ()
    {
        try
        {
            while (true)
            {
                InputStream IS = socket.getInputStream();
                byte[] bt = new byte[256];
                int size = IS.read(bt);

                String output = new String(bt, 0, size, "UTF-8");
                System.out.println("Thread " + id + " >  " + output);
            }
        } catch (IOException e)
        {
        }finally {
            try {
                System.out.println("    Thread " + id + " is closed. ");
                socket.close();
            } catch (IOException e) {

            }
        }
    }
}

class ServerThread extends Thread
{
    ServerSocket serverSocket;
    int count = 1;

    ServerThread (ServerSocket serverSocket)
    {
        System.out.println(testsocket.getTime() + " Server opened");
        this.serverSocket = serverSocket;
    }

    @Override
    public void run ()
    {
        try
        {
            while (true)
            {
                Socket socket = serverSocket.accept();
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write("hello client \n".getBytes());
                System.out.println("    Thread :" + count);
                ClientThread clientThread = new ClientThread(socket, count);
                clientThread.start();
                count++;
                    socket.close();
            }
        } catch (Exception e)
        {
            System.out.println("    SERVER CLOSE    ");
            count--;
        }
    }
}



