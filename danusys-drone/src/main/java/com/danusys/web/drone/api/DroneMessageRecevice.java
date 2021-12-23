package com.danusys.web.drone.api;

import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/23
 * Time : 10:12 오전
 */
public class DroneMessageRecevice {
    public static void main(String[] args) {
        //try (Socket socket = new Socket("172.20.14.84", 14550)) {
            try (Socket socket = new Socket("172.20.14.87", 14550)) {
            MavlinkConnection connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

//
//            CommandAck commandAck = CommandAck.builder()
//                    .command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM)
//                    .result(MavResult.MAV_RESULT_ACCEPTED)
//                    .progress(0)
//                    .resultParam2(0)
//                    .targetSystem(255)
//                    .targetComponent(190)
//                    .build();
//
//            int systemId = 1;
//            int componentId = 1;
//            int linkId = 1;
//            long timestamp = System.currentTimeMillis();/* provide microsecond time */;
//            byte[] secretKey = MessageDigest.getInstance("SHA-256")
//                    .digest("danusys".getBytes(StandardCharsets.UTF_8));
////            connection.send2(systemId, componentId, commandAck, linkId, timestamp, secretKey);
//
//            GpsGlobalOrigin gpsGlobalOrigin = GpsGlobalOrigin.builder()
//                    .latitude(-353643232)
//                    .longitude(1491705072)
//                    .altitude(577850)
//                    .build();
//            connection.send2(systemId, componentId, gpsGlobalOrigin, linkId, timestamp, secretKey);
//
//            HomePosition homePosition = HomePosition.builder()
//                    .latitude(-353643232)
//                    .longitude(1491705072)
//                    .altitude(577850)
//                    .build();
//            connection.send2(systemId, componentId, homePosition, linkId, timestamp, secretKey);


            MavlinkMessage message;
            while ((message = connection.next()) != null) {
                if (message instanceof Mavlink2Message) {
                     Mavlink2Message message2 = (Mavlink2Message)message;

                    if(message2.getPayload().getClass().getName().contains("GlobalPositionInt") ||
                            message2.getPayload().getClass().getName().contains("SysStatus") ||
                            message2.getPayload().getClass().getName().contains("PowerStatus") ||
                            message2.getPayload().getClass().getName().contains("NavControllerOutput") ||
                            message2.getPayload().getClass().getName().contains("MissionCurrent") ||
                            message2.getPayload().getClass().getName().contains("ServoOutputRaw") ||
                            message2.getPayload().getClass().getName().contains("RcChannels") ||
                            message2.getPayload().getClass().getName().contains("RawImu") ||
                            message2.getPayload().getClass().getName().contains("ScaledImu2") ||
                            message2.getPayload().getClass().getName().contains("ScaledImu3") ||
                            message2.getPayload().getClass().getName().contains("ScaledPressure") ||
                            message2.getPayload().getClass().getName().contains("ScaledPressure2") ||
                            message2.getPayload().getClass().getName().contains("GpsRawInt") ||
                            message2.getPayload().getClass().getName().contains("SystemTime") ||
                            message2.getPayload().getClass().getName().contains("TerrainReport") ||
                            message2.getPayload().getClass().getName().contains("LocalPositionNed") ||
                            message2.getPayload().getClass().getName().contains("Vibration") ||
                            message2.getPayload().getClass().getName().contains("BatteryStatus") ||
                            message2.getPayload().getClass().getName().contains("Attitude") ||
                            message2.getPayload().getClass().getName().contains("VfrHud") ||
                            message2.getPayload().getClass().getName().contains("Heartbeat") ||
                            message2.getPayload().getClass().getName().contains("Meminfo") ||
                            message2.getPayload().getClass().getName().contains("Ahrs") ||
                            message2.getPayload().getClass().getName().contains("Hwstatus") ||
                            message2.getPayload().getClass().getName().contains("MountStatus") ||
                            message2.getPayload().getClass().getName().contains("EkfStatusReport") ||
                            message2.getPayload().getClass().getName().contains("Simstate") ||
                            message2.getPayload().getClass().getName().contains("Ahrs2") ||
                            message2.getPayload().getClass().getName().contains("Timesync") ||
                            message2.getPayload().getClass().getName().contains("ParamValue")) {
                    } else {
                        System.out.println(message2.getPayload().getClass().getName() + " > " + message2 );
                        System.out.println("==========================================================================================");
                    }

                    if (message2.isSigned()) {

                    } else {

                    }
                } else {
                    // This is a Mavlink1 message.
                }

//                if (message.getPayload() instanceof Heartbeat) {
//                    MavlinkMessage<Heartbeat> heartbeatMessage = (MavlinkMessage<Heartbeat>)message;
//                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
