package com.danusys.web.drone.controller;


import com.danusys.web.drone.dto.response.MissionResponse;
import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.model.MissionDetails;
import com.danusys.web.drone.service.MissionDetailsService;
import com.danusys.web.drone.service.MissionService;
import com.google.gson.Gson;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.CommandLong;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavFrame;
import io.dronefleet.mavlink.common.MissionItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

import com.danusys.web.drone.utils.Flight;

@RestController
@RequestMapping("/drone/api")
@Slf4j
@RequiredArgsConstructor
public class DroneApiController {


    @Value("${tcp.server.host}")
    private String tcpServerHost;

    @Value("${tcp.server.port}")
    private int tcpServerPort;
    private final MissionService missionService;
    private final MissionDetailsService missionDetailsService;
    private final Flight flight;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/return")
    public void returnDrone() {
        try (Socket socket = new Socket(tcpServerHost, tcpServerPort)) {

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
    public void takeOffDrone(float z) {

        flight.takeoff(z);
    }


    @GetMapping("/goto")
    public void gotoDrone(float x, float y) {

        try (Socket socket = new Socket(tcpServerHost, tcpServerPort)) {

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

            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_DO_SET_MODE)
                    .param1(1)
                    .param2(4)
                    .build(), linkId, timestamp, secretKey);

            connection.send2(systemId, componentId, new MissionItem.Builder()
                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                    .targetSystem(0)
                    .targetComponent(0)
                    .seq(0)
                    .current(2)
                    .autocontinue(1)
                    .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                    .x(x)
                    .y(y)
                    .z(100)
                    .build(), linkId, timestamp, secretKey);

            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|HomePosition|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";
            MavlinkMessage message;
            while ((message = connection.next()) != null) {
                Object p = message.getPayload();
                if (!objectNames.contains(p.getClass().getSimpleName())) {
                    // logger.info("#" + message.getSequence() + " --> " + p);
                }
            }


        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            System.out.println("전송됨");
        }

    }

//
//    @GetMapping("/start")
//    public void MissonStart() {
//
//        try (Socket socket = new Socket("172.20.14.87", 14550)) {
//
//            MavlinkConnection connection = MavlinkConnection.create(
//                    socket.getInputStream(),
//                    socket.getOutputStream());
//
//            int systemId = 1;
//            int componentId = 1;
//            int linkId = 1;
//            long timestamp = System.currentTimeMillis();/* provide microsecond time */
//            ;
//            byte[] secretKey = MessageDigest.getInstance("SHA-256")
//                    .digest("danusys".getBytes(StandardCharsets.UTF_8));
//
//            connection.send2(systemId, componentId, new CommandLong.Builder()
//                    .command(MavCmd.MAV_CMD_MISSION_START)
//                    .build(), linkId, timestamp, secretKey);
//
//
//            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|HomePosition|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";
//            MavlinkMessage message;
//            while ((message = connection.next()) != null) {
//                Object p = message.getPayload();
//                if (!objectNames.contains(p.getClass().getSimpleName())) {
//                    // logger.info("#" + message.getSequence() + " --> " + p);
//                }
//            }
//
//
//        } catch (Exception ioe) {
//            ioe.printStackTrace();
//        } finally {
//            System.out.println("전송됨");
//        }
//
//
//    }

    @GetMapping("/plan")
    public void missonPlanDrone() {

        try (Socket socket = new Socket(tcpServerHost, tcpServerPort)) {

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
                if (!objectNames.contains(p.getClass().getSimpleName())) {
                    // logger.info("#" + message.getSequence() + " --> " + p);
                }
            }


        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            System.out.println("전송됨");
        }

    }

    @GetMapping("/hold")
    public void holdDrone() {


        try (Socket socket = new Socket(tcpServerHost, tcpServerPort)) {

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

            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_DO_SET_MODE)
                    .param1(1)
                    .param2(3)
                    .build(), linkId, timestamp, secretKey);

            Thread.sleep(1000);
            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_DO_PAUSE_CONTINUE)
                    .param1(0)

                    .build(), linkId, timestamp, secretKey);


            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|HomePosition|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";
            MavlinkMessage message;
            while ((message = connection.next()) != null) {
                Object p = message.getPayload();
                if (!objectNames.contains(p.getClass().getSimpleName())) {
                    // logger.info("#" + message.getSequence() + " --> " + p);
                }
            }


        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            System.out.println("전송됨");
        }

    }

//    @GetMapping("/waypoint")
//    public ResponseEntity<?> wayPoint(float x, float y, float z) {
//
//        String result = flight.wayPoint(x, y, z);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(result);
//    }

//    @MessageMapping("/drone3")
//    @SendTo("/topic/drone3")
//    public void topicDroneSendTo() {
//
//
////		if("start".equals(droneStart)) {
//        Map<String, Object> messages = new HashMap<>();
//        Gson gson = new Gson();
//        try (Socket socket = new Socket(tcpServerHost, tcpServerPort)) {
//            MavlinkConnection connection = MavlinkConnection.create(
//                    socket.getInputStream(),
//                    socket.getOutputStream());
//
//            MavlinkMessage message;
//            while ((message = connection.next()) != null) {
//                if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {
//                    messages.put("message", gson.toJson(message.getPayload()));
//
//                 //   if (message.getSequence() == 255) {
//
//                        messages = new HashMap<>();
//                 //   }
//
//
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            log.info("###topicDroneSendTo : 데이터 전송 종료~");
//        }
//    }


    @MessageMapping("/drone2")
    @SendTo("/topic/drone2")
    //@GetMapping("/startmission")
//    public void wayPoint(float x1, float y1, float x2, float y2) {
    public void startMission(Mission mission) {
        Gson gson = new Gson();
        Mission missionResponse = missionService.missionResponseList(mission.getId());
        int i = 2;
        HashMap<Integer, String> missionIndex = new HashMap<>();
        HashMap<String, Float> gpsXs = new HashMap<>();
        HashMap<String, Float> gpsYs = new HashMap<>();
        HashMap<String, Float> gpsZs = new HashMap<>();
        HashMap<String, String> result = new HashMap<>();
        Iterator iterator = missionResponse.getMissonDetails().iterator();

        while (iterator.hasNext()) {
            MissionDetails missionDetails = (MissionDetails) iterator.next();
            if (missionDetails.getName().equals("takeoff")) {

                gpsZs.put("takeoff", missionDetails.getAlt());
                missionIndex.put(1, "takeoff");


            } else if (missionDetails.getName().equals("waypoint")) {
                missionIndex.put(i, "waypoint" + missionDetails.getIndex());
                i++;
                gpsXs.put("waypoint" + missionDetails.getIndex(), missionDetails.getGpsX());
                gpsYs.put("waypoint" + missionDetails.getIndex(), missionDetails.getGpsY());
                gpsZs.put("waypoint" + missionDetails.getIndex(), missionDetails.getAlt());
            } else if (missionDetails.getName().equals("return")) {

                missionIndex.put(4, "return");
            }
        }

        log.info("gps={}", gpsZs.get("takeoff"));
        float takeOffAlt = gpsZs.get("takeoff");
        float x1 = gpsXs.get(missionIndex.get(2));
        float y1 = gpsYs.get(missionIndex.get(2));
        float z1 = gpsZs.get(missionIndex.get(2));


        float x2 = gpsXs.get(missionIndex.get(3));
        float y2 = gpsYs.get(missionIndex.get(3));
        float z2 = gpsZs.get(missionIndex.get(3));
        //리스트 분할해서 적용하기
        MavlinkConnection connection = null;
        MavlinkMessage message = null;

        int index = 0;


        if (missionIndex.get(1).equals("takeoff")) {

            connection=flight.takeoff(takeOffAlt);
        }

        log.info(missionIndex.get(2));
        if (missionIndex.get(2).equals("waypoint2")) {

            flight.wayPoint(x1, y1, z1);


        }
        if (missionIndex.get(3).equals("waypoint3")) {
            flight.wayPoint(x2, y2, z2);
        }

        if (missionIndex.get(4).equals("return")) {
            flight.returnDrone();
        }
//        try (Socket socket = new Socket(tcpServerHost, tcpServerPort)) {
//
//            connection = MavlinkConnection.create(
//                    socket.getInputStream(),
//                    socket.getOutputStream());
//
//            int systemId = 1;
//            int componentId = 1;
//            int linkId = 1;
//            long timestamp = System.currentTimeMillis();/* provide microsecond time */
//            ;
//            byte[] secretKey = MessageDigest.getInstance("SHA-256")
//                    .digest("danusys".getBytes(StandardCharsets.UTF_8));
//
//
//            connection.send2(systemId, componentId, new MissionItem.Builder()
//                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
//                    .targetSystem(0)
//                    .targetComponent(0)
//                    .seq(0)
//                    .current(2)
//                    .autocontinue(1)
//                    .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
//                    .x(x1)
//                    //37.445521894457784f
//                    .y(y1)
//                    //126.89555727255802f
//                    .z(z1)
//                    .build(), linkId, timestamp, secretKey);
//
//
////            CommandLong STATUS_gps = new CommandLong.Builder().command(MavCmd.MAV_CMD_SET_MESSAGE_INTERVAL).param1(24).param2(1000000).param7(0).build();
////            connection.send1(255, 0, STATUS_gps);
//
//
//            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|HomePosition|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";
//           // MavlinkMessage message;
//            String a = null;
//            String b = null;
//            int c = 0;
//
//            while ((message = connection.next()) != null) {
//                if (message.getPayload().getClass().getName().contains("GpsGlobalOrigin"))
//                    //log.info("message={}", message.getPayload());
//                if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {
//                //    log.info(message.getPayload().toString());
//                    a = message.getPayload().toString();
//                    index = a.indexOf("lat");
//                    b = a.substring(index + 4, index + 4 + 8);   //float형이라 8자리에서짤라도됨
//
//                    c = Integer.parseInt(b);
//                    //   log.info("c={}",c * 0.000001);
//                    //   log.info("x1={}",x1);
//                    if (c * 0.000001 <= x1 + 0.000001 && c * 0.000001 >= x1 - 0.000001 && x2!=0 && y2!=0) {
//                        log.info("도착");
//                        connection.send2(systemId, componentId, new MissionItem.Builder()
//                                .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
//                                .targetSystem(0)
//                                .targetComponent(0)
//                                .seq(0)
//                                .current(2)
//                                .autocontinue(1)
//                                .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
//                                .x(x2)
//                                //(37.443321894457784f)
//                                .y(y2)
//                                //126.89335727255802f
//                                .z(z2)
//                                .build(), linkId, timestamp, secretKey);
//                        return;
//                    }
//
//                }
//
//
//            }
//
//
//        } catch (Exception ioe) {
//            ioe.printStackTrace();
//        } finally {
//            System.out.println("전송됨");
//        }




        this.simpMessagingTemplate.convertAndSend("/topic/drone3", gson.toJson(result));

       // return ResponseEntity.status(HttpStatus.OK).body("success");
    }

}



