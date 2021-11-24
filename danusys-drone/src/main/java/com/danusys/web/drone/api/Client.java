package com.danusys.web.drone.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/20
 * Time : 1:25 오전
 */


public class Client {
    public static void main(String[] args) {
//        if (args.length < 2) {
//            System.out.println("#######Need More Arguments #########");
//            return;
//        }
//        String hostname = args[0];
//        int port = Integer.parseInt(args[1]);
        String hostname = "127.0.0.1";
        int port = 50001;
        for (int i = 0; i < 1; i++) {
            try (Socket socket = new Socket(hostname, port)) {
                OutputStream out = socket.getOutputStream();
                String realStr = "This is woolbro dev Test sent";
                out.write(realStr.getBytes());

                InputStream input = socket.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String time = reader.readLine();

                System.out.println(time);

            } catch (UnknownHostException ex) {
                System.out.println("Server not found: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }
        }
    }
}
