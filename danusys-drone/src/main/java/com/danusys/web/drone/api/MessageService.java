package com.danusys.web.drone.api;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavFrame;
import io.dronefleet.mavlink.common.MissionItem;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/13
 * Time : 4:36 오전
 */
public class MessageService {
    private final static Logger logger = Logger.getGlobal();


    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        //=============================================
        // 기본 로그 제거
        //------------
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        Handler handler = new FileHandler("/Users/kai/dev/log/message.log", false);

        CustomLogFormatter formatter = new CustomLogFormatter();
        handler.setFormatter(formatter);
        logger.addHandler(handler);

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

            connection.send2(systemId, componentId,  new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_DO_SET_MODE)
                    .param1(1)
                    .param2(4)
                    .build(), linkId, timestamp, secretKey);


            Thread.sleep(1000);

            connection.send2(systemId, componentId, new CommandLong.Builder().
                    command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM)
                    .param1(1)
                    .param2(0)
                    .build(), linkId, timestamp, secretKey);

            Thread.sleep(2000);
            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_NAV_TAKEOFF)
                    .param1(15)
                    .param2(0)
                    .param3(0)
                    .param4(0)
                    .param5(0)
                    .param6(0)
                    .param7(10)
                    .build(), linkId, timestamp, secretKey);

            Thread.sleep(5000);

//            connection.send2(systemId, componentId, new CommandLong.Builder().
//                    command(MavCmd.MAV_CMD_DO_CHANGE_SPEED)
//                    .param1(0)
//                    .param2(10)
//                    .param3(-1)
//                    .param4(0)
//                    .build(), linkId, timestamp, secretKey);
            connection.send2(systemId, componentId, new MissionItem.Builder()
                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                    .targetSystem(0)
                    .targetComponent(0)
                    .seq(0)
                    .current(2)
                    .autocontinue(0)
                    .frame(MavFrame.MAV_FRAME_GLOBAL_RELATIVE_ALT)
                    .x(-35.364321f)
                    .y(149.170503f)
                    .z(10)
                    .build(), linkId, timestamp, secretKey);




            Thread.sleep(15000);
            connection.send2(systemId, componentId, new MissionItem.Builder()
                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                    .targetSystem(0)
                    .targetComponent(0)
                    .seq(0)
                    .current(2)
                    .autocontinue(0)
                    .frame(MavFrame.MAV_FRAME_GLOBAL_RELATIVE_ALT)
                    .x(-35.3645121f)
                    .y(149.1701370f)
                    .z(10)
                    .build(), linkId, timestamp, secretKey);



            Thread.sleep(15000);
            connection.send2(systemId, componentId, new MissionItem.Builder()
                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                    .targetSystem(0)
                    .targetComponent(0)
                    .seq(0)
                    .current(2)
                    .autocontinue(0)
                    .frame(MavFrame.MAV_FRAME_GLOBAL_RELATIVE_ALT)
                    .x(-35.364321f)
                    .y(149.170503f)
                    .z(10)
                    .build(), linkId, timestamp, secretKey);


//            https://map.kakao.com/link/map/37.4456046,126.8953084
//            MavCmd.MAV_CMD_NAV_WAYPOINT


            //MissionCurrent|MissionItemInt
            //PositionTargetGlobalInt
            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|HomePosition|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";
            MavlinkMessage message;
            while ((message = connection.next()) != null) {
                Object p = message.getPayload();
                if(!objectNames.contains(p.getClass().getSimpleName())) {
                    logger.info("#" + message.getSequence() + " --> " + p);
                }
            }

        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            System.out.println("전송됨");
        }
    }
}
