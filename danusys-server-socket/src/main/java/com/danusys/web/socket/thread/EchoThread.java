package com.danusys.web.socket.thread;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class EchoThread extends Thread{
    private Socket sock;
    private BufferedInputStream bis;

    public EchoThread(Socket sock){
        this.sock = sock;

    }

    public void run(){
        try{
            InputStream in = sock.getInputStream();
            bis = new BufferedInputStream(in);

            int offset;

            byte[] ba = new byte[52];

            while ((offset = bis.read(ba)) != -1) {
                StringBuilder sb = new StringBuilder();
                Map<String,Object> maps = new HashMap<>();
                List<Map<String,Object>> result =  new ArrayList<>();
                Map<String,Object> resultMap = new HashMap<>();
                for (byte a : ba) {
                    int tempHex = (int) a & 0xff;
                    String hex = Integer.toHexString(tempHex);
                    if (hex.length() % 2 == 1) {
                        hex = "0" + hex;
                    }
                    sb.append(hex);
                }
                String sbString = sb.toString();
                String first = sbString.substring(0, 2);
                String last = sbString.substring(100, 102);
                if(first.equals("02") && last.equals("03")){
                    //시설물번호
                    int faSeq = Integer.parseInt(sbString.substring(28,30));

                    int idx = sbString.indexOf("dd");
                    int endidx = sbString.indexOf("0303");
                    String sbStringData = sbString.substring(idx + 2, endidx - 4);
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
                    double[] ArrayData = String2List.stream().mapToDouble(value -> Integer.parseInt(value, 16)).toArray();
                    maps.put("count",(int) ArrayData[0]);
                    maps.put("temperature",ArrayData[1]+(ArrayData[2]/10.0));
                    maps.put("humidity",ArrayData[3]+(ArrayData[4]/10.0));
                    maps.put("O3",ArrayData[5]);
                    maps.put("CO",ArrayData[6]);
                    maps.put("NO2",ArrayData[7]);
                    maps.put("PM2.5",ArrayData[8]);
                    maps.put("PM10",ArrayData[9]);
                    maps.put("PM2_5t",ArrayData[10]);
                    maps.put("PM10t",ArrayData[11]);
                    maps.put("UVI",ArrayData[12]);
                    maps.put("noiseAvg",ArrayData[13]);
                    maps.put("noiseMax",ArrayData[14]);
                    maps.put("windSpAvg",ArrayData[15]);
                    maps.put("windDiAvg",ArrayData[16]);
                    for(String key : maps.keySet()){
                        Map<String,Object> m = new HashMap<>();
                        m.put("deviceId","BSNG_S_"+faSeq);
                        m.put("name",key);
                        m.put("value",maps.get(key));
                        m.put("type",50);
                        result.add(m);
                    }
                    resultMap.put("callUrl","/facility/facilityData");
                    resultMap.put("dataList",result);

                    //json 파일 변환
                    ObjectMapper om = new ObjectMapper();
                    String jsonString = om.writeValueAsString(resultMap);

                    //내부서버로 전송
                    WebClient webClient = WebClient.create("http://10.8.50.52:8400/api/facilityData");
                    webClient.post().contentType(MediaType.APPLICATION_JSON).bodyValue(jsonString).retrieve().bodyToMono(String.class).subscribe();
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
                if(bis != null){bis.close();}
                if(sock != null && sock.isClosed() == false){sock.close();}
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    } // run
}
