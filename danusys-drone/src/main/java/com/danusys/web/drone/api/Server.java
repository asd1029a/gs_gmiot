package com.danusys.web.drone.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/20
 * Time : 1:22 오전
 */

public class Server {
    public static void main(String[] args) {
//        if (args.length < 1) {
//            System.out.println("######## Argument is Null ########");
//            return;
//        }
//        int port = Integer.parseInt(args[0]);
        int port = 50001;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[ " + socket.getInetAddress() + " ] client connected");

                OutputStream output = socket.getOutputStream();

                PrintWriter writer = new PrintWriter(output, true);

                writer.println(new Date().toString());

                InputStream input = socket.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                System.out.println("###### msg rcv : " + reader.readLine());
            }
        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
