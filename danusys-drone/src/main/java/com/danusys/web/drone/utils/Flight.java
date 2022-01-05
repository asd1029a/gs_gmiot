package com.danusys.web.drone.utils;

import com.danusys.web.drone.dto.response.Gps;
import com.google.gson.Gson;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

@Service
@Slf4j
@RequiredArgsConstructor
public class Flight {
    @Value("${tcp.server.host}")
    private String tcpServerHost;

    @Value("${tcp.server.port}")
    private int tcpServerPort;


    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Substring substring;


    //  public MavlinkConnection takeoff(Socket socket, MavlinkConnection connection, float takeOffAlt) {
    //try {
    public String takeoff(float takeOffAlt) {
        Socket socket = null;
        MavlinkConnection connection = null;
        Gps gps = new Gps();
        gps.setMissionId("takeoff");
        try {

            socket = new Socket(tcpServerHost, tcpServerPort);
            connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));

            MavlinkMessage message;
            String TerrianReportMessage = null;
            int index = 0;
            String currentHeight = null;
            float currentHeightFloat = 0;


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


            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_NAV_TAKEOFF)
                    .param1(15).param2(0).param3(0).param4(0).param5(0).param6(0).param7(takeOffAlt)
                    .build(), linkId, timestamp, secretKey);


//armed

            while ((message = connection.next()) != null) {
                Gson gson = new Gson();


                if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {

                    substring.getGps(message, gps);


                } else if (message.getPayload().getClass().getName().contains("TerrainReport")) {
                    TerrianReportMessage = message.getPayload().toString();
                    log.info("takeoff={}", TerrianReportMessage);
                    index = TerrianReportMessage.indexOf("currentHeight");
                    currentHeight = TerrianReportMessage.substring(index + 14, index + 14 + 3);   //float형이라 8자리에서짤라도됨

                    currentHeightFloat = Float.parseFloat(currentHeight);
                    gps.setCurrentHeight(currentHeightFloat);
                    log.info("height={}", currentHeightFloat);
                    if (currentHeightFloat >= takeOffAlt - 1.5) {
                        break;
                    }
                }
                if (substring.timerFlag(message))
                    this.simpMessagingTemplate.convertAndSend("/topic/log", gson.toJson(gps));

            }
//mavmodetype


        } catch (Exception ioe) {
            if (ioe instanceof EOFException) {

                try {
                    socket.close();
                    return "onemore";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            System.out.println("takeoff");


        }
        return "stop";
    }

    //public String wayPoint(Socket socket, MavlinkConnection connection, float x, float y, float z, float speed) {
    // try {
    public String wayPoint(float x, float y, float z, float speed) {
        Socket socket = null;
        MavlinkConnection connection = null;
        Timer timer = new Timer();
        try {

            socket = new Socket(tcpServerHost, tcpServerPort);
            connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));

            MavlinkMessage message;
            log.info("x={},y={}", x, y);
//           connection.send2(systemId, componentId, new CommandLong.Builder()
//                   .command(MavCmd.MAV_CMD_DO_SET_MODE)
//                   .param1(1)
//                   .param2(4)
//                   .build(), linkId, timestamp, secretKey);
//
//           connection.send2(systemId, componentId, new CommandLong.Builder()
//                   .command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM)
//                   .param1(1)
//                   .param2(0)
//                   .build(), linkId, timestamp, secretKey);


            connection.send2(systemId, componentId, new MissionItem.Builder()
                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                    .targetSystem(0)
                    .targetComponent(0)
                    .seq(0)
                    .current(2)
                    .autocontinue(1)
                    .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                    .x(x)
                    //37.445521894457784f
                    .y(y)
                    //126.89555727255802f
                    .z(z)
                    .build(), linkId, timestamp, secretKey);

//            connection.send2(systemId, componentId, new CommandLong.Builder()
//                    .command(MavCmd.MAV_CMD_DO_CHANGE_SPEED)
//                    .param1(0)
//                    .param2(speed)
//                    .param3(-1)
//                    .param4(0)
//                    .build(), linkId, timestamp, secretKey);


//            CommandLong STATUS_gps = new CommandLong.Builder().command(MavCmd.MAV_CMD_SET_MESSAGE_INTERVAL).param1(24).param2(1000000).param7(0).build();
//            connection.send1(2x55, 0, STATUS_gps);


            final String objectNames = "ParamValue|MissionCurrent|PositionTargetGlobalInt|Timesync|Attitude|Ahrs|Ahrs2|A|ttitude|BatteryStatus|EkfStatusReport|EscTelemetry1To4|GlobalPositionInt|GpsGlobalOrigin|GpsRawInt|Heartbeat|HomePosition|Hwstatus|LocalPositionNed|Meminfo|MountStatus|NavControllerOutput|PowerStatus|RawImu|RcChannels|ScaledImu2|ScaledImu3|ScaledPressure|ScaledPressure2|ServoOutputRaw|Simstate|Statustext|SysStatus|SystemTime|TerrainReport|VfrHud|Vibration";
            // MavlinkMessage message;
            String NavControllerOutput = null;
            String wpDistString = null;
            int wpDist = 0;
            int substringCount = 10;
            int index = 0;
            Gps gps = new Gps();
            gps.setMissionId("waypoint");
            while ((message = connection.next()) != null) {
                Gson gson = new Gson();

                if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {

                    substring.getGps(message, gps);
                }

                if (message.getPayload().getClass().getName().contains("TerrainReport")) {
                    log.info("waypoint={}", message.getPayload());
                }
                if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {
                    //  log.info(message.getPayload().toString());
                    NavControllerOutput = message.getPayload().toString();
                    index = NavControllerOutput.indexOf("wpDist");

                    //   log.info("istrue={}",b.contains(","));
                    while ((wpDistString = NavControllerOutput.substring(index + 7, index + 7 + substringCount)).contains(",")) {
                        substringCount--;

                    }
                    //    log.info(wpDistString);

                    gps.setWpDistString(wpDistString);
                    wpDist = Integer.parseInt(wpDistString);
                    //   log.info("c={}",c * 0.000001);
                    //   log.info("x1={}",x1);


                    if (wpDist == 0) {
                        log.info("도착");
                        return "success";
                        //  break;
                    }

                } if (message.getPayload().getClass().getName().contains("TerrainReport")) {

                    String TerrianReportMessage = message.getPayload().toString();
                    log.info("takeoff={}", TerrianReportMessage);
                    index = TerrianReportMessage.indexOf("currentHeight");
                    String currentHeight = TerrianReportMessage.substring(index + 14, index + 14 + 3);   //float형이라 8자리에서짤라도됨

                    Float currentHeightFloat = Float.parseFloat(currentHeight);
                    gps.setCurrentHeight(currentHeightFloat);
                }
                if (substring.timerFlag(message))
                    this.simpMessagingTemplate.convertAndSend("/topic/log", gson.toJson(gps));

            }


        } catch (Exception ioe) {
            if (ioe instanceof EOFException) {

                try {
                    socket.close();
                    return "onemore";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            System.out.println("wayPoint");


        }
        return "stop";
    }


    //public MavlinkConnection returnDrone(Socket socket) {
//        try {
    public String returnDrone() {
        MavlinkConnection connection = null;
        Socket socket = null;

        try {


            socket = new Socket(tcpServerHost, tcpServerPort);

            connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));


            MavlinkMessage message;
            String TerrianReportMessage = null;
            int index = 0;
            String currentHeight = null;
            float currentHeightFloat = 0;
            Gps gps = new Gps();
            gps.setMissionId("return");
            //돌아가기

            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_NAV_RETURN_TO_LAUNCH)
                    .build(), linkId, timestamp, secretKey);


            while ((message = connection.next()) != null) {
                Gson gson = new Gson();
                HashMap<String, Object> messages = new HashMap<>();
                if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {
                    log.info("return={}",(message.getPayload().getClass().getName().contains("GlobalPositionInt")));
                    substring.getGps(message, gps);

                } else if (message.getPayload().getClass().getName().contains("TerrainReport")) {
                    TerrianReportMessage = message.getPayload().toString();
                    log.info("return={}", TerrianReportMessage);
                    index = TerrianReportMessage.indexOf("currentHeight");
                    currentHeight = TerrianReportMessage.substring(index + 14, index + 14 + 3);   //float형이라 8자리에서짤라도됨
                    //      log.info("wow={}", currentHeight);
                    currentHeightFloat = Float.parseFloat(currentHeight);
                    gps.setCurrentHeight(currentHeightFloat);
                    log.info("float={}", currentHeightFloat);
                    if (currentHeightFloat <= 0.01) {
                        break;
                    }
                }
                if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {
                    //  log.info(message.getPayload().toString());
                    String NavControllerOutput = message.getPayload().toString();
                    index = NavControllerOutput.indexOf("wpDist");

                    //   log.info("istrue={}",b.contains(","));
                    String wpDistString = null;
                    int substringCount = 10;
                    while ((wpDistString = NavControllerOutput.substring(index + 7, index + 7 + substringCount)).contains(",")) {
                        substringCount--;

                    }
                    //    log.info(wpDistString);

                    gps.setWpDistString(wpDistString);
                }
                if (substring.timerFlag(message))
                    this.simpMessagingTemplate.convertAndSend("/topic/log", gson.toJson(gps));
                //this.simpMessagingTemplate.convertAndSend("/topic/drone3", gson.toJson(gps));
            }


        } catch (Exception ioe) {
            if (ioe instanceof EOFException) return "onemore";
        } finally {

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("returnDrone");


        }
        return "stop";

    }


    public String setHome() {
        MavlinkConnection connection = null;
        Socket socket = null;
        try {
            socket = new Socket(tcpServerHost, tcpServerPort);

            connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));


            MavlinkMessage message;
            String TerrianReportMessage = null;
            int index = 0;
            String currentHeight = null;
            float currentHeightFloat = 0;
            //돌아가기
            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_DO_SET_HOME).param1(0).param2(0).param3(0).param4(0).param5(37.4556f).param6(126.8963f).param7(22.012743f)
                    .build(), linkId, timestamp, secretKey);


        } catch (Exception ioe) {
            if (ioe instanceof EOFException) {

                try {
                    socket.close();
                    return "onemore";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            System.out.println("sethome");


        }
        return "stop";

    }

    public String holdDrone() {
        Socket socket = null;
        MavlinkConnection connection = null;
        try {
            socket = new Socket(tcpServerHost, tcpServerPort);
            connection = MavlinkConnection.create(
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
            if (ioe instanceof EOFException) {

                try {
                    socket.close();
                    return "onemore";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            System.out.println("holddrone");


        }
        return "stop";
    }


    public MavlinkConnection changeSpeedDrone(float speed) {
        MavlinkConnection connection = null;
        try (Socket socket = new Socket(tcpServerHost, tcpServerPort)) {

            connection = MavlinkConnection.create(
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
                    .command(MavCmd.MAV_CMD_DO_CHANGE_SPEED)
                    .param1(0)
                    .param2(speed)
                    .param3(-1)
                    .param4(0)
                    .build(), linkId, timestamp, secretKey);


            MavlinkMessage message;
            while ((message = connection.next()) != null) {

            }


        } catch (Exception ioe) {

            ioe.printStackTrace();
        } finally {
            System.out.println("전송됨");
        }

        return connection;
    }


    public String loiter(float stopTime) {
        MavlinkConnection connection = null;
        Socket socket = null;
        try {
            socket = new Socket(tcpServerHost, tcpServerPort);
            connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));

            MavlinkMessage message;

//
//            connection.send2(systemId, componentId, new MissionItem.Builder()
//                    .command(MavCmd.MAV_CMD_NAV_LOITER_TIME)
//                    .param1(5f)
//                    .param2(1)
//                    .param3(5f)
//                    .param4(0)
//                    .targetSystem(0)
//                    .targetComponent(0)
//                    .seq(0)
//                    .current(2)
//                    .autocontinue(0)
//                    .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
//                    .x(35.2444f)
//                    //37.445521894457784f
//                    .y(126.8988f)
//                    //126.89555727255802f
//                    .z(30f)
//                    .build(), linkId, timestamp, secretKey);


            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_NAV_LOITER_TIME)
                    .param1(stopTime)
                    .param2(0)
                    .param3(0)
                    .param4(0)
                    .param5(35.3444f)//37.445521894457784f
                    .param6(126.9288f)
                    //126.89555727255802f
                    .param7(30)
                    .build(), linkId, timestamp, secretKey);



            String attitude = null;
            String timeBootMs = null;
            int time = 0;
            int minTime = 0;

            int index = 0;
            int substringCount = 10;
            Gps gps =new Gps();
            Gson gson=new Gson();
            while ((message = connection.next()) != null) {
                if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {
                    log.info("return={}", (message.getPayload().getClass().getName().contains("GlobalPositionInt")));
                    substring.getGps(message, gps);

                } if (message.getPayload().getClass().getName().contains("TerrainReport")) {
                    String TerrianReportMessage = message.getPayload().toString();
                    log.info("return={}", TerrianReportMessage);
                    index = TerrianReportMessage.indexOf("currentHeight");
                    String currentHeight = TerrianReportMessage.substring(index + 14, index + 14 + 3);   //float형이라 8자리에서짤라도됨
                    //      log.info("wow={}", currentHeight);
                    float currentHeightFloat = Float.parseFloat(currentHeight);
                    gps.setCurrentHeight(currentHeightFloat);

                }
                if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {
                    //  log.info(message.getPayload().toString());
                    String NavControllerOutput = message.getPayload().toString();
                    index = NavControllerOutput.indexOf("wpDist");

                    //   log.info("istrue={}",b.contains(","));
                    String wpDistString = null;

                    while ((wpDistString = NavControllerOutput.substring(index + 7, index + 7 + substringCount)).contains(",")) {
                        substringCount--;

                    }
                    //    log.info(wpDistString);

                    gps.setWpDistString(wpDistString);
                }


                if (message.getPayload().getClass().getName().contains("Attitude")) {
                    //log.info("Attitude={}", message.getPayload());
                    attitude = message.getPayload().toString();
                    index = attitude.indexOf("timeBootMs");
                    while ((timeBootMs = attitude.substring(index + 11, index + 11 + substringCount)).contains(",")) {


                        substringCount--;
                    }
                    // 10

                    time = Integer.parseInt(timeBootMs);
                    if (time <= minTime || minTime == 0) {
                        minTime = time;
                    }
                    //log.info("{}",(time-minTime)/1000);

                    //    messages = new HashMap<>();


                    if ((time - minTime) / 1000 >= stopTime) {
                        break;
                    }
                }

                if (substring.timerFlag(message))
                    this.simpMessagingTemplate.convertAndSend("/topic/log", gson.toJson(gps));
                //this.simpMessagingTemplate.convertAndSend("/topic/drone3", gson.toJson(gps));

            }


        } catch (Exception ioe) {
            if (ioe instanceof EOFException) {

                try {
                    socket.close();
                    return "onemore";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            System.out.println("holddrone");


        }
        return "stop";
    }

    public String droneInfo() {
        Socket socket = null;
        MavlinkConnection connection = null;


        try {
            socket = new Socket(tcpServerHost, tcpServerPort);

            connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());
            Gps gps = new Gps();
            MavlinkMessage message;

            while ((message = connection.next()) != null) {
                Gson gson = new Gson();

                if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {
                    substring.getGps(message, gps);


                } else if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {

                    String NavControllerOutput = message.getPayload().toString();
                    int index = NavControllerOutput.indexOf("wpDist");
                    String wpDistString = null;
                    int substringCount = 10;
                    //   log.info("istrue={}",b.contains(","));
                    while ((wpDistString = NavControllerOutput.substring(index + 7, index + 7 + substringCount)).contains(",")) {
                        substringCount--;

                    }
                    //    log.info(wpDistString);
                    gps.setWpDistString(wpDistString);
                    if (substring.timerFlag(message))
                        this.simpMessagingTemplate.convertAndSend("/topic/log", gson.toJson(gps));


                }
            }
        } catch (Exception ioe) {
            if (ioe instanceof EOFException) {

                try {
                    socket.close();
                    return "onemore";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            System.out.println("droneInfo");


        }
        return "onemore";
    }


    public String camera() {
        MavlinkConnection connection = null;
        Socket socket = null;
        try {
            socket = new Socket(tcpServerHost, tcpServerPort);
            connection = MavlinkConnection.create(
                    socket.getInputStream(),
                    socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest("danusys".getBytes(StandardCharsets.UTF_8));

            MavlinkMessage message;



            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_SET_CAMERA_MODE )
                    .param1(0)
                    .param2(1)
//                    .param3(0)
//                    .param4(0)
//                    .param7(0)
                    .build(), linkId, timestamp, secretKey);

            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_DO_CONTROL_VIDEO )
                    .param1(-1)     //all camera
                    .param2(1)      //Transmission: 0: disabled, 1: enabled compressed, 2: enabled raw
                    .param3(0)      //Transmission mode: 0: video stream, >0: single images every n seconds
                    .param4(1)      //Recording: 0: disabled, 1: enabled compressed, 2: enabled raw
//                    .param5(0)
//                    .param6(0)
//                    .param7(0)


                    .build(), linkId, timestamp, secretKey);






            while ((message = connection.next()) != null) {
            }

        } catch (Exception ioe) {
            if (ioe instanceof EOFException) {

                try {
                    socket.close();
                    return "onemore";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            System.out.println("holddrone");


        }
        return "stop";
    }
}




