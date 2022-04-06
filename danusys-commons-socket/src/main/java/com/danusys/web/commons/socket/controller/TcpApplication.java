package com.danusys.web.commons.socket.controller;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpApplication {
    static ServerSocket server;
    public static void main(String[] args) throws IOException {
        try{
            server = new ServerSocket(8500);
            System.out.println("접속을 기다립니다.");
            while(true){
                Socket sock = server.accept();
                EchoThread echothread = new EchoThread(sock);
                InetAddress inetaddr = sock.getInetAddress();
                System.out.println(inetaddr.getHostAddress() + " 로 부터 접속하였습니다.");
                echothread.start();
            } // while
        }catch(Exception e){
            System.out.println(e);
        }finally {
            System.out.println("서버를 종료합니다");
            server.close();
        }
    } // main
}

class EchoThread extends Thread{
    Socket sock;
    BufferedReader br;
    PrintWriter pw;
    public EchoThread(Socket sock){
        this.sock = sock;
    } // 생성자
    public void run(){
        try{
            OutputStream out = sock.getOutputStream();
            pw = new PrintWriter(new OutputStreamWriter(out));

            InputStream in = sock.getInputStream();
            br = new BufferedReader(new InputStreamReader(in));
            //byte[] bt = new byte[256];
            //new String(bt, 0, in.read(), "UTF-8");
            //int num = Integer.parseInt(br.readLine());
            String line = null;
            while((line = br.readLine()) != null){
                System.out.println("클라이언트로 부터 전송받은 문자열 : " + line);
                pw.println(line);
                pw.flush();
                if(line.equals("exit")){
                    break;
                }
            }

            //pw.close();
            //br.close();
            //sock.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            System.out.println("클라이언트가 나갔습니다.");
            try{
                if(pw != null){pw.close();}
                if(br != null){br.close();}
                if(sock != null){sock.close();}
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    } // run
}
