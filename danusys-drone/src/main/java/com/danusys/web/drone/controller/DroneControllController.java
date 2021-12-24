package com.danusys.web.drone.controller;


import com.danusys.web.drone.model.Misson;
import com.danusys.web.drone.repository.MissonRepository;
import com.danusys.web.drone.service.MissonService;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavFrame;
import io.dronefleet.mavlink.common.MissionItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/drone/api")
@Slf4j
@RequiredArgsConstructor
public class DroneControllController {


        private final MissonService missonService;
    @GetMapping("/move")
    public void MoveDrone() {
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
                    .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                    .x(37.443862156239504f)
                    .y(126.89168008272964f)
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


    @GetMapping("/takeoff")
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

            int takeoff_alt=50;
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


    @GetMapping("/misson")
    public void MissonDrone(){

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


            connection.send2(systemId, componentId, new MissionItem.Builder()
                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                    .targetSystem(0)
                    .targetComponent(0)
                    .seq(0)
                    .current(2)
                    .autocontinue(0)
                    .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                    .x(37.445521894457784f)
                    .y(126.89555727255802f)
                    .z(10)
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


    @GetMapping("/misson2")
    public void Misson2Drone(){

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



            connection.send2(systemId, componentId, new MissionItem.Builder()
                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                    .targetSystem(0)
                    .targetComponent(0)
                    .seq(0)
                    .current(2)
                    .autocontinue(1)
                    .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                    .x(37.445521894457784f)
                    .y(126.89555727255802f)
                    .z(100f)
                    .build(), linkId, timestamp, secretKey);
//
//            connection.send2(systemId, componentId, new MissionItem.Builder()
//                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
//                    .targetSystem(0)
//                    .targetComponent(0)
//                    .seq(0)
//                    .current(2)
//                    .autocontinue(1)
//                    .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
//                    .x(37.443421894457784f)
//                    .y(126.88339727255802f)
//                    .z(100f)
//                    .build(), linkId, timestamp, secretKey);


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

    @GetMapping("/goto")
    public void GotoDrone(){

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


            connection.send2(systemId, componentId, new MissionItem.Builder()
                    .command(MavCmd.MAV_CMD_OVERRIDE_GOTO)
                    .targetSystem(0)
                    .targetComponent(0)
                    .seq(0)
                    .current(2)
                    .autocontinue(0)
                    .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                    .x(37.413421894457784f)
                    .y(126.81339727255802f)
                    .z(100)
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


    @GetMapping("/start")
    public void MissonStart(){

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
                .command(MavCmd.MAV_CMD_MISSION_START)
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
    @GetMapping("/plan")
    public void MissonPlanDrone(){

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






            connection.send2(systemId, componentId, new MissionItem.Builder()
                    .command(MavCmd.MAV_CMD_NAV_PATHPLANNING)
                    .targetSystem(0)
                    .targetComponent(0)
                    .seq(0)
                    .current(2)
                    .autocontinue(0)
                    .frame(MavFrame.MAV_FRAME_MISSION)
                    .x(37.442321894457784f)
                    .y(126.89219727255802f)
                    .z(10)
                    .build(), linkId, timestamp, secretKey);


            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_MISSION_START)
                    .build(), linkId, timestamp, secretKey);


            //   System.out.println(EnumValue.of(MavMode.MAV_MODE_GUIDED_DISARMED).value());






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


    @PostMapping("/saveMisson")
    public ResponseEntity <?> saveMisson(Misson misson){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(missonService.saveMisson(misson));

    }

    @GetMapping("/findMisson")
    public ResponseEntity  findMisson(String name) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(missonService.findMisson(name));
    }



    @GetMapping("/getgps")
    public void GetGpsDrone(float x1, float y1, float x2, float y2, float x3, float y3){


        log.info("x1={},x2={}",x1,x2);
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



            connection.send2(systemId, componentId, new MissionItem.Builder()
                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                    .targetSystem(0)
                    .targetComponent(0)
                    .seq(0)
                    .current(2)
                    .autocontinue(1)
                    .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                    .x(x1)
                    //37.445521894457784f
                    .y(y1)
                    //126.89555727255802f
                    .z(100f)
                    .build(), linkId, timestamp, secretKey);





//            CommandLong STATUS_gps = new CommandLong.Builder().command(MavCmd.MAV_CMD_SET_MESSAGE_INTERVAL).param1(24).param2(1000000).param7(0).build();
//            connection.send1(255, 0, STATUS_gps);






            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|HomePosition|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";
            MavlinkMessage message;
            String a=null;
            String b=null;
            int c=0;
            int index=0;
            while ((message = connection.next()) != null) {
                if(message.getPayload().getClass().getName().contains("GpsGlobalOrigin"))
                log.info("message={}",message.getPayload());
                if(message.getPayload().getClass().getName().contains("GlobalPositionInt") ){
                    a=message.getPayload().toString();
                    index=a.indexOf("lat");
                    b=a.substring(index+4,index+4+8);   //float형이라 8자리에서짤라도됨

                    c=Integer.parseInt(b);
                    log.info("c={}",c * 0.000001);
                    log.info("x1={}",x1);
                    if(c*0.000001<=x1+0.000001 && c*0.000001 >=x1-0.000001){
                        log.info("도착");
                        connection.send2(systemId, componentId, new MissionItem.Builder()
                                .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                                .targetSystem(0)
                                .targetComponent(0)
                                .seq(0)
                                .current(2)
                                .autocontinue(1)
                                .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                                .x(x2)
                                //(37.443321894457784f)
                                .y(y2)
                                //126.89335727255802f
                                .z(100f)
                                .build(), linkId, timestamp, secretKey);
                        return;
                    }

                }





            }


        }catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            System.out.println("전송됨");
        }

    }
}



