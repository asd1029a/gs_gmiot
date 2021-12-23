package com.danusys.web.drone.api;

import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;

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
public class MessageService2 {
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

        Handler handler = new FileHandler("/Users/kai/dev/log/message2.log", false);

        CustomLogFormatter formatter = new CustomLogFormatter();
        handler.setFormatter(formatter);
        logger.addHandler(handler);

        //try (Socket socket = new Socket("172.20.14.84", 14550)) {
            try (Socket socket = new Socket("172.20.14.87", 14550)) {
            MavlinkConnection connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));


//            connection.send2(systemId, componentId, new CommandLong.Builder().
//                    command(MavCmd.MAV_CMD_DO_CHANGE_SPEED)
//                    .param1(0)
//                    .param2(10)
//                    .param3(-1)
//                    .param4(0)
//                    .build(), linkId, timestamp, secretKey);

//            connection.send2(systemId, componentId, new MissionItem.Builder()
//                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
//                    .targetSystem(0)
//                    .targetComponent(0)
//                    .seq(0)
//                    .current(2) //2일때 해당 포지션으로 바로 비행이 됨.
//                    .autocontinue(0)
//                    .frame(MavFrame.MAV_FRAME_GLOBAL_RELATIVE_ALT)
//                    .x(-35.3643678f) //-35.3643678
//                    .y(149.1695791f) //149.1695791
//                    .z(10)
//                    .build(), linkId, timestamp, secretKey);




//            2021-12-01 18:13 [INFO] [main] #3 --> CommandAck{command=EnumValue{value=176, entry=MAV_CMD_DO_SET_MODE}, result=EnumValue{value=0, entry=MAV_RESULT_ACCEPTED}, progress=0, resultParam2=0, targetSystem=255, targetComponent=190}
//            2021-12-01 18:13 [INFO] [main] #4 --> CommandAck{command=EnumValue{value=11, entry=null}, result=EnumValue{value=0, entry=MAV_RESULT_ACCEPTED}, progress=0, resultParam2=0, targetSystem=255, targetComponent=190}
//            2021-12-01 18:13 [INFO] [main] #6 --> CommandAck{command=EnumValue{value=11, entry=null}, result=EnumValue{value=0, entry=MAV_RESULT_ACCEPTED}, progress=0, resultParam2=0, targetSystem=255, targetComponent=190}




            //MissionCurrent|MissionItemInt
            //PositionTargetGlobalInt
//            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";
            MavlinkMessage message;
            while ((message = connection.next()) != null) {
                Object p = message.getPayload();
//                if(!objectNames.contains(p.getClass().getSimpleName())) {
                    logger.info("#" + message.getSequence() + " --> " + p);
//                }
            }

        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            System.out.println("전송됨");
        }
    }
}
