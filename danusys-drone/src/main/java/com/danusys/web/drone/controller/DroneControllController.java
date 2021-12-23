package com.danusys.web.drone.controller;


import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavFrame;
import io.dronefleet.mavlink.common.MissionItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Controller
@RequestMapping("/drone/api")
public class DroneControllController {


    @GetMapping("/move")
    public void MoveDrone(float x, float y, float z) {
        try (Socket socket = new Socket("172.20.14.87", 14550)) {

            MavlinkConnection connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));

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


            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|HomePosition|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";
            MavlinkMessage message;
            while ((message = connection.next()) != null) {
                Object p = message.getPayload();
                if (!objectNames.contains(p.getClass().getSimpleName())) {
                    //logger.info("#" + message.getSequence() + " --> " + p);
                }
            }


        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            System.out.println("전송됨");
        }
    }

    @GetMapping("/return")
    public void ReturnDrone() {
        try (Socket socket = new Socket("172.20.14.87", 14550)) {

            MavlinkConnection connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));

            //돌아가기
            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_NAV_RETURN_TO_LAUNCH)
                    .build(), linkId, timestamp, secretKey);


            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|HomePosition|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";
            MavlinkMessage message;
            while ((message = connection.next()) != null) {
                Object p = message.getPayload();
                if (!objectNames.contains(p.getClass().getSimpleName())) {
                    //    logger.info("#" + message.getSequence() + " --> " + p);
                }
            }


        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            System.out.println("전송됨");
        }
    }


    @GetMapping("takeoff")
    public void TakeOffDrone(){
        try (Socket socket = new Socket("172.20.14.87", 14550)) {

            MavlinkConnection connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));
            /*
        //돌아가기
            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_NAV_RETURN_TO_LAUNCH)
                    .build(), linkId, timestamp, secretKey);
*/

            //   System.out.println(EnumValue.of(MavMode.MAV_MODE_GUIDED_DISARMED).value());

            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_DO_SET_MODE)
                    .param1(1)
                    .param2(4)
                    .build(), linkId, timestamp, secretKey);

            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM)
                    .param1(1)
                    .param2(0)
                    .build(), linkId, timestamp, secretKey);

            int takeoff_alt=10;
            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_NAV_TAKEOFF)
                    .param1(15).param2(0).param3(0).param4(0).param5(0).param6(0).param7(takeoff_alt)
                    .build(), linkId, timestamp, secretKey);


//armed

//mavmodetype

            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|HomePosition|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";
            MavlinkMessage message;
            while ((message = connection.next()) != null) {
                Object p = message.getPayload();
                if(!objectNames.contains(p.getClass().getSimpleName())) {
                   // logger.info("#" + message.getSequence() + " --> " + p);
                }
            }


        }catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            System.out.println("전송됨");
        }
    }



}




