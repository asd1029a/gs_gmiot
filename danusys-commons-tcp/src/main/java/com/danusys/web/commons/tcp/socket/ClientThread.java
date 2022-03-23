package com.danusys.web.commons.tcp.socket;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/03/21
 * Time : 6:55 PM
 */
@RequiredArgsConstructor
public class ClientThread extends Thread {
    public Socket socket;
    public int id;

    protected ClientThread(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
    }
    public Socket getSocket(){
        return this.socket;
    }

    @Override
    public void run () {
        try {
            while (true) {
                InputStream IS = socket.getInputStream();
                byte[] bt = new byte[256];
                int size = IS.read(bt);

                String output = new String(bt, 0, size, "UTF-8");

                //System.out.println("Thread " + id + " >  " + output);

            }
        } catch (IOException e) {
        } finally {
            try {
                System.out.println("    Thread " + id + " is closed. ");
                socket.close();
            } catch (IOException e) {

            }
        }
    }
}