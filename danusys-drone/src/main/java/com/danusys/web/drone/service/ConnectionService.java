package com.danusys.web.drone.service;

import com.danusys.web.commons.socket.config.CustomServerSocket;
import com.danusys.web.drone.model.DroneSocket;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkDialect;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavAutopilot;
import io.dronefleet.mavlink.common.MavState;
import io.dronefleet.mavlink.common.MavType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class ConnectionService {

    private final DroneSocketService droneSocketService;
    private final CustomServerSocket ServerSocket;
    Map<String, Object> connectionMap = new HashMap<>();
    int currentIndex = -1;

    public List<Map<String, Object>> getSocketList() {
        List<Map <String, Object>> listMap = new ArrayList<>();
        Map<Integer, Socket> socketList = ServerSocket.serverThread.getSocketList();

        MavlinkConnection connection = null;
        List<Integer> deleteList = new ArrayList<>();
        log.info("socketList={}", socketList);
        if (socketList.isEmpty() || socketList == null)
            return null;


        for (Integer index : socketList.keySet()) {
            log.info("key={}", index);
            Socket socket = socketList.get(index);
            currentIndex = index;

            connection = connect(socket);


            log.info("connection={}", connection);

            if ( isConnected(connection)) {
                log.info("addMap={}", index);
                saveMap(index,socketList);
              //  addMap(index, socketList, listMap);
//                if (!connectionMap.containsValue(connection)) {
//                    log.info("여기왜와요???");
//                    connectionMap.put("connection" + index, connection);
//                }

            } else {
                log.info("deleteList={}", index);
                deleteList.add(index);
            }


        }
        for (Integer deleteIndex : deleteList) {
            ServerSocket.serverThread.destroySocket(deleteIndex);
        }

        return listMap;
    }

    public MavlinkConnection connect(Socket socket) {

        MavlinkConnection connection = null;

        try {
            connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public boolean isConnected(MavlinkConnection connection) {

        int linkId = 1;
        long timestamp = System.currentTimeMillis();/* provide microsecond time */
        MavlinkMessage message;
        byte[] secretKey = new byte[0];
        final int[] timeSec = {0};
        Timer t=new Timer();
        TimerTask tt=new TimerTask() {
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
            t.schedule(tt,0,1000);
            while ((message = connection.next()) != null) {
                //TODO socket try catch 안쪽으로 별개로 작성
                if (message.getPayload() instanceof Heartbeat) {
                    //  MavlinkMessage<Heartbeat> heartbeatMavlinkMessage = (MavlinkMessage<Heartbeat>) message;
                    break;
                }
                if(timeSec[0]>=2) {
                    return true;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            log.info("IOEEXCEPTION");
            return false;
        } catch(Exception e){
            e.printStackTrace();
        }
        finally {
            tt.cancel();
            t.cancel();
            timeSec[0]=0;
        }

        return true;


    }

//    public void addMap(int index, Map<Integer, Socket> socketList, List<Map<String, Object>> listMap) {
//        Map<String, Object> map = new HashMap<>();
//
//        map.put("index", index);
//        map.put("port", socketList.get(index).getPort());
//        map.put("address", socketList.get(index).getInetAddress());
//        map.put("localPort", socketList.get(index).getLocalPort());
//        log.info("map={}", map);
//        listMap.add(map);
//
//
//    }


    public void saveMap(int index, Map<Integer, Socket> socketList) {
        Map<String, Object> map = new HashMap<>();

        map.put("index", index);
        map.put("port", socketList.get(index).getPort());
        map.put("address", socketList.get(index).getInetAddress());
        map.put("localPort", socketList.get(index).getLocalPort());
        log.info("map={}", map);
        DroneSocket droneSocket = new DroneSocket();
        droneSocket.setIndex(index);
        droneSocket.setPort(Integer.toString(socketList.get(index).getPort()));
        droneSocket.setIp(socketList.get(index).getInetAddress().toString());
        droneSocket.setLocalport(Integer.toString(socketList.get(index).getLocalPort()));
        droneSocketService.saveList(droneSocket);


    }

    public MavlinkConnection getMavlinkConnection(int index) {

        MavlinkConnection connection = (MavlinkConnection) connectionMap.get("connection" + index);
        return connection;
    }

    public Map<String, Object> getConnectionMap() {
        return connectionMap;
    }
}
