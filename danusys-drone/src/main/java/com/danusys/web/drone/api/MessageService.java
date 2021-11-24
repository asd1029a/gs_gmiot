package com.danusys.web.drone.api;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.CommandAck;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.Heartbeat;
import io.dronefleet.mavlink.common.MavCmd;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/13
 * Time : 4:36 오전
 */
public class MessageService {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        try (Socket socket = new Socket("172.20.14.84", 14550)) {
            MavlinkConnection connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));

            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_DO_SET_MODE).param1(1).param2(4).build(), linkId, timestamp, secretKey);
            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM).param1(1).param2(0).build(), linkId, timestamp, secretKey);
            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_NAV_TAKEOFF).param1(15).param2(0).param3(0).param4(0).param5(0).param6(0).param7(10).build(), linkId, timestamp, secretKey);

            MavlinkMessage message;
            while ((message = connection.next()) != null) {
                Object p = message.getPayload();
                if (p instanceof Heartbeat || p instanceof CommandAck)
                    System.out.println("" + message.getSequence() + " --> " + p);
            }

        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
//            System.exit(0);
            System.out.println("전송됨");
        }
    }
}
