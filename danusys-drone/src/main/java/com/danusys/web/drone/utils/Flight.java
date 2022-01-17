package com.danusys.web.drone.utils;

import com.danusys.web.drone.dto.response.Gps;
import com.google.gson.Gson;
import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.*;
import io.mavsdk.telemetry.TelemetryProto;
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
import java.util.*;

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

    public HashMap<String, MissionItemInt> missionTakeoff() {

        MavlinkConnection connection = null;
        Socket socket = null;
        HashMap<String, MissionItemInt> missionItemMap = new HashMap<>();
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
                    .param1(15).param2(0).param3(0).param4(0).param5(0).param6(0).param7(50)
                    .build(), linkId, timestamp, secretKey);


            int flag = 0;
            while ((message = connection.next()) != null) {
                Gson gson = new Gson();
                //if(message.getPayload().getClass().getName().contains("MavlinkMessage")){
                //     if(message.getPayload() instanceof MissionAck){

                Mavlink2Message message2 = (Mavlink2Message) message;


                if (
                        message2.getPayload().getClass().getName().contains("SysStatus") ||//battery voltage배터리  //battery_remaining 배터리
                                message2.getPayload().getClass().getName().contains("PowerStatus") || //payload=PowerStatus{vcc=5000, vservo=0, flags=EnumValue{value=0, entry=null}}}
                                message2.getPayload().getClass().getName().contains("NavControllerOutput") || //wpdist 목적지와의 거리
                                message2.getPayload().getClass().getName().contains("GlobalPositionInt") ||
                                message2.getPayload().getClass().getName().contains("SensorOffsets") ||
                                message2.getPayload().getClass().getName().contains("MissionCurrent") || //payload=MissionCurrent{seq=0}
                                message2.getPayload().getClass().getName().contains("ServoOutputRaw") || //ServoOutputRaw{timeUsec=2772547204, port=0, servo1Raw=1591, servo2Raw=1591, servo3Raw=1590, servo4Raw=1591, servo5Raw=0, servo6Raw=0, servo7Raw=0, servo8Raw=0, servo9Raw=0, servo10Raw=0, servo11Raw=0, servo12Raw=0, servo13Raw=0, servo14Raw=0, servo15Raw=0, servo16Raw=0}
                                message2.getPayload().getClass().getName().contains("RcChannels") || //RcChannels{timeBootMs=2637547, chancount=16, chan1Raw=1500, chan2Raw=1500, chan3Raw=1000, chan4Raw=1500, chan5Raw=1800, chan6Raw=1000, chan7Raw=1000, chan8Raw=1800, chan9Raw=0, chan10Raw=0, chan11Raw=0, chan12Raw=0, chan13Raw=0, chan14Raw=0, chan15Raw=0, chan16Raw=0, chan17Raw=0, chan18Raw=0, rssi=255}}
                                message2.getPayload().getClass().getName().contains("RawImu") || //RawImu{timeUsec=2582049267, xacc=0, yacc=1, zacc=-997, xgyro=4, ygyro=3, zgyro=2, xmag=151, ymag=258, zmag=413, id=0, temperature=4499}
                                message2.getPayload().getClass().getName().contains("ScaledImu2") || //payload=ScaledImu2{timeBootMs=3097547, xacc=4, yacc=0, zacc=-1001, xgyro=-1, ygyro=0, zgyro=0, xmag=119, ymag=274, zmag=413, temperature=4499}
                                message2.getPayload().getClass().getName().contains("ScaledImu3") ||
                                message2.getPayload().getClass().getName().contains("ScaledPressure") ||
                                message2.getPayload().getClass().getName().contains("ScaledPressure2") ||
                                message2.getPayload().getClass().getName().contains("GpsRawInt") || //lat=374456473, lon=1268953303, alt=18230, eph=121, epv=200, vel=0, cog=3988, satellitesVisible=10, altEllipsoid=0, hAcc=300, vAcc=300, velAcc=40, hdgAcc=0
                                message2.getPayload().getClass().getName().contains("SystemTime") ||
                                message2.getPayload().getClass().getName().contains("LocalPositionNed") || //LocalPositionNed{timeBootMs=12024498, x=-0.07747139, y=0.061710242, z=0.001238235, vx=-0.013305673, vy=-1.9261298E-4, vz=4.191259E-4}}
                                message2.getPayload().getClass().getName().contains("Vibration") || //Vibration{timeUsec=14555498804, vibrationX=0.0026672243, vibrationY=0.0027407336, vibrationZ=0.0027245833, clipping0=0, clipping1=0, clipping2=0}}
                                message2.getPayload().getClass().getName().contains("BatteryStatus") || //batteryFunction=EnumValue{value=0, entry=MAV_BATTERY_FUNCTION_UNKNOWN}, type=EnumValue{value=0, entry=MAV_BATTERY_TYPE_UNKNOWN}, temperature=32767, voltages=[12587, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535], currentBattery=0, currentConsumed=151475, energyConsumed=68546, batteryRemaining=0, timeRemaining=0, chargeState=EnumValue{value=1, entry=MAV_BATTERY_CHARGE_STATE_OK}}}
                                message2.getPayload().getClass().getName().contains("Attitude") || //=Attitude{timeBootMs=11556094, roll=0.0018187475, pitch=6.057985E-4, yaw=2.2014248, rollspeed=2.1905173E-4, pitchspeed=2.5810348E-4, yawspeed=7.619873E-4}}
                                message2.getPayload().getClass().getName().contains("VfrHud") || // payload=VfrHud{airspeed=3.3680003, groundspeed=3.021782, heading=37, throttle=34, alt=99.979996, climb=0.015121608}}
                                message2.getPayload().getClass().getName().contains("Heartbeat") || //Heartbeat{type=EnumValue{value=2, entry=MAV_TYPE_QUADROTOR}, autopilot=EnumValue{value=3, entry=MAV_AUTOPILOT_ARDUPILOTMEGA}, baseMode=EnumValue{value=89, entry=null}, customMode=6, systemStatus=EnumValue{value=3, entry=MAV_STATE_STANDBY}, mavlinkVersion=3}}
                                message2.getPayload().getClass().getName().contains("Meminfo") || //payload=Meminfo{brkval=0, freemem=65535, freemem32=131072}}payload=Meminfo{brkval=0, freemem=65535, freemem32=131072}}
                                message2.getPayload().getClass().getName().contains("Ahrs") || //Ahrs{omegaix=-0.0025094047, omegaiy=-0.0025298656, omegaiz=-0.0020406283, accelWeight=0.0, renormVal=0.0, errorRp=0.002322554, errorYaw=0.0013488759}}
                                message2.getPayload().getClass().getName().contains("Hwstatus") || // payload=Hwstatus{vcc=5000, i2cerr=0}}
                                message2.getPayload().getClass().getName().contains("MountStatus") || //x
                                message2.getPayload().getClass().getName().contains("EkfStatusReport") || //velocityVariance=0.021335527, posHorizVariance=0.025864147, posVertVariance=0.0017659252, compassVariance=0.035943523, terrainAltVariance=0.0, airspeedVariance=0.0}}
                                message2.getPayload().getClass().getName().contains("Simstate") || //Simstate{roll=5.885804E-4, pitch=-5.997561E-7, yaw=-1.0545728, xacc=-0.09285733, yacc=-0.050651476, zacc=-9.81958, xgyro=-0.0067729917, ygyro=-0.0050521465, zgyro=3.3953006E-4, lat=374456475, lng=1268953310}
                                message2.getPayload().getClass().getName().contains("Ahrs2") || //x
                                message2.getPayload().getClass().getName().contains("Timesync") ||
                                message2.getPayload().getClass().getName().contains("ParamValue") || //x
                                message2.getPayload().getClass().getName().contains("PositionTargetGlobalInt") || //x
                                message2.getPayload().getClass().getName().contains("EscTelemetry1To4"))//EscTelemetry1To4{temperature=[B@553a3d88, voltage=[0, 0, 0, 0], current=[0, 0,
                {
                } else {
                    log.info(message2.toString());
                    if (message.getPayload() instanceof HomePosition) {
                        MavlinkMessage<HomePosition> homePositionMavlinkMessage = (MavlinkMessage<HomePosition>) message;
                        log.info("home Position = {}", homePositionMavlinkMessage.getPayload());
                        int latitude = homePositionMavlinkMessage.getPayload().latitude();//x
                        int longitude = homePositionMavlinkMessage.getPayload().longitude();//y
                        int altitude = homePositionMavlinkMessage.getPayload().altitude();//z

                        MissionItemInt missionItemInt0 = new MissionItemInt.Builder()
                                .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                                .param1(0)
                                .param2(0)
                                .param3(0)
                                .param4(0)
                                .x(latitude)
                                .y(longitude)
                                .z(altitude)
                                .seq(0)
                                .targetComponent(0)
                                .targetSystem(0)
                                .current(0)
                                .autocontinue(1)
                                .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                                .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
//                                .build(), linkId, timestamp, secretKey);
                                .build();


                        missionItemMap.put("missionItemInt0", missionItemInt0);


                    } else if (message.getPayload() instanceof TerrainReport) {
                        MavlinkMessage<TerrainReport> terrainReportMavlinkMessage = (MavlinkMessage<TerrainReport>) message;
                        float takeoff = terrainReportMavlinkMessage.getPayload().currentHeight();
                        if (takeoff >  30- 1.5) {
                            break;
                        }
                    }

                }
            }//end while


        } catch (Exception ioe) {


        } finally {
            System.out.println("holddrone");


        }
        return missionItemMap;
    }


    //try {
    public String flightTakeoff(float takeOffAlt) {
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

                }
                if (message.getPayload().getClass().getName().contains("TerrainReport")) {

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
                    log.info("return={}", (message.getPayload().getClass().getName().contains("GlobalPositionInt")));
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


    public String doMission(HashMap<String, MissionItemInt> missionItemMap, int maxFlag) {
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

            MissionCount count = MissionCount.builder().count(maxFlag).targetComponent(0).targetSystem(0).missionType(MavMissionType.MAV_MISSION_TYPE_MISSION).build();
            //   connection.send2(systemId, componentId, count, linkId, timestamp, secretKey);
            connection.send2(systemId, componentId, count);

            int flag = 0;
            while ((message = connection.next()) != null) {
                Gson gson = new Gson();
                //if(message.getPayload().getClass().getName().contains("MavlinkMessage")){
                //     if(message.getPayload() instanceof MissionAck){

                Mavlink2Message message2 = (Mavlink2Message) message;


                if (
                        message2.getPayload().getClass().getName().contains("SysStatus") ||//battery voltage배터리  //battery_remaining 배터리
                                message2.getPayload().getClass().getName().contains("PowerStatus") || //payload=PowerStatus{vcc=5000, vservo=0, flags=EnumValue{value=0, entry=null}}}
                                message2.getPayload().getClass().getName().contains("NavControllerOutput") || //wpdist 목적지와의 거리
                                message2.getPayload().getClass().getName().contains("GlobalPositionInt") ||
                                message2.getPayload().getClass().getName().contains("SensorOffsets") ||
                                message2.getPayload().getClass().getName().contains("MissionCurrent") || //payload=MissionCurrent{seq=0}
                                message2.getPayload().getClass().getName().contains("ServoOutputRaw") || //ServoOutputRaw{timeUsec=2772547204, port=0, servo1Raw=1591, servo2Raw=1591, servo3Raw=1590, servo4Raw=1591, servo5Raw=0, servo6Raw=0, servo7Raw=0, servo8Raw=0, servo9Raw=0, servo10Raw=0, servo11Raw=0, servo12Raw=0, servo13Raw=0, servo14Raw=0, servo15Raw=0, servo16Raw=0}
                                message2.getPayload().getClass().getName().contains("RcChannels") || //RcChannels{timeBootMs=2637547, chancount=16, chan1Raw=1500, chan2Raw=1500, chan3Raw=1000, chan4Raw=1500, chan5Raw=1800, chan6Raw=1000, chan7Raw=1000, chan8Raw=1800, chan9Raw=0, chan10Raw=0, chan11Raw=0, chan12Raw=0, chan13Raw=0, chan14Raw=0, chan15Raw=0, chan16Raw=0, chan17Raw=0, chan18Raw=0, rssi=255}}
                                message2.getPayload().getClass().getName().contains("RawImu") || //RawImu{timeUsec=2582049267, xacc=0, yacc=1, zacc=-997, xgyro=4, ygyro=3, zgyro=2, xmag=151, ymag=258, zmag=413, id=0, temperature=4499}
                                message2.getPayload().getClass().getName().contains("ScaledImu2") || //payload=ScaledImu2{timeBootMs=3097547, xacc=4, yacc=0, zacc=-1001, xgyro=-1, ygyro=0, zgyro=0, xmag=119, ymag=274, zmag=413, temperature=4499}
                                message2.getPayload().getClass().getName().contains("ScaledImu3") ||
                                message2.getPayload().getClass().getName().contains("ScaledPressure") ||
                                message2.getPayload().getClass().getName().contains("ScaledPressure2") ||
                                message2.getPayload().getClass().getName().contains("GpsRawInt") || //lat=374456473, lon=1268953303, alt=18230, eph=121, epv=200, vel=0, cog=3988, satellitesVisible=10, altEllipsoid=0, hAcc=300, vAcc=300, velAcc=40, hdgAcc=0
                                message2.getPayload().getClass().getName().contains("SystemTime") ||
                                message2.getPayload().getClass().getName().contains("TerrainReport") ||//TerrainReport{lat=374433470, lon=1268897507, spacing=100, terrainHeight=14.5117655, currentHeight=100.431244, pending=0, loaded=504}}
                                message2.getPayload().getClass().getName().contains("LocalPositionNed") || //LocalPositionNed{timeBootMs=12024498, x=-0.07747139, y=0.061710242, z=0.001238235, vx=-0.013305673, vy=-1.9261298E-4, vz=4.191259E-4}}
                                message2.getPayload().getClass().getName().contains("Vibration") || //Vibration{timeUsec=14555498804, vibrationX=0.0026672243, vibrationY=0.0027407336, vibrationZ=0.0027245833, clipping0=0, clipping1=0, clipping2=0}}
                                message2.getPayload().getClass().getName().contains("BatteryStatus") || //batteryFunction=EnumValue{value=0, entry=MAV_BATTERY_FUNCTION_UNKNOWN}, type=EnumValue{value=0, entry=MAV_BATTERY_TYPE_UNKNOWN}, temperature=32767, voltages=[12587, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535], currentBattery=0, currentConsumed=151475, energyConsumed=68546, batteryRemaining=0, timeRemaining=0, chargeState=EnumValue{value=1, entry=MAV_BATTERY_CHARGE_STATE_OK}}}
                                message2.getPayload().getClass().getName().contains("Attitude") || //=Attitude{timeBootMs=11556094, roll=0.0018187475, pitch=6.057985E-4, yaw=2.2014248, rollspeed=2.1905173E-4, pitchspeed=2.5810348E-4, yawspeed=7.619873E-4}}
                                message2.getPayload().getClass().getName().contains("VfrHud") || // payload=VfrHud{airspeed=3.3680003, groundspeed=3.021782, heading=37, throttle=34, alt=99.979996, climb=0.015121608}}
                                message2.getPayload().getClass().getName().contains("Heartbeat") || //Heartbeat{type=EnumValue{value=2, entry=MAV_TYPE_QUADROTOR}, autopilot=EnumValue{value=3, entry=MAV_AUTOPILOT_ARDUPILOTMEGA}, baseMode=EnumValue{value=89, entry=null}, customMode=6, systemStatus=EnumValue{value=3, entry=MAV_STATE_STANDBY}, mavlinkVersion=3}}
                                message2.getPayload().getClass().getName().contains("Meminfo") || //payload=Meminfo{brkval=0, freemem=65535, freemem32=131072}}payload=Meminfo{brkval=0, freemem=65535, freemem32=131072}}
                                message2.getPayload().getClass().getName().contains("Ahrs") || //Ahrs{omegaix=-0.0025094047, omegaiy=-0.0025298656, omegaiz=-0.0020406283, accelWeight=0.0, renormVal=0.0, errorRp=0.002322554, errorYaw=0.0013488759}}
                                message2.getPayload().getClass().getName().contains("Hwstatus") || // payload=Hwstatus{vcc=5000, i2cerr=0}}
                                message2.getPayload().getClass().getName().contains("MountStatus") || //x
                                message2.getPayload().getClass().getName().contains("EkfStatusReport") || //velocityVariance=0.021335527, posHorizVariance=0.025864147, posVertVariance=0.0017659252, compassVariance=0.035943523, terrainAltVariance=0.0, airspeedVariance=0.0}}
                                message2.getPayload().getClass().getName().contains("Simstate") || //Simstate{roll=5.885804E-4, pitch=-5.997561E-7, yaw=-1.0545728, xacc=-0.09285733, yacc=-0.050651476, zacc=-9.81958, xgyro=-0.0067729917, ygyro=-0.0050521465, zgyro=3.3953006E-4, lat=374456475, lng=1268953310}
                                message2.getPayload().getClass().getName().contains("Ahrs2") || //x
                                message2.getPayload().getClass().getName().contains("Timesync") ||
                                message2.getPayload().getClass().getName().contains("ParamValue") || //x
                                message2.getPayload().getClass().getName().contains("PositionTargetGlobalInt") || //x
                                message2.getPayload().getClass().getName().contains("EscTelemetry1To4"))//EscTelemetry1To4{temperature=[B@553a3d88, voltage=[0, 0, 0, 0], current=[0, 0,
                {
                } else {
                    log.info(message2.toString());

                    while (message.getPayload() instanceof MissionRequest && flag <= maxFlag-1) {
                        log.info("찾았다{}", missionItemMap.get("missionItemInt" + flag));
                        log.info("maxFlag={}",maxFlag);
                        log.info("flag");
                        connection.send2(systemId, componentId, missionItemMap.get("missionItemInt" + flag));



                        flag++;


                    }

                    if (message.getPayload() instanceof MissionRequest && flag == maxFlag) {
                        log.info("미션등록");
                        connection.send2(systemId, componentId, new CommandLong.Builder()
                                .command(MavCmd.MAV_CMD_DO_SET_MODE)
                                .param1(1)
                                .param2(3)
                                .build(), linkId, timestamp, secretKey);
                        flag++;
                    }


//                    if (message.getPayload() instanceof MissionRequest && flag == 0) {
//                        log.info("찾았따");
//                        connection.send2(systemId, componentId, missionItemMap.get("missionItemInt0"));
//                        flag++;
//                    } else if (message.getPayload() instanceof MissionRequest && flag == 1) {
//                        log.info("찾았따");
//                        connection.send2(systemId, componentId, missionItemMap.get("missionItemInt1"));
//                        flag++;
//                    } else if (message.getPayload() instanceof MissionRequest && flag == 2) {
//                        log.info("찾았따");
//                        connection.send2(systemId, componentId, missionItemMap.get("missionItemInt2"));
//                        flag++;
//                    } else if (flag == 3) {
//
//
//                    }

                }


                //  MavlinkMessage<MissionAck> missionAck = (MavlinkMessage<MissionAck>)message;
                //    if(missionAck.getPayload().type().entry().equals(MavMissionResult.MAV_MISSION_ACCEPTED)){}


            }

/*
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
                    log.info("43=={}", ( message.getPayload().getClass().getName().contains("GlobalPositionInt")));
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
                   //     break;
                    }
                }

                if (substring.timerFlag(message))
                    this.simpMessagingTemplate.convertAndSend("/topic/log", gson.toJson(gps));
                //this.simpMessagingTemplate.convertAndSend("/topic/drone3", gson.toJson(gps));

            }

*/
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
                    .command(MavCmd.MAV_CMD_SET_CAMERA_MODE)
                    .param1(0)
                    .param2(1)
//                    .param3(0)
//                    .param4(0)
//                    .param7(0)
                    .build(), linkId, timestamp, secretKey);

            connection.send2(systemId, componentId, new CommandLong.Builder()
                    .command(MavCmd.MAV_CMD_DO_CONTROL_VIDEO)
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




