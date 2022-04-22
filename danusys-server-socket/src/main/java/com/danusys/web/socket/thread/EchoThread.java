package com.danusys.web.socket.thread;


import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class EchoThread extends Thread{
    private Socket sock;
    //BufferedOutputStream bout;
    //private ObjectOutputStream oout;
    private BufferedInputStream bis;
    private List<EchoThread> threadList;
    private PrintWriter pw;

    public EchoThread(Socket sock, List<EchoThread> threadList){
        this.threadList = threadList;
        this.sock = sock;

    } // 생성자

    public void sendList(String o) throws IOException {
        //Gson gson = new Gson();
        //JSONObject jo = gson.fromJson(o, JSONObject.class);
        //List<Map<String,Object>> list = (List<Map<String, Object>>) jo.get("result");
        for(int i=0;i<threadList.size();i++){
            //threadList.get(i).oout.writeObject(o);
            //threadList.get(i).oout.flush();
            threadList.get(i).pw.println(o);
            threadList.get(i).pw.flush();
            log.info("threadList size = {}",threadList.size());
        }
    }
    public void run(){
        try{
            OutputStream out = sock.getOutputStream();
            //pw = new PrintWriter(new OutputStreamWriter(out,"utf-8"));
            //oout = new ObjectOutputStream(out);
            //bout = new BufferedOutputStream(out);
            InputStream in = sock.getInputStream();
            bis = new BufferedInputStream(in);

          /*  URL url = new URL("http://localhost:8081/test");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setDoOutput(true);
            pw = new PrintWriter(new OutputStreamWriter(conn.getOutputStream()));*/
            //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
            WebClient webClient = WebClient.create("http://localhost:8080");



            int offset;

            byte[] ba = new byte[52];

            while ((offset = bis.read(ba)) != -1) {
                StringBuilder sb = new StringBuilder();
                Map<String,Object> maps = new HashMap<>();
                List<Map<String,Object>> result =  new ArrayList<>();
                for (byte a : ba) {
                    int tempHex = (int) a & 0xff;
                    String hex = Integer.toHexString(tempHex);
                    //int decimal = Integer.parseInt(hex,16);
                    //log.info("decimal = >>>{}",decimal); 처음과 끝을 확인하자
                    if (hex.length() % 2 == 1) {
                        hex = "0" + hex;
                    }
                    //sb.append(hex).append("\t");
                    sb.append(hex);
                }
                String sbString = sb.toString();
                String first = sbString.substring(0, 2);
                String last = sbString.substring(100, 102);
                if(first.equals("02") && last.equals("03")){
                    int findTypeidx = sbString.indexOf("21");
                    //지역번호,시설물번호
                    int group = Integer.parseInt(sbString.substring(findTypeidx - 2, findTypeidx),16);
                    int faSeq = Integer.parseInt(sbString.substring(findTypeidx + 2, findTypeidx + 4),16);
                    int idx = sbString.indexOf("dd");
                    int endidx = sbString.indexOf("0303");
                    String sbStringData = sbString.substring(idx + 2, endidx - 4);
                    //log.info("sbStringData = {}",sbStringData);
                    List<String> StringList = new ArrayList<>();
                    List<String> String2List = new ArrayList<>();
                    for(int i=0;i<sbStringData.length();i+=2){
                        StringList.add(sbStringData.substring(i,i+2));
                    }
                    String2List.add(0,StringList.get(0)+StringList.get(1));
                    String2List.add(1,StringList.get(2));
                    String2List.add(2,StringList.get(3));
                    String2List.add(3,StringList.get(4));
                    String2List.add(4,StringList.get(5));
                    for(int i=6;i<StringList.size();i+=2){
                        String2List.add(StringList.get(i)+StringList.get(i+1));
                    }
                    //log.info("String2List = {}",String2List);
                    //int[] decimalData = StringList.stream().mapToInt(value -> Integer.parseInt(value, 16)).toArray();
                    double[] ArrayData = String2List.stream().mapToDouble(value -> Integer.parseInt(value, 16)).toArray();
                    //log.info("decimalData = {}",decimalData);
                    //log.info("ArrayData = {}",ArrayData);
                    //group_sequence
                    maps.put("count",(int) ArrayData[0]);
                    maps.put("temperature",ArrayData[1]+(ArrayData[2]/10.0)+"℃");
                    maps.put("humidity",ArrayData[3]+(ArrayData[4]/10.0)+"%");
                    maps.put("o3",ArrayData[5]+"ppb");
                    maps.put("co",ArrayData[6]+"ppm");
                    maps.put("no2",ArrayData[7]+"ppb");
                    maps.put("pm2_5",ArrayData[8]+"㎍/㎥");
                    maps.put("pm10",ArrayData[9]+"㎍/㎥");
                    maps.put("pm2_5t",ArrayData[10]+"㎍/㎥");
                    maps.put("pm10t",ArrayData[11]+"㎍/㎥");
                    maps.put("uvi",ArrayData[12]+"uvi");
                    maps.put("noiseAvg",ArrayData[13]);
                    maps.put("noiseMax",ArrayData[14]);
                    maps.put("windSpAvg",ArrayData[15]);
                    maps.put("windDiAvg",ArrayData[16]);
                    //log.info("maps = {}",maps);
                    for(String key : maps.keySet()){
                        Map<String,Object> m = new HashMap<>();
                        m.put("areaCode",group);
                        m.put("facilitySeq",faSeq);
                        m.put(key,maps.get(key));
                        result.add(m);
                    }

                    JSONObject jo = new JSONObject();
                    jo.put("result",result);
                    webClient.post().body(BodyInserters.fromFormData("params",new Gson().toJson(jo))).exchange();


                    //log.info("hex string : >>> {}", sb.toString());
                    //log.info("result = {}",result);
                    //oout.writeObject(result);
                    log.info("클라이언트로 부터 받은 json {}",new Gson().toJson(jo));
                    //bw.write(new Gson().toJson(jo));
                    //bw.flush();
                    //pw.println(new Gson().toJson(jo));
                    //pw.flush();
                    //sendList(new Gson().toJson(jo));
                    //bout.flush();
                }else{
                    log.info("데이터 오류");
                    break;
                }
            }
        }catch(StringIndexOutOfBoundsException e){
            log.info("데이터 오류");
        }catch(SocketException e){
            log.info("클라이언트 접속 종료");
        }catch(IOException e){
            log.info("서버 에러");
        }finally {
            try{
                if(pw != null){pw.close();}
                if(bis != null){bis.close();}
                if(sock != null && sock.isClosed() == false){sock.close();}
            }catch (IOException e){
                e.printStackTrace();
            }
            threadList.remove(this);
        }
    } // run
}
