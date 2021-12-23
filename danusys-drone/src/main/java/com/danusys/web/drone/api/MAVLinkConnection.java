package com.danusys.web.drone.api;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.MissionItemInt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

@Slf4j
@Service
public class MAVLinkConnection {

    private MavlinkConnection connection;
//    static MavlinkMessage message;
//    Socket socket;
//    Handler mHandler;
//    static DroneMessage droneMessage = new DroneMessage();

//    static boolean isAskingFeedback = false;

//    public MAVLinkConnection() {
//    }

    public void createConnection() {
//        new Thread(() -> {
//            int connect_num = connect_type.getSelectedItemPosition();
            if (connection == null) {
                try (Socket socket = new Socket("172.20.14.84", 14550)){
                    connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
                    log.info("is Connecting...");
//                    while ((message = connection.next()) != null) {
//                        droneMessage.MessageClassify(message);
//                        if(!isAskingFeedback){
//                            log.info("Connected ...");
//                            DroneCommand.STATUS();
//                            isAskingFeedback = true;
//                        }
//                    }
                } catch (EOFException eof) {
//                    releaseConnection();
                    log.error("MAVLinkConnection", "Connection Failed (SITL crush)");
                } catch (UnknownHostException e) {
//                    releaseConnection();
                    log.error("MAVLinkConnection", "Connection Failed");
                } catch (IOException e) {
//                    releaseConnection();
                    log.error("MAVLinkConnection", "Connection Failed (No WIFI or APP crush)");
                }
            }
//        }).start();
    }

    public void Send(CommandLong commandLong) throws IOException {
//        new Thread(() -> {
//        if(connection != null) {
      //  try (Socket socket = new Socket("172.20.14.84", 14550)) {
         try (Socket socket = new Socket("172.20.14.87", 14550)) {

            connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
            connection.send1(255, 0, commandLong);
            log.info(commandLong.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        }
//        }).start();
    }

    public void Send(MissionItemInt missionItemInt) {
       // try (Socket socket = new Socket("172.20.14.84", 14550)) {
            try (Socket socket = new Socket("172.20.14.87", 14550)) {
            connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
            connection.send1(1, 1, missionItemInt);
            log.info(missionItemInt.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void releaseConnection() {
//        try {
//            if (socket != null) {
//                if (socket.getOutputStream() != null) {
//                    socket.getOutputStream().close();
//                }
//                socket.close();
//            }
//            connection = null;
//            log.info("Disconnected...");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}