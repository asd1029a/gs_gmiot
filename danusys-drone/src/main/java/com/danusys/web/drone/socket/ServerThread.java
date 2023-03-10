package com.danusys.web.drone.socket;

import com.danusys.web.drone.model.DroneSocket;
import com.danusys.web.drone.service.DroneSocketService;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavAutopilot;
import io.dronefleet.mavlink.common.MavState;
import io.dronefleet.mavlink.common.MavType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@RequiredArgsConstructor
@Slf4j
public class ServerThread extends Thread {

    DroneSocketService droneSocketService;
    ServerSocket serverSocket;
    HashMap<Integer, Socket> socketList = new HashMap<>();
    Map<Integer,Object> systemIdConnectionMap =new HashMap<>();

    int count = 1;
    boolean flag = true;

    public ServerThread(ServerSocket serverSocket, DroneSocketService droneSocketService) {
        this.serverSocket = serverSocket;
        this.droneSocketService =droneSocketService;
    }

    public HashMap<Integer, Socket> getSocketList() {
        return socketList;
    }

    public Map<Integer,Object> getSystemIdConnectionMap(){
        return systemIdConnectionMap;
    }

    public boolean destroySocket(int index) {
        if (index != -1) {
            System.out.println("deleteSocketindex : "+ (index));
            Socket socket=socketList.get(index);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socketList.remove(index);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void run() {
        Socket socket = null;
        List<Integer> deleteList = new ArrayList<>();
        while (flag) {
            try {

                int systemId =0;
                socket = serverSocket.accept();
                System.out.println("Thread " + count + "connected");
                //ClientThread testThread = new ClientThread(socket, count);
                socketList.put(count, socket);
                log.info("socketList={}",socketList);
                MavlinkConnection connection = this.connect(socket,count);
                if(connection != null) {
                    if ((systemId = isConnected(connection)) != -1) {
                        log.info("syststemId={}", systemId);
                        saveMap(systemId, count, socketList);
                    } else {
                        connection = null;
                        log.info("deleteList={}", count);
                        deleteList.add(count);
                    }
                    for (Integer deleteIndex : deleteList) {
                        this.destroySocket(deleteIndex);
                    }
                }

            } catch (IOException | InterruptedException e) {
                System.out.println("???????????? ????????????");
                if (!socket.isClosed()) {
                    try {
                        System.out.println("?????? ??????");
                        socket.close();
                        socketList.remove(count);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            socketList.remove(count);
            count++;
        }
    }

    public MavlinkConnection connect(Socket socket,int count) throws InterruptedException {

        MavlinkConnection connection = null;

        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            Thread.sleep(2000);
            boolean ready = reader.ready();
            log.info("###reader.ready() : {}", ready);

            if (ready) {
                connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());

            } else {
                this.destroySocket(count);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }
    public int isConnected(MavlinkConnection connection) {

        int linkId = 1;
        int systemId = 0;
        long timestamp = System.currentTimeMillis();/* provide microsecond time */
        MavlinkMessage message;
        byte[] secretKey = new byte[0];
        final int[] timeSec = {0};
        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                timeSec[0]++;
            }
        };
        try {
            secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));
            Heartbeat firstHeartbeat = Heartbeat.builder()
                    .autopilot(MavAutopilot.MAV_AUTOPILOT_GENERIC)
                    .type(MavType.MAV_TYPE_GENERIC)
                    .systemStatus(MavState.MAV_STATE_UNINIT)
                    .baseMode()
                    .mavlinkVersion(3)
                    .build();


            connection.send2(1, 1, firstHeartbeat, linkId, timestamp, secretKey);
            t.schedule(tt, 0, 1000);

            log.info("??????????1");
            while (timeSec[0] < 3) {
                if((message = connection.next()) != null) {
                    if (timeSec[0] >= 3) {
                        break;
                    }
                    if (message.getPayload() instanceof Heartbeat) {
                        systemId = message.getOriginSystemId();
                        break;
                    }
                }
//                while ((message = connection.next()) != null) {
//                    //TODO socket try catch ???????????? ????????? ??????
////                    if (flightManager.getConnectionMap().getOrDefault(connection, -1) == 2) {
////
////                        break;
////                    }
//                }
            }

            log.info("??????????3");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            log.info("EOFException");
            return -1;
        } catch (IOException e) {
            log.info("IOEEXCEPTION");
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tt.cancel();
            t.cancel();
            timeSec[0] = 0;

        }

        return systemId;


    }
    public void saveMap(int systemId, int index, Map<Integer, Socket> socketList) {
        Map<String, Object> map = new HashMap<>();

        map.put("index", index);
        map.put("socket",socketList.get(index));
        log.info("map={}", map);
        systemIdConnectionMap.put(systemId, map);
        log.info("systemIdConnectionMap={}",systemIdConnectionMap);
        DroneSocket droneSocket = new DroneSocket();
        droneSocket.setIndex(Long.valueOf(index));
        droneSocket.setPort(Integer.toString(socketList.get(index).getPort()));
        droneSocket.setIp(socketList.get(index).getInetAddress().toString());
        droneSocket.setLocalport(Integer.toString(socketList.get(index).getLocalPort()));
        droneSocket.setSystemId(systemId);
        droneSocketService.saveList(droneSocket);


    }

}
