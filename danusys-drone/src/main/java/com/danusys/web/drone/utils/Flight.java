package com.danusys.web.drone.utils;

import com.danusys.web.drone.dto.response.Gps;
import com.danusys.web.drone.model.DroneLog;
import com.danusys.web.drone.model.DroneLogDetails;
import com.danusys.web.drone.service.DroneDetailsService;
import com.danusys.web.drone.service.DroneLogDetailsService;
import com.danusys.web.drone.service.DroneLogService;
import com.google.gson.Gson;
import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.ardupilotmega.GimbalControl;
import io.dronefleet.mavlink.common.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.modeler.BaseModelMBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private final DroneLogDetailsService droneLogDetailsService;
    private final DroneLogService droneLogService;

    private MavlinkConnection connection = null;
    private Socket socket = null;
    private Gps gps = new Gps();
    private Gson gson = new Gson();
    private Timer t = null;
    private DroneLog droneLog = null;
    private int sec = 0;
    private int min = 0;
    private int hour = 0;
    private String stringSeconds=null;
    private String stringMinutes=null;
    private String stringHours=null;
    private TimerTask tt = null;

    public HashMap<String, MissionItemInt> missionTakeoff(DroneLog inputDroneLog, int droneId) {

        connection = null;
        socket = null;
        //Timer t = null;
        Gson gson = new Gson();
        HashMap<String, MissionItemInt> missionItemMap = new HashMap<>();
        droneLog = inputDroneLog;
        gps.setMissionType("0");
        gps.setStatus(1);
        gps.setDroneId(droneId);
        tt = new TimerTask() {
            @Override
            public void run() {

                stringSeconds=Integer.toString(sec);
                stringMinutes=Integer.toString(min);
                stringHours=Integer.toString(hour);

                if(sec<10){
                    stringSeconds="0"+stringSeconds;
                }
                if(min<10){
                    stringMinutes="0"+stringMinutes;
                }
                if(hour<10){
                    stringHours="0"+stringHours;
                }
                gps.setSec(stringSeconds);
                gps.setMin(stringMinutes);
                gps.setHour(stringHours);


                sec += 1;
                if (sec == 60) {
                    sec = 0;
                    min++;
                }
                if (min == 60) {
                    min = 0;
                    hour++;
                }
                simpMessagingTemplate.convertAndSend("/topic/log", gson.toJson(gps));
            }
        };
        try {

            socket = new Socket(tcpServerHost, tcpServerPort);
            connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
            Heartbeat heartbeat = null;
            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));

            t = new Timer();
            t.schedule(tt, 0, 1000);

            MavlinkMessage message;


            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_GET_HOME_POSITION).build(), linkId, timestamp, secretKey);
            DroneLogDetails droneLogDetailsHomePosition = new DroneLogDetails();

            droneLogDetailsHomePosition.setDroneLog(droneLog);
            droneLogDetailsHomePosition.setFromTarget("gcs");
            droneLogDetailsHomePosition.setToTarget("drone");
            droneLogDetailsHomePosition.setType("MAV_CMD_GET_HOME_POSITION");
            droneLogDetailsHomePosition.setParam1("0");
            droneLogDetailsHomePosition.setParam2("0");
            droneLogDetailsHomePosition.setParam3("0");
            droneLogDetailsHomePosition.setParam4("0");
            droneLogDetailsHomePosition.setParam5("0");
            droneLogDetailsHomePosition.setParam6("0");
            droneLogDetailsHomePosition.setParam7("0");

            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsHomePosition);
            while ((message = connection.next()) != null) {

                if (message.getPayload() instanceof HomePosition) {
                    MavlinkMessage<HomePosition> homePositionMavlinkMessage = (MavlinkMessage<HomePosition>) message;
                    log.info("home Position = {}", homePositionMavlinkMessage.getPayload());
                    int latitude = homePositionMavlinkMessage.getPayload().latitude();//x
                    int longitude = homePositionMavlinkMessage.getPayload().longitude();//y
                    int altitude = homePositionMavlinkMessage.getPayload().altitude();//z

                    MissionItemInt missionItemInt0 = new MissionItemInt.Builder().command(MavCmd.MAV_CMD_NAV_WAYPOINT).
                            param1(0).param2(0).param3(0).param4(0)
                            .x(latitude).y(longitude).z(altitude).seq(0)
                            .targetComponent(0).targetSystem(0).current(0).autocontinue(1)
                            .frame(MavFrame.MAV_FRAME_GLOBAL_INT).missionType(MavMissionType.MAV_MISSION_TYPE_MISSION).build();

                    missionItemMap.put("missionItemInt0", missionItemInt0);
                    break;
                }
            }
            //new connection
            connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());


            //4 guided mode


            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_DO_SET_MODE).param1(1).param2(4).build(), linkId, timestamp, secretKey);
            DroneLogDetails droneLogDetailsDoSetMode = new DroneLogDetails();
            droneLogDetailsDoSetMode.setDroneLog(droneLog);
            droneLogDetailsDoSetMode.setFromTarget("gcs");
            droneLogDetailsDoSetMode.setToTarget("drone");
            droneLogDetailsDoSetMode.setType("MAV_CMD_DO_SET_MODE");
            droneLogDetailsDoSetMode.setParam1("1");
            droneLogDetailsDoSetMode.setParam2("4");
            droneLogDetailsDoSetMode.setParam3("0");
            droneLogDetailsDoSetMode.setParam4("0");
            droneLogDetailsDoSetMode.setParam5("0");
            droneLogDetailsDoSetMode.setParam6("0");
            droneLogDetailsDoSetMode.setParam7("0");
            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsDoSetMode);
            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM).param1(1).param2(0).build(), linkId, timestamp, secretKey);


            DroneLogDetails droneLogDetailsArmDisarm = new DroneLogDetails();
            droneLogDetailsArmDisarm.setDroneLog(droneLog);
            droneLogDetailsArmDisarm.setFromTarget("gcs");
            droneLogDetailsArmDisarm.setToTarget("drone");
            droneLogDetailsArmDisarm.setType("MAV_CMD_COMPONENT_ARM_DISARM");
            droneLogDetailsArmDisarm.setParam1("1");
            droneLogDetailsArmDisarm.setParam2("0");
            droneLogDetailsArmDisarm.setParam3("0");
            droneLogDetailsArmDisarm.setParam4("0");
            droneLogDetailsArmDisarm.setParam5("0");
            droneLogDetailsArmDisarm.setParam6("0");
            droneLogDetailsArmDisarm.setParam7("0");
            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsArmDisarm);

            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_NAV_TAKEOFF).param1(15).param2(0).param3(0).param4(0).param5(0).param6(0).param7(40).build(), linkId, timestamp, secretKey);
            DroneLogDetails droneLogDetailsTakeOff = new DroneLogDetails();
            droneLogDetailsTakeOff.setDroneLog(droneLog);
            droneLogDetailsTakeOff.setFromTarget("gcs");
            droneLogDetailsTakeOff.setToTarget("drone");
            droneLogDetailsTakeOff.setType("MAV_CMD_NAV_TAKEOFF");
            droneLogDetailsTakeOff.setParam1("15");
            droneLogDetailsTakeOff.setParam2("0");
            droneLogDetailsTakeOff.setParam3("0");
            droneLogDetailsTakeOff.setParam4("0");
            droneLogDetailsTakeOff.setParam5("0");
            droneLogDetailsTakeOff.setParam6("0");
            droneLogDetailsTakeOff.setParam7("40");
            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsTakeOff);
            int flag = 0;
            while ((message = connection.next()) != null) {

                if (message.getPayload() instanceof TerrainReport) {
                    MavlinkMessage<TerrainReport> terrainReportMavlinkMessage = (MavlinkMessage<TerrainReport>) message;
                    float takeoff = terrainReportMavlinkMessage.getPayload().currentHeight();
                    if (takeoff > 40 - 1.5) {
                        break;
                    }

                } else if (message.getPayload() instanceof Heartbeat) {
                    MavlinkMessage<Heartbeat> heartbeatMavlinkMessage = (MavlinkMessage<Heartbeat>) message;
                    heartbeat = Heartbeat.builder().autopilot(heartbeatMavlinkMessage.getPayload().autopilot())
                            .type(heartbeatMavlinkMessage.getPayload().type())
                            .systemStatus(heartbeatMavlinkMessage.getPayload().systemStatus())
                            .baseMode()
                            .mavlinkVersion(heartbeatMavlinkMessage.getPayload().mavlinkVersion())
                            .build();
                    connection.send2(systemId, componentId, heartbeat, linkId, timestamp, secretKey);

                } else if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {      //x,y,z
                    MavlinkMessage<GlobalPositionInt> globalPositionIntMavlinkMessage = (MavlinkMessage<GlobalPositionInt>) message;
                    int x = globalPositionIntMavlinkMessage.getPayload().lat();
                    int y = globalPositionIntMavlinkMessage.getPayload().lon();
                    int z = globalPositionIntMavlinkMessage.getPayload().alt();
                    int heading = globalPositionIntMavlinkMessage.getPayload().hdg();

                    gps.setGpsX((double) y / 10000000);
                    gps.setGpsY((double) x / 10000000);
                    gps.setCurrentHeight((double) z / 1000);
                    gps.setHeading(heading / 100);

                } else if (message.getPayload().getClass().getName().contains("VfrHud")) {
                    MavlinkMessage<VfrHud> vfrHudMavlinkMessage = (MavlinkMessage<VfrHud>) message;

                    float airSpeed = vfrHudMavlinkMessage.getPayload().airspeed();

                    gps.setAirSpeed(Float.parseFloat(String.format("%.1f", airSpeed)));
                } else if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {//wpdist
                    MavlinkMessage<NavControllerOutput> navControllerOutputMavlinkMessage = (MavlinkMessage<NavControllerOutput>) message;

                    int wpDist = navControllerOutputMavlinkMessage.getPayload().wpDist();

                    gps.setWpDist(wpDist);

                } else if (message.getPayload() instanceof Statustext) {        //statusMessage


                    log.info(message.toString());


                    MavlinkMessage<Statustext> statustextMavlinkMessage = (MavlinkMessage<Statustext>) message;


                    String missionText = statustextMavlinkMessage.getPayload().text();
                    log.info(missionText);
                    //   gps.setMissionType(missionText);


                } else if (message.getPayload() instanceof CommandAck) {
                    MavlinkMessage<CommandAck> commandAckMavlinkMessage = (MavlinkMessage<CommandAck>) message;
                    DroneLogDetails droneLogDetailsCommandAck = new DroneLogDetails();
                    droneLogDetailsCommandAck.setDroneLog(droneLog);
                    droneLogDetailsCommandAck.setFromTarget("drone");
                    droneLogDetailsCommandAck.setToTarget("gcs");
                    droneLogDetailsCommandAck.setType("CommandAck");
                    droneLogDetailsCommandAck.setParam1(commandAckMavlinkMessage.getPayload().command().toString());
                    droneLogDetailsCommandAck.setParam2(commandAckMavlinkMessage.getPayload().result().toString());
                    droneLogDetailsCommandAck.setParam3("0");
                    droneLogDetailsCommandAck.setParam4("0");
                    droneLogDetailsCommandAck.setParam5("0");
                    droneLogDetailsCommandAck.setParam6("0");
                    droneLogDetailsCommandAck.setParam7("0");
                    droneLogDetailsService.saveDroneLogDetails(droneLogDetailsCommandAck);
                    log.info("commandAck={}", message);
                }


            }
            //end while


        } catch (Exception ioe) {
            ioe.printStackTrace();

        } finally {
//            t.purge();
            log.info("endtakeoff");
//            try {
//                //socket.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//
//            }


        }
        return missionItemMap;
    }


    //try {
    public String flightTakeoff(float takeOffAlt) {
        connection = null;
        socket = null;

        Gson gson = new Gson();

        try {
            socket = new Socket(tcpServerHost, tcpServerPort);
            connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
            Heartbeat heartbeat = null;
            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));

//            t = new Timer();
//            t.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    simpMessagingTemplate.convertAndSend("/topic/log", gson.toJson(gps));
//                }
//            }, 0, 2000);
            MavlinkMessage message;

            //4 guided mode
            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_DO_SET_MODE).param1(1).param2(4).build(), linkId, timestamp, secretKey);

            DroneLogDetails droneLogDetailsDoSetMode = new DroneLogDetails();

            droneLogDetailsDoSetMode.setFromTarget("gcs");
            droneLogDetailsDoSetMode.setToTarget("drone");
            droneLogDetailsDoSetMode.setType("MAV_CMD_DO_SET_MODE");
            droneLogDetailsDoSetMode.setParam1("1");
            droneLogDetailsDoSetMode.setParam2("4");
            droneLogDetailsDoSetMode.setParam3("0");
            droneLogDetailsDoSetMode.setParam4("0");
            droneLogDetailsDoSetMode.setParam5("0");
            droneLogDetailsDoSetMode.setParam6("0");
            droneLogDetailsDoSetMode.setParam7("0");
            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsDoSetMode);

            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM).param1(1).param2(0).build(), linkId, timestamp, secretKey);

            DroneLogDetails droneLogDetailsArmDisarm = new DroneLogDetails();
            droneLogDetailsArmDisarm.setFromTarget("gcs");
            droneLogDetailsArmDisarm.setToTarget("drone");
            droneLogDetailsArmDisarm.setType("MAV_CMD_COMPONENT_ARM_DISARM");
            droneLogDetailsArmDisarm.setParam1("1");
            droneLogDetailsArmDisarm.setParam2("0");
            droneLogDetailsArmDisarm.setParam3("0");
            droneLogDetailsArmDisarm.setParam4("0");
            droneLogDetailsArmDisarm.setParam5("0");
            droneLogDetailsArmDisarm.setParam6("0");
            droneLogDetailsArmDisarm.setParam7("0");
            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsArmDisarm);

            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_NAV_TAKEOFF).param1(15).param2(0).param3(0).param4(0).param5(0).param6(0).param7(takeOffAlt).build(), linkId, timestamp, secretKey);

            DroneLogDetails droneLogDetailsTakeOff = new DroneLogDetails();
            droneLogDetailsTakeOff.setFromTarget("gcs");
            droneLogDetailsTakeOff.setToTarget("drone");
            droneLogDetailsTakeOff.setType("MAV_CMD_NAV_TAKEOFF");
            droneLogDetailsTakeOff.setParam1("15");
            droneLogDetailsTakeOff.setParam2("0");
            droneLogDetailsTakeOff.setParam3("0");
            droneLogDetailsTakeOff.setParam4("0");
            droneLogDetailsTakeOff.setParam5("0");
            droneLogDetailsTakeOff.setParam6("0");
            droneLogDetailsTakeOff.setParam7("40");
            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsTakeOff);


            int flag = 0;
            while ((message = connection.next()) != null) {

                if (message.getPayload() instanceof TerrainReport) {
                    MavlinkMessage<TerrainReport> terrainReportMavlinkMessage = (MavlinkMessage<TerrainReport>) message;
                    float takeoff = terrainReportMavlinkMessage.getPayload().currentHeight();
                    if (takeoff > takeOffAlt - 1.5) {
                        break;
                    }

                } else if (message.getPayload() instanceof Heartbeat) {
                    MavlinkMessage<Heartbeat> heartbeatMavlinkMessage = (MavlinkMessage<Heartbeat>) message;
                    heartbeat = Heartbeat.builder().autopilot(heartbeatMavlinkMessage.getPayload().autopilot())
                            .type(heartbeatMavlinkMessage.getPayload().type())
                            .systemStatus(heartbeatMavlinkMessage.getPayload().systemStatus())
                            .baseMode()
                            .mavlinkVersion(heartbeatMavlinkMessage.getPayload().mavlinkVersion())
                            .build();


                    connection.send2(systemId, componentId, heartbeat, linkId, timestamp, secretKey);
                } else if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {      //x,y,z
                    MavlinkMessage<GlobalPositionInt> globalPositionIntMavlinkMessage = (MavlinkMessage<GlobalPositionInt>) message;
                    int x = globalPositionIntMavlinkMessage.getPayload().lat();
                    int y = globalPositionIntMavlinkMessage.getPayload().lon();
                    int z = globalPositionIntMavlinkMessage.getPayload().alt();
                    int heading = globalPositionIntMavlinkMessage.getPayload().hdg();

                    gps.setGpsX((double) y / 10000000);
                    gps.setGpsY((double) x / 10000000);
                    gps.setCurrentHeight((double) z / 1000);
                    gps.setHeading(heading / 100);

                } else if (message.getPayload().getClass().getName().contains("VfrHud")) {
                    MavlinkMessage<VfrHud> vfrHudMavlinkMessage = (MavlinkMessage<VfrHud>) message;

                    float airSpeed = vfrHudMavlinkMessage.getPayload().airspeed();
                    gps.setAirSpeed(Float.parseFloat(String.format("%.1f", airSpeed)));
                } else if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {//wpdist
                    MavlinkMessage<NavControllerOutput> navControllerOutputMavlinkMessage = (MavlinkMessage<NavControllerOutput>) message;

                    int wpDist = navControllerOutputMavlinkMessage.getPayload().wpDist();

                    gps.setWpDist(wpDist);

                } else if (message.getPayload() instanceof Statustext) {        //statusMessage


                    log.info(message.toString());


                    MavlinkMessage<Statustext> statustextMavlinkMessage = (MavlinkMessage<Statustext>) message;


                    String missionText = statustextMavlinkMessage.getPayload().text();

                    log.info(missionText);
                    if (missionText.equals("Arming motors")) {
                        gps.setMissionType("takeoff");
                    }


                } else if (message.getPayload() instanceof CommandAck) {
                    MavlinkMessage<CommandAck> commandAckMavlinkMessage = (MavlinkMessage<CommandAck>) message;
                    log.info("commandAck={}", message);
                    DroneLogDetails droneLogDetailsCommandAck = new DroneLogDetails();
                    droneLogDetailsCommandAck.setFromTarget("drone");
                    droneLogDetailsCommandAck.setToTarget("gcs");
                    droneLogDetailsCommandAck.setType("CommandAck");
                    droneLogDetailsCommandAck.setParam1(commandAckMavlinkMessage.getPayload().command().toString());
                    droneLogDetailsCommandAck.setParam2(commandAckMavlinkMessage.getPayload().result().toString());
                    droneLogDetailsCommandAck.setParam3("0");
                    droneLogDetailsCommandAck.setParam4("0");
                    droneLogDetailsCommandAck.setParam5("0");
                    droneLogDetailsCommandAck.setParam6("0");
                    droneLogDetailsCommandAck.setParam7("0");
                    droneLogDetailsService.saveDroneLogDetails(droneLogDetailsCommandAck);
                }


            }


        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {

//            t.purge();
            System.out.println("takeoff");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return "end";
    }

    //public String wayPoint(Socket socket, MavlinkConnection connection, float x, float y, float z, float speed) {
    // try {
    //x,y 반대로 넣어야되기떄문에
    public String wayPoint(int gpsY, int gpsX, int gpsZ, int yaw) {
        socket = null;
        connection = null;

        Gson gson = new Gson();
        gps.setMissionType("waypoint");

        try {

            socket = new Socket(tcpServerHost, tcpServerPort);
            connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));

            MavlinkMessage message;
            log.info("x={},y={}", gpsX, gpsY);

            connection.send2(systemId, componentId, new MissionItemInt.Builder().command(MavCmd.MAV_CMD_NAV_WAYPOINT).param1(0)
                    .targetSystem(0).targetComponent(0).seq(0).current(2).autocontinue(1).frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                    .x(gpsX)
                    .y(gpsY)
                    .z(gpsZ).build(), linkId, timestamp, secretKey);

            DroneLogDetails droneLogDetailsWayPoint = new DroneLogDetails();
            droneLogDetailsWayPoint.setFromTarget("gcs");
            droneLogDetailsWayPoint.setToTarget("drone");
            droneLogDetailsWayPoint.setType("MAV_CMD_NAV_WAYPOINT");
            droneLogDetailsWayPoint.setParam1("0");
            droneLogDetailsWayPoint.setParam2("0");
            droneLogDetailsWayPoint.setParam3("0");
            droneLogDetailsWayPoint.setParam4("0");
            droneLogDetailsWayPoint.setParam5(Integer.toString(gpsX));
            droneLogDetailsWayPoint.setParam6(Integer.toString(gpsY));
            droneLogDetailsWayPoint.setParam7(Integer.toString(gpsZ));
            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsWayPoint);

            connection.send2(systemId, componentId, new CommandLong.Builder().
                    command(MavCmd.MAV_CMD_CONDITION_YAW)
                    .param1(yaw)
                    .param2(0)
                    .param3(1)
                    .param4(0)
                    .build(), linkId, timestamp, secretKey);

            DroneLogDetails droneLogDetailsConditionYaw = new DroneLogDetails();
            droneLogDetailsWayPoint.setFromTarget("gcs");
            droneLogDetailsWayPoint.setToTarget("drone");
            droneLogDetailsWayPoint.setType("MAV_CMD_CONDITION_YAW");
            droneLogDetailsWayPoint.setParam1(Integer.toString(yaw));
            droneLogDetailsWayPoint.setParam2("0");
            droneLogDetailsWayPoint.setParam3("1");
            droneLogDetailsWayPoint.setParam4("0");

            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsConditionYaw);
            Heartbeat heartbeat = null;
//            t = new Timer();
//            t.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    simpMessagingTemplate.convertAndSend("/topic/log", gson.toJson(gps));
//                }
//            }, 0, 2000);

            while ((message = connection.next()) != null) {

                if (message.getPayload() instanceof Heartbeat) {
                    MavlinkMessage<Heartbeat> heartbeatMavlinkMessage = (MavlinkMessage<Heartbeat>) message;
                    heartbeat = Heartbeat.builder().autopilot(heartbeatMavlinkMessage.getPayload().autopilot())
                            .type(heartbeatMavlinkMessage.getPayload().type())
                            .systemStatus(heartbeatMavlinkMessage.getPayload().systemStatus())
                            .baseMode()
                            .mavlinkVersion(heartbeatMavlinkMessage.getPayload().mavlinkVersion())
                            .build();
                    connection.send2(systemId, componentId, heartbeat, linkId, timestamp, secretKey);
                } else if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {      //x,y,z
                    MavlinkMessage<GlobalPositionInt> globalPositionIntMavlinkMessage = (MavlinkMessage<GlobalPositionInt>) message;
                    int x = globalPositionIntMavlinkMessage.getPayload().lat();
                    int y = globalPositionIntMavlinkMessage.getPayload().lon();
                    int z = globalPositionIntMavlinkMessage.getPayload().alt();
                    int heading = globalPositionIntMavlinkMessage.getPayload().hdg();

                    gps.setGpsX((double) y / 10000000);
                    gps.setGpsY((double) x / 10000000);
                    gps.setCurrentHeight((double) z / 1000);
                    gps.setHeading(heading / 100);

                } else if (message.getPayload().getClass().getName().contains("VfrHud")) {
                    MavlinkMessage<VfrHud> vfrHudMavlinkMessage = (MavlinkMessage<VfrHud>) message;

                    float airSpeed = vfrHudMavlinkMessage.getPayload().airspeed();
                    gps.setAirSpeed(Float.parseFloat(String.format("%.1f", airSpeed)));
                } else if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {//wpdist
                    MavlinkMessage<NavControllerOutput> navControllerOutputMavlinkMessage = (MavlinkMessage<NavControllerOutput>) message;

                    int wpDist = navControllerOutputMavlinkMessage.getPayload().wpDist();

                    gps.setWpDist(wpDist);
                    if (wpDist == 0) {
                        break;
                    }

                } else if (message.getPayload() instanceof Statustext) {        //statusMessage

                    log.info(message.toString());
                    MavlinkMessage<Statustext> statustextMavlinkMessage = (MavlinkMessage<Statustext>) message;


                    String missionText = statustextMavlinkMessage.getPayload().text();
                    log.info(missionText);
                    gps.setMissionType("waypoint");


                } else if (message.getPayload() instanceof CommandAck) {
                    MavlinkMessage<CommandAck> commandAckMavlinkMessage = (MavlinkMessage<CommandAck>) message;
                    log.info("commandAck={}", message);
                    DroneLogDetails droneLogDetailsCommandAck = new DroneLogDetails();
                    droneLogDetailsCommandAck.setFromTarget("drone");
                    droneLogDetailsCommandAck.setToTarget("gcs");
                    droneLogDetailsCommandAck.setType("CommandAck");
                    droneLogDetailsCommandAck.setParam1(commandAckMavlinkMessage.getPayload().command().toString());
                    droneLogDetailsCommandAck.setParam2(commandAckMavlinkMessage.getPayload().result().toString());
                    droneLogDetailsCommandAck.setParam3("0");
                    droneLogDetailsCommandAck.setParam4("0");
                    droneLogDetailsCommandAck.setParam5("0");
                    droneLogDetailsCommandAck.setParam6("0");
                    droneLogDetailsCommandAck.setParam7("0");
                    droneLogDetailsService.saveDroneLogDetails(droneLogDetailsCommandAck);
                }

            }


        } catch (Exception ioe) {


        } finally {
//            t.purge();
            try {
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("wayPoint");


        }
        return "end";
    }


    //public MavlinkConnection returnDrone(Socket socket) {
//        try {
    public String returnDrone() {

        try {
            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));
            gps.setMissionType("return");
            MavlinkMessage message;

            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_NAV_RETURN_TO_LAUNCH).build(), linkId, timestamp, secretKey);

            DroneLogDetails droneLogDetailsReturnToLaunch = new DroneLogDetails();
            droneLogDetailsReturnToLaunch.setFromTarget("gcs");
            droneLogDetailsReturnToLaunch.setToTarget("drone");
            droneLogDetailsReturnToLaunch.setType("MAV_CMD_NAV_RETURN_TO_LAUNCH");
            droneLogDetailsReturnToLaunch.setParam1("0");
            droneLogDetailsReturnToLaunch.setParam2("0");
            droneLogDetailsReturnToLaunch.setParam3("0");
            droneLogDetailsReturnToLaunch.setParam4("0");
            droneLogDetailsReturnToLaunch.setParam5("0");
            droneLogDetailsReturnToLaunch.setParam6("0");
            droneLogDetailsReturnToLaunch.setParam7("0");
            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsReturnToLaunch);

            Heartbeat heartbeat = null;
//            t = new Timer();
//
//
//            t.schedule(tt, 0, 2000);


            while ((message = connection.next()) != null) {

                if (message.getPayload() instanceof Heartbeat) {
                    MavlinkMessage<Heartbeat> heartbeatMavlinkMessage = (MavlinkMessage<Heartbeat>) message;
                    heartbeat = Heartbeat.builder().autopilot(heartbeatMavlinkMessage.getPayload().autopilot())
                            .type(heartbeatMavlinkMessage.getPayload().type())
                            .systemStatus(heartbeatMavlinkMessage.getPayload().systemStatus())
                            .baseMode()
                            .mavlinkVersion(heartbeatMavlinkMessage.getPayload().mavlinkVersion())
                            .build();
                    connection.send2(systemId, componentId, heartbeat, linkId, timestamp, secretKey);
                } else if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {      //x,y,z
                    MavlinkMessage<GlobalPositionInt> globalPositionIntMavlinkMessage = (MavlinkMessage<GlobalPositionInt>) message;
                    int x = globalPositionIntMavlinkMessage.getPayload().lat();
                    int y = globalPositionIntMavlinkMessage.getPayload().lon();
                    int z = globalPositionIntMavlinkMessage.getPayload().alt();
                    int heading = globalPositionIntMavlinkMessage.getPayload().hdg();

                    gps.setGpsX((double) y / 10000000);
                    gps.setGpsY((double) x / 10000000);
                    gps.setCurrentHeight((double) z / 1000);
                    gps.setHeading(heading / 100);

                } else if (message.getPayload().getClass().getName().contains("VfrHud")) {
                    MavlinkMessage<VfrHud> vfrHudMavlinkMessage = (MavlinkMessage<VfrHud>) message;

                    float airSpeed = vfrHudMavlinkMessage.getPayload().airspeed();
                    gps.setAirSpeed(Float.parseFloat(String.format("%.1f", airSpeed)));
                } else if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {//wpdist
                    MavlinkMessage<NavControllerOutput> navControllerOutputMavlinkMessage = (MavlinkMessage<NavControllerOutput>) message;

                    int wpDist = navControllerOutputMavlinkMessage.getPayload().wpDist();
                    gps.setWpDist(wpDist);


                } else if (message.getPayload() instanceof Statustext) {        //statusMessage


                    MavlinkMessage<Statustext> statustextMavlinkMessage = (MavlinkMessage<Statustext>) message;

                    if (statustextMavlinkMessage.getPayload().text().contains("Hit ground")) {

                    } else if (statustextMavlinkMessage.getPayload().text().equals("Disarming motors")) {
                        gps.setMissionType("end");
                        gps.setStatus(0);
                        Thread.sleep(2000);
                        break;
                    }

                    String missionText = statustextMavlinkMessage.getPayload().text();
                    log.info(missionText);
                    gps.setMissionType(missionText);


                } else if (message.getPayload() instanceof CommandAck) {
                    MavlinkMessage<CommandAck> commandAckMavlinkMessage = (MavlinkMessage<CommandAck>) message;
                    log.info("commandAck={}", message);
                    DroneLogDetails droneLogDetailsCommandAck = new DroneLogDetails();
                    droneLogDetailsCommandAck.setFromTarget("drone");
                    droneLogDetailsCommandAck.setToTarget("gcs");
                    droneLogDetailsCommandAck.setType("CommandAck");
                    droneLogDetailsCommandAck.setParam1(commandAckMavlinkMessage.getPayload().command().toString());
                    droneLogDetailsCommandAck.setParam2(commandAckMavlinkMessage.getPayload().result().toString());
                    droneLogDetailsCommandAck.setParam3("0");
                    droneLogDetailsCommandAck.setParam4("0");
                    droneLogDetailsCommandAck.setParam5("0");
                    droneLogDetailsCommandAck.setParam6("0");
                    droneLogDetailsCommandAck.setParam7("0");
                    droneLogDetailsService.saveDroneLogDetails(droneLogDetailsCommandAck);
                }


            }


        } catch (Exception ioe) {

        } finally {

            try {
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("returnDrone");


        }
        return "return";
    }


    public String setHome() {
        MavlinkConnection connection = null;
        Socket socket = null;
        try {
            socket = new Socket(tcpServerHost, tcpServerPort);

            connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));


            MavlinkMessage message;
            String TerrianReportMessage = null;
            int index = 0;
            String currentHeight = null;
            float currentHeightFloat = 0;
            //돌아가기
            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_DO_SET_HOME).param1(0).param2(0).param3(0).param4(0).param5(37.4556f).param6(126.8963f).param7(22.012743f).build(), linkId, timestamp, secretKey);


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

    public String pauseOrPlay(int pauseOrPlay) {
        try {
            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));

            Heartbeat heartbeat = null;
            //1 play 0 pause
            connection.send2(systemId, componentId, new CommandInt.Builder().command(MavCmd.MAV_CMD_DO_PAUSE_CONTINUE)
                    .param1(pauseOrPlay).param2(0).param3(0).param4(0).x(0).y(0).z(0).frame(MavFrame.MAV_FRAME_GLOBAL_INT).build());


            DroneLogDetails droneLogDetailsDoPauseContinue = new DroneLogDetails();
            droneLogDetailsDoPauseContinue.setDroneLog(droneLog);
            droneLogDetailsDoPauseContinue.setFromTarget("gcs");
            droneLogDetailsDoPauseContinue.setToTarget("drone");
            droneLogDetailsDoPauseContinue.setType("MAV_CMD_DO_PAUSE_CONTINUE");
            droneLogDetailsDoPauseContinue.setParam1(Integer.toString(pauseOrPlay));
            droneLogDetailsDoPauseContinue.setParam2("0");
            droneLogDetailsDoPauseContinue.setParam3("0");
            droneLogDetailsDoPauseContinue.setParam4("0");
            droneLogDetailsDoPauseContinue.setParam5("0");
            droneLogDetailsDoPauseContinue.setParam6("0");
            droneLogDetailsDoPauseContinue.setParam7("0");
            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsDoPauseContinue);

            MavlinkMessage message;
            while ((message = connection.next()) != null) {

                if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {      //x,y,z
                    MavlinkMessage<GlobalPositionInt> globalPositionIntMavlinkMessage = (MavlinkMessage<GlobalPositionInt>) message;
                    int x = globalPositionIntMavlinkMessage.getPayload().lat();
                    int y = globalPositionIntMavlinkMessage.getPayload().lon();
                    int z = globalPositionIntMavlinkMessage.getPayload().alt();
                    int heading = globalPositionIntMavlinkMessage.getPayload().hdg();

                    gps.setGpsX((double) y / 10000000);
                    gps.setGpsY((double) x / 10000000);
                    gps.setCurrentHeight((double) z / 1000);
                    gps.setHeading(heading / 100);

                } else if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {//wpdist
                    MavlinkMessage<NavControllerOutput> navControllerOutputMavlinkMessage = (MavlinkMessage<NavControllerOutput>) message;

                    int wpDist = navControllerOutputMavlinkMessage.getPayload().wpDist();

                    gps.setWpDist(wpDist);

                } else if (message.getPayload() instanceof Statustext) {        //statusMessage

                    MavlinkMessage<Statustext> statustextMavlinkMessage = (MavlinkMessage<Statustext>) message;


                    String missionText = statustextMavlinkMessage.getPayload().text();
                    log.info(missionText);
                    gps.setMissionType(missionText);

                    if (missionText.equals("Paused mission")) {
                        log.info("break");
                        break;
                    } else if (missionText.equals("Resumed mission")) {
                        log.info("break");
                        break;
                    }


                } else if (message.getPayload() instanceof Heartbeat) {     //heartbaet
                    MavlinkMessage<Heartbeat> heartbeatMavlinkMessage = (MavlinkMessage<Heartbeat>) message;
                    heartbeat = Heartbeat.builder().autopilot(heartbeatMavlinkMessage.getPayload().autopilot())
                            .type(heartbeatMavlinkMessage.getPayload().type())
                            .systemStatus(heartbeatMavlinkMessage.getPayload().systemStatus())
                            .baseMode()
                            .mavlinkVersion(heartbeatMavlinkMessage.getPayload().mavlinkVersion())
                            .build();
                    connection.send2(systemId, componentId, heartbeat, linkId, timestamp, secretKey);
                } else if (message.getPayload() instanceof CommandAck) {
                    MavlinkMessage<CommandAck> commandAckMavlinkMessage = (MavlinkMessage<CommandAck>) message;
                    log.info("commandAck={}", message);
                    DroneLogDetails droneLogDetailsCommandAck = new DroneLogDetails();
                    droneLogDetailsCommandAck.setDroneLog(droneLog);
                    droneLogDetailsCommandAck.setFromTarget("drone");
                    droneLogDetailsCommandAck.setToTarget("gcs");
                    droneLogDetailsCommandAck.setType("CommandAck");
                    droneLogDetailsCommandAck.setParam1(commandAckMavlinkMessage.getPayload().command().toString());
                    droneLogDetailsCommandAck.setParam2(commandAckMavlinkMessage.getPayload().result().toString());
                    droneLogDetailsCommandAck.setParam3("0");
                    droneLogDetailsCommandAck.setParam4("0");
                    droneLogDetailsCommandAck.setParam5("0");
                    droneLogDetailsCommandAck.setParam6("0");
                    droneLogDetailsCommandAck.setParam7("0");
                    droneLogDetailsService.saveDroneLogDetails(droneLogDetailsCommandAck);
                }
            }


        } catch (Exception ioe) {
            ioe.printStackTrace();

        } finally {


            System.out.println("pause or play");


        }
        return "stop";
    }


    public MavlinkConnection changeSpeedDrone(int speed) {
        MavlinkConnection connection = null;
        try (Socket socket = new Socket(tcpServerHost, tcpServerPort)) {

            connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));

            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_DO_CHANGE_SPEED).param1(0).param2(speed).param3(-1).param4(0).build(), linkId, timestamp, secretKey);

            DroneLogDetails droneLogDetailsChangeSpeed = new DroneLogDetails();
            droneLogDetailsChangeSpeed.setDroneLog(droneLog);
            droneLogDetailsChangeSpeed.setFromTarget("gcs");
            droneLogDetailsChangeSpeed.setToTarget("drone");
            droneLogDetailsChangeSpeed.setType("MAV_CMD_DO_CHANGE_SPEED");
            droneLogDetailsChangeSpeed.setParam1("0");
            droneLogDetailsChangeSpeed.setParam2(Integer.toString(speed));
            droneLogDetailsChangeSpeed.setParam3("-1");
            droneLogDetailsChangeSpeed.setParam4("0");
            droneLogDetailsChangeSpeed.setParam5("0");
            droneLogDetailsChangeSpeed.setParam6("0");
            droneLogDetailsChangeSpeed.setParam7("0");
            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsChangeSpeed);

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


    public String doMission(HashMap<String, MissionItemInt> missionItemMap, int maxFlag, HashMap<String, Integer> speeds
            , HashMap<String, Float> yaws, HashMap<Integer, String> missionIndex) {
        //       connection = null;
        //       socket = null;
        log.info("speeds={}", speeds); //speeds={return5=0, waypoint4=4, waypoint3=6, waypoint2=8}
        missionIndex.forEach((r, r2) -> {
            log.info("missionIndex={}:{}", r, r2);
        });
        try {
            //     socket = new Socket(tcpServerHost, tcpServerPort);
            //     connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
            //      Gps gps = new Gps();
            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timeBootMs = 0;
            long minTimeBootMs = 0;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));

            MavlinkMessage message;

            MissionCount count = MissionCount.builder().count(maxFlag).targetComponent(1).targetSystem(1).missionType(MavMissionType.MAV_MISSION_TYPE_MISSION).build();
            connection.send2(systemId, componentId, count, linkId, timestamp, secretKey);

            DroneLogDetails droneLogDetailsMissionCount = new DroneLogDetails();
            droneLogDetailsMissionCount.setDroneLog(droneLog);
            droneLogDetailsMissionCount.setFromTarget("drone");
            droneLogDetailsMissionCount.setToTarget("gcs");
            droneLogDetailsMissionCount.setType("MissionCount");
            droneLogDetailsMissionCount.setParam1(Integer.toString(maxFlag));
            droneLogDetailsMissionCount.setParam2("0");
            droneLogDetailsMissionCount.setParam3("0");
            droneLogDetailsMissionCount.setParam4("0");
            droneLogDetailsMissionCount.setParam5("0");
            droneLogDetailsMissionCount.setParam6("0");
            droneLogDetailsMissionCount.setParam7("0");
            droneLogDetailsService.saveDroneLogDetails(droneLogDetailsMissionCount);
            //    connection.send2(systemId, componentId, count);

            //  Heartbeat heartbeat = Heartbeat.builder().autopilot(MavAutopilot.MAV_AUTOPILOT_GENERIC).type(MavType.MAV_TYPE_GENERIC).systemStatus(MavState.MAV_STATE_UNINIT).baseMode().mavlinkVersion(3).build();
            Heartbeat heartbeat = null;


            //    Gson gson = new Gson();
//            t = new Timer();
//
//            t.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    simpMessagingTemplate.convertAndSend("/topic/log", gson.toJson(gps));
//                }
//            }, 0, 2000);
            int flag = 0;
            while ((message = connection.next()) != null) {

                //  log.info("messageCount");
                //if(message.getPayload().getClass().getName().contains("MavlinkMessage")){
                //     if(message.getPayload() instanceof MissionAck){


                if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {      //x,y,z
                    MavlinkMessage<GlobalPositionInt> globalPositionIntMavlinkMessage = (MavlinkMessage<GlobalPositionInt>) message;
                    int x = globalPositionIntMavlinkMessage.getPayload().lat();
                    int y = globalPositionIntMavlinkMessage.getPayload().lon();
                    int z = globalPositionIntMavlinkMessage.getPayload().alt();
                    int heading = globalPositionIntMavlinkMessage.getPayload().hdg();

                    gps.setGpsX((double) y / 10000000);
                    gps.setGpsY((double) x / 10000000);
                    gps.setCurrentHeight((double) z / 1000);
                    gps.setHeading(heading / 100);

                } else if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {//wpdist
                    MavlinkMessage<NavControllerOutput> navControllerOutputMavlinkMessage = (MavlinkMessage<NavControllerOutput>) message;

                    int wpDist = navControllerOutputMavlinkMessage.getPayload().wpDist();

                    gps.setWpDist(wpDist);

                } else if (message.getPayload().getClass().getName().contains("VfrHud")) {
                    MavlinkMessage<VfrHud> vfrHudMavlinkMessage = (MavlinkMessage<VfrHud>) message;

                    float airSpeed = vfrHudMavlinkMessage.getPayload().airspeed();
                    gps.setAirSpeed(Float.parseFloat(String.format("%.1f", airSpeed)));
                } else if (message.getPayload().getClass().getName().contains("Attitude")) {    //time
                    MavlinkMessage<Attitude> attitudeMavlinkMessage = (MavlinkMessage<Attitude>) message;

                    timeBootMs = attitudeMavlinkMessage.getPayload().timeBootMs();

                    if (timeBootMs <= minTimeBootMs || minTimeBootMs == 0) {
                        minTimeBootMs = timeBootMs;
                    }

                } else if (message.getPayload() instanceof Statustext) {        //statusMessage


                    log.info(message.toString());


                    MavlinkMessage<Statustext> statustextMavlinkMessage = (MavlinkMessage<Statustext>) message;
                    String missionText = statustextMavlinkMessage.getPayload().text();
                    log.info(missionText);
                    String missionNumber = missionText.substring(9, 10);
                    if (missionText.contains("Mission")) {

                        if (missionText.contains("RTL"))
                            gps.setMissionType("return");
                        else {

                            gps.setMissionType(missionNumber);
                        }


                        int speed = speeds.getOrDefault(missionIndex.get(Integer.parseInt(missionNumber)), 0);
                        float yaw = yaws.getOrDefault(missionIndex.get(Integer.parseInt(missionNumber)), 0f);
                        if (speed != 0) {
                            connection.send2(systemId, componentId, new CommandLong.Builder().
                                    command(MavCmd.MAV_CMD_DO_CHANGE_SPEED)
                                    .param1(0)
                                    .param2(speed)
                                    .param3(-1)
                                    .param4(0)
                                    .build(), linkId, timestamp, secretKey);
                        }
                        if (yaw != 0) {
                            connection.send2(systemId, componentId, new CommandLong.Builder().
                                    command(MavCmd.MAV_CMD_CONDITION_YAW)
                                    .param1(yaw)
                                    .param2(0)
                                    .param3(1)
                                    .param4(0)
                                    .build(), linkId, timestamp, secretKey);
                        }


                    }

                    if (missionText.equals("Disarming motors")) {
                        //gps.setMissionType("mission end");
                        gps.setMissionType("end");
                        gps.setStatus(0);
                        Thread.sleep(2000);
                        break;
                    }


                    if (missionText.contains("WP")) {
                        //      speed = speeds.getOrDefault(missionIndex.get(step), 0);
                    }


                } else if (message.getPayload() instanceof Heartbeat) {     //heartbaet
                    MavlinkMessage<Heartbeat> heartbeatMavlinkMessage = (MavlinkMessage<Heartbeat>) message;
                    heartbeat = Heartbeat.builder().autopilot(heartbeatMavlinkMessage.getPayload().autopilot())
                            .type(heartbeatMavlinkMessage.getPayload().type())
                            .systemStatus(heartbeatMavlinkMessage.getPayload().systemStatus())
                            .baseMode()
                            .mavlinkVersion(heartbeatMavlinkMessage.getPayload().mavlinkVersion())
                            .build();


                    connection.send2(systemId, componentId, heartbeat, linkId, timestamp, secretKey);
                } else if (message.getPayload() instanceof CommandAck) {
                    MavlinkMessage<CommandAck> commandAckMavlinkMessage = (MavlinkMessage<CommandAck>) message;
                    log.info("commandAck={}", message);
                    DroneLogDetails droneLogDetailsCommandAck = new DroneLogDetails();
                    droneLogDetailsCommandAck.setDroneLog(droneLog);
                    droneLogDetailsCommandAck.setFromTarget("drone");
                    droneLogDetailsCommandAck.setToTarget("gcs");
                    droneLogDetailsCommandAck.setType("CommandAck");
                    droneLogDetailsCommandAck.setParam1(commandAckMavlinkMessage.getPayload().command().toString());
                    droneLogDetailsCommandAck.setParam2(commandAckMavlinkMessage.getPayload().result().toString());
                    droneLogDetailsCommandAck.setParam3("0");
                    droneLogDetailsCommandAck.setParam4("0");
                    droneLogDetailsCommandAck.setParam5("0");
                    droneLogDetailsCommandAck.setParam6("0");
                    droneLogDetailsCommandAck.setParam7("0");
                    droneLogDetailsService.saveDroneLogDetails(droneLogDetailsCommandAck);
                }

                while (message.getPayload() instanceof MissionRequest && flag <= maxFlag - 1) {     //missioncount
                    // log.info("찾았다{}", missionItemMap.get("missionItemInt" + flag));
                    log.info("loginfo={}", message.getPayload());
                    log.info("maxFlag={}", maxFlag);
                    log.debug("flag={}", flag);
                    connection.send2(systemId, componentId, missionItemMap.get("missionItemInt" + flag), linkId, timestamp, secretKey);
                    MissionItemInt missionItemInt = missionItemMap.get("missionItemInt" + flag);
                    DroneLogDetails droneLogDetailsMissionRequest = new DroneLogDetails();
                    droneLogDetailsMissionRequest.setDroneLog(droneLog);
                    droneLogDetailsMissionRequest.setFromTarget("gcs");
                    droneLogDetailsMissionRequest.setToTarget("drone");
                    droneLogDetailsMissionRequest.setType("missionItemInt" + missionItemInt.command());
                    droneLogDetailsMissionRequest.setParam1(Float.toString(missionItemInt.param1()));
                    droneLogDetailsMissionRequest.setParam2(Float.toString(missionItemInt.param2()));
                    droneLogDetailsMissionRequest.setParam3(Float.toString(missionItemInt.param3()));
                    droneLogDetailsMissionRequest.setParam4(Float.toString(missionItemInt.param4()));
                    droneLogDetailsMissionRequest.setParam5(Integer.toString(missionItemInt.x()));
                    droneLogDetailsMissionRequest.setParam6(Integer.toString(missionItemInt.y()));
                    droneLogDetailsMissionRequest.setParam7(Float.toString(missionItemInt.z()));
                    droneLogDetailsService.saveDroneLogDetails(droneLogDetailsMissionRequest);


                    flag++;
                    if (message.getPayload() instanceof MissionRequest && flag == maxFlag) {    //changemode
                        log.info("미션등록");
                        //param2 automode
                        connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_DO_SET_MODE)
                                .param1(1).param2(3).build(), linkId, timestamp, secretKey);

                        DroneLogDetails droneLogDetailsSetMode = new DroneLogDetails();
                        droneLogDetailsSetMode.setDroneLog(droneLog);
                        droneLogDetailsSetMode.setFromTarget("gcs");
                        droneLogDetailsSetMode.setToTarget("drone");
                        droneLogDetailsSetMode.setType("MAV_CMD_DO_SET_MODE");
                        droneLogDetailsSetMode.setParam1("1");
                        droneLogDetailsSetMode.setParam2("3");
                        droneLogDetailsSetMode.setParam3("0");
                        droneLogDetailsSetMode.setParam4("0");
                        droneLogDetailsSetMode.setParam5("0");
                        droneLogDetailsSetMode.setParam6("0");
                        droneLogDetailsSetMode.setParam7("0");
                        droneLogDetailsService.saveDroneLogDetails(droneLogDetailsSetMode);
                        flag++;
                    }


                }


            }

        } catch (Exception ioe) {
            ioe.printStackTrace();


        } finally {
            System.out.println("Mission");
//            t.purge();
            tt.cancel();
            t.cancel();

            try {
                socket.close();
                connection = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return "stop";
    }


    public String heartBeat() {
        MavlinkConnection connection = null;
        Socket socket = null;
        try {
            socket = new Socket(tcpServerHost, tcpServerPort);
            connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            int flag = 0;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));

            MavlinkMessage message;


            Heartbeat heartbeat = Heartbeat.builder().autopilot(MavAutopilot.MAV_AUTOPILOT_GENERIC).type(MavType.MAV_TYPE_GENERIC).systemStatus(MavState.MAV_STATE_UNINIT).baseMode().mavlinkVersion(3).build();

            connection.send2(systemId, componentId, heartbeat);

            while ((message = connection.next()) != null) {
                if (message instanceof Mavlink2Message) {
                    Mavlink2Message message2 = (Mavlink2Message) message;
                    if (message2.getPayload().getClass().getName().contains("SysStatus") ||//battery voltage배터리  //battery_remaining 배터리
                            message2.getPayload().getClass().getName().contains("PowerStatus") || //payload=PowerStatus{vcc=5000, vservo=0, flags=EnumValue{value=0, entry=null}}}
                            message2.getPayload().getClass().getName().contains("NavControllerOutput") || //wpdist 목적지와의 거리
                            message2.getPayload().getClass().getName().contains("MissionCurrent") || //payload=MissionCurrent{seq=0}
                            message2.getPayload().getClass().getName().contains("GlobalPositionInt") || //payload=MissionCurrent{seq=0}
                            message2.getPayload().getClass().getName().contains("ServoOutputRaw") || //ServoOutputRaw{timeUsec=2772547204, port=0, servo1Raw=1591, servo2Raw=1591, servo3Raw=1590, servo4Raw=1591, servo5Raw=0, servo6Raw=0, servo7Raw=0, servo8Raw=0, servo9Raw=0, servo10Raw=0, servo11Raw=0, servo12Raw=0, servo13Raw=0, servo14Raw=0, servo15Raw=0, servo16Raw=0}
                            message2.getPayload().getClass().getName().contains("SensorOffsets") || //ServoOutputRaw{timeUsec=2772547204, port=0, servo1Raw=1591, servo2Raw=1591, servo3Raw=1590, servo4Raw=1591, servo5Raw=0, servo6Raw=0, servo7Raw=0, servo8Raw=0, servo9Raw=0, servo10Raw=0, servo11Raw=0, servo12Raw=0, servo13Raw=0, servo14Raw=0, servo15Raw=0, servo16Raw=0}
                            message2.getPayload().getClass().getName().contains("RcChannels") || //RcChannels{timeBootMs=2637547, chancount=16, chan1Raw=1500, chan2Raw=1500, chan3Raw=1000, chan4Raw=1500, chan5Raw=1800, chan6Raw=1000, chan7Raw=1000, chan8Raw=1800, chan9Raw=0, chan10Raw=0, chan11Raw=0, chan12Raw=0, chan13Raw=0, chan14Raw=0, chan15Raw=0, chan16Raw=0, chan17Raw=0, chan18Raw=0, rssi=255}}
                            message2.getPayload().getClass().getName().contains("RawImu") || //RawImu{timeUsec=2582049267, xacc=0, yacc=1, zacc=-997, xgyro=4, ygyro=3, zgyro=2, xmag=151, ymag=258, zmag=413, id=0, temperature=4499}
                            message2.getPayload().getClass().getName().contains("ScaledImu2") || //payload=ScaledImu2{timeBootMs=3097547, xacc=4, yacc=0, zacc=-1001, xgyro=-1, ygyro=0, zgyro=0, xmag=119, ymag=274, zmag=413, temperature=4499}
                            message2.getPayload().getClass().getName().contains("ScaledImu3") || message2.getPayload().getClass().getName().contains("ScaledPressure") || message2.getPayload().getClass().getName().contains("ScaledPressure2") || message2.getPayload().getClass().getName().contains("GpsRawInt") || //lat=374456473, lon=1268953303, alt=18230, eph=121, epv=200, vel=0, cog=3988, satellitesVisible=10, altEllipsoid=0, hAcc=300, vAcc=300, velAcc=40, hdgAcc=0
                            message2.getPayload().getClass().getName().contains("SystemTime") || message2.getPayload().getClass().getName().contains("TerrainReport") ||//TerrainReport{lat=374433470, lon=1268897507, spacing=100, terrainHeight=14.5117655, currentHeight=100.431244, pending=0, loaded=504}}
                            message2.getPayload().getClass().getName().contains("LocalPositionNed") || //LocalPositionNed{timeBootMs=12024498, x=-0.07747139, y=0.061710242, z=0.001238235, vx=-0.013305673, vy=-1.9261298E-4, vz=4.191259E-4}}
                            message2.getPayload().getClass().getName().contains("Vibration") || //Vibration{timeUsec=14555498804, vibrationX=0.0026672243, vibrationY=0.0027407336, vibrationZ=0.0027245833, clipping0=0, clipping1=0, clipping2=0}}
                            message2.getPayload().getClass().getName().contains("BatteryStatus") || //batteryFunction=EnumValue{value=0, entry=MAV_BATTERY_FUNCTION_UNKNOWN}, type=EnumValue{value=0, entry=MAV_BATTERY_TYPE_UNKNOWN}, temperature=32767, voltages=[12587, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535], currentBattery=0, currentConsumed=151475, energyConsumed=68546, batteryRemaining=0, timeRemaining=0, chargeState=EnumValue{value=1, entry=MAV_BATTERY_CHARGE_STATE_OK}}}
                            message2.getPayload().getClass().getName().contains("Attitude") || //=Attitude{timeBootMs=11556094, roll=0.0018187475, pitch=6.057985E-4, yaw=2.2014248, rollspeed=2.1905173E-4, pitchspeed=2.5810348E-4, yawspeed=7.619873E-4}}
                            message2.getPayload().getClass().getName().contains("VfrHud") || // payload=VfrHud{airspeed=3.3680003, groundspeed=3.021782, heading=37, throttle=34, alt=99.979996, climb=0.015121608}}
                            // payload=VfrHud{airspeed=3.3680003, groundspeed=3.021782, heading=37, throttle=34, alt=99.979996, climb=0.015121608}}

                            message2.getPayload().getClass().getName().contains("Meminfo") || //payload=Meminfo{brkval=0, freemem=65535, freemem32=131072}}payload=Meminfo{brkval=0, freemem=65535, freemem32=131072}}

                            message2.getPayload().getClass().getName().contains("Ahrs") || //Ahrs{omegaix=-0.0025094047, omegaiy=-0.0025298656, omegaiz=-0.0020406283, accelWeight=0.0, renormVal=0.0, errorRp=0.002322554, errorYaw=0.0013488759}}
                            message2.getPayload().getClass().getName().contains("Hwstatus") || // payload=Hwstatus{vcc=5000, i2cerr=0}}
                            message2.getPayload().getClass().getName().contains("MountStatus") || //x
                            message2.getPayload().getClass().getName().contains("EkfStatusReport") || //velocityVariance=0.021335527, posHorizVariance=0.025864147, posVertVariance=0.0017659252, compassVariance=0.035943523, terrainAltVariance=0.0, airspeedVariance=0.0}}
                            message2.getPayload().getClass().getName().contains("Simstate") || //Simstate{roll=5.885804E-4, pitch=-5.997561E-7, yaw=-1.0545728, xacc=-0.09285733, yacc=-0.050651476, zacc=-9.81958, xgyro=-0.0067729917, ygyro=-0.0050521465, zgyro=3.3953006E-4, lat=374456475, lng=1268953310}
                            message2.getPayload().getClass().getName().contains("Ahrs2") || //x
                            message2.getPayload().getClass().getName().contains("Timesync") || message2.getPayload().getClass().getName().contains("ParamValue") || //x
                            message2.getPayload().getClass().getName().contains("PositionTargetGlobalInt") || //x
                            message2.getPayload().getClass().getName().contains("EscTelemetry1To4") //EscTelemetry1To4{temperature=[B@553a3d88, voltage=[0, 0, 0, 0], current=[0, 0, 0, 0], totalcurrent=[0, 0, 0, 0], rpm=[0, 0, 0, 0], count=[0, 0, 0, 0]}
                        //여기서부터 내가작성
                    ) {
                    } else {
                        log.info(message2.toString());

                    }
                }
            }

        } catch (Exception ioe) {
            System.out.println(ioe);
        } finally {
            System.out.println("holddrone");


        }
        return "stop";
    }


    public String logTest() {
        MavlinkConnection connection = null;
        Socket socket = null;
        try {
            socket = new Socket(tcpServerHost, tcpServerPort);
            connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());

            int systemId = 0;
            int componentId = 0;
            int linkId = 1;
            int flag = 0;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));

            MavlinkMessage message;

            LogRequestList logRequestList = LogRequestList.builder().targetSystem(0).targetComponent(0).start(0).end(3).build();//로그리스트를 받아옴
            LogRequestData logRequestData = LogRequestData.builder().targetComponent(0).targetSystem(0).id(1).count(90).ofs(0).build();

            connection.send2(systemId, componentId, logRequestData);

            while ((message = connection.next()) != null) {
                if (message instanceof Mavlink2Message) {
                    Mavlink2Message message2 = (Mavlink2Message) message;
                    if (message2.getPayload().getClass().getName().contains("SysStatus") ||//battery voltage배터리  //battery_remaining 배터리
                            message2.getPayload().getClass().getName().contains("PowerStatus") || //payload=PowerStatus{vcc=5000, vservo=0, flags=EnumValue{value=0, entry=null}}}
                            message2.getPayload().getClass().getName().contains("NavControllerOutput") || //wpdist 목적지와의 거리
                            message2.getPayload().getClass().getName().contains("MissionCurrent") || //payload=MissionCurrent{seq=0}
                            message2.getPayload().getClass().getName().contains("GlobalPositionInt") || //payload=MissionCurrent{seq=0}
                            message2.getPayload().getClass().getName().contains("ServoOutputRaw") || //ServoOutputRaw{timeUsec=2772547204, port=0, servo1Raw=1591, servo2Raw=1591, servo3Raw=1590, servo4Raw=1591, servo5Raw=0, servo6Raw=0, servo7Raw=0, servo8Raw=0, servo9Raw=0, servo10Raw=0, servo11Raw=0, servo12Raw=0, servo13Raw=0, servo14Raw=0, servo15Raw=0, servo16Raw=0}
                            message2.getPayload().getClass().getName().contains("SensorOffsets") || //ServoOutputRaw{timeUsec=2772547204, port=0, servo1Raw=1591, servo2Raw=1591, servo3Raw=1590, servo4Raw=1591, servo5Raw=0, servo6Raw=0, servo7Raw=0, servo8Raw=0, servo9Raw=0, servo10Raw=0, servo11Raw=0, servo12Raw=0, servo13Raw=0, servo14Raw=0, servo15Raw=0, servo16Raw=0}
                            message2.getPayload().getClass().getName().contains("RcChannels") || //RcChannels{timeBootMs=2637547, chancount=16, chan1Raw=1500, chan2Raw=1500, chan3Raw=1000, chan4Raw=1500, chan5Raw=1800, chan6Raw=1000, chan7Raw=1000, chan8Raw=1800, chan9Raw=0, chan10Raw=0, chan11Raw=0, chan12Raw=0, chan13Raw=0, chan14Raw=0, chan15Raw=0, chan16Raw=0, chan17Raw=0, chan18Raw=0, rssi=255}}
                            message2.getPayload().getClass().getName().contains("RawImu") || //RawImu{timeUsec=2582049267, xacc=0, yacc=1, zacc=-997, xgyro=4, ygyro=3, zgyro=2, xmag=151, ymag=258, zmag=413, id=0, temperature=4499}
                            message2.getPayload().getClass().getName().contains("ScaledImu2") || //payload=ScaledImu2{timeBootMs=3097547, xacc=4, yacc=0, zacc=-1001, xgyro=-1, ygyro=0, zgyro=0, xmag=119, ymag=274, zmag=413, temperature=4499}
                            message2.getPayload().getClass().getName().contains("ScaledImu3") || message2.getPayload().getClass().getName().contains("ScaledPressure") || message2.getPayload().getClass().getName().contains("ScaledPressure2") || message2.getPayload().getClass().getName().contains("GpsRawInt") || //lat=374456473, lon=1268953303, alt=18230, eph=121, epv=200, vel=0, cog=3988, satellitesVisible=10, altEllipsoid=0, hAcc=300, vAcc=300, velAcc=40, hdgAcc=0
                            message2.getPayload().getClass().getName().contains("SystemTime") || message2.getPayload().getClass().getName().contains("TerrainReport") ||//TerrainReport{lat=374433470, lon=1268897507, spacing=100, terrainHeight=14.5117655, currentHeight=100.431244, pending=0, loaded=504}}
                            message2.getPayload().getClass().getName().contains("LocalPositionNed") || //LocalPositionNed{timeBootMs=12024498, x=-0.07747139, y=0.061710242, z=0.001238235, vx=-0.013305673, vy=-1.9261298E-4, vz=4.191259E-4}}
                            message2.getPayload().getClass().getName().contains("Vibration") || //Vibration{timeUsec=14555498804, vibrationX=0.0026672243, vibrationY=0.0027407336, vibrationZ=0.0027245833, clipping0=0, clipping1=0, clipping2=0}}
                            message2.getPayload().getClass().getName().contains("BatteryStatus") || //batteryFunction=EnumValue{value=0, entry=MAV_BATTERY_FUNCTION_UNKNOWN}, type=EnumValue{value=0, entry=MAV_BATTERY_TYPE_UNKNOWN}, temperature=32767, voltages=[12587, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535], currentBattery=0, currentConsumed=151475, energyConsumed=68546, batteryRemaining=0, timeRemaining=0, chargeState=EnumValue{value=1, entry=MAV_BATTERY_CHARGE_STATE_OK}}}
                            message2.getPayload().getClass().getName().contains("Attitude") || //=Attitude{timeBootMs=11556094, roll=0.0018187475, pitch=6.057985E-4, yaw=2.2014248, rollspeed=2.1905173E-4, pitchspeed=2.5810348E-4, yawspeed=7.619873E-4}}
                            message2.getPayload().getClass().getName().contains("VfrHud") || // payload=VfrHud{airspeed=3.3680003, groundspeed=3.021782, heading=37, throttle=34, alt=99.979996, climb=0.015121608}}
                            // payload=VfrHud{airspeed=3.3680003, groundspeed=3.021782, heading=37, throttle=34, alt=99.979996, climb=0.015121608}}

                            message2.getPayload().getClass().getName().contains("Meminfo") || //payload=Meminfo{brkval=0, freemem=65535, freemem32=131072}}payload=Meminfo{brkval=0, freemem=65535, freemem32=131072}}
                            message2.getPayload().getClass().getName().contains("Heartbeat") || //payload=Meminfo{brkval=0, freemem=65535, freemem32=131072}}payload=Meminfo{brkval=0, freemem=65535, freemem32=131072}}

                            message2.getPayload().getClass().getName().contains("Ahrs") || //Ahrs{omegaix=-0.0025094047, omegaiy=-0.0025298656, omegaiz=-0.0020406283, accelWeight=0.0, renormVal=0.0, errorRp=0.002322554, errorYaw=0.0013488759}}
                            message2.getPayload().getClass().getName().contains("Hwstatus") || // payload=Hwstatus{vcc=5000, i2cerr=0}}

                            message2.getPayload().getClass().getName().contains("EkfStatusReport") || //velocityVariance=0.021335527, posHorizVariance=0.025864147, posVertVariance=0.0017659252, compassVariance=0.035943523, terrainAltVariance=0.0, airspeedVariance=0.0}}
                            message2.getPayload().getClass().getName().contains("Simstate") || //Simstate{roll=5.885804E-4, pitch=-5.997561E-7, yaw=-1.0545728, xacc=-0.09285733, yacc=-0.050651476, zacc=-9.81958, xgyro=-0.0067729917, ygyro=-0.0050521465, zgyro=3.3953006E-4, lat=374456475, lng=1268953310}
                            message2.getPayload().getClass().getName().contains("Ahrs2") || //x
                            message2.getPayload().getClass().getName().contains("Timesync") || message2.getPayload().getClass().getName().contains("ParamValue") || //x
                            message2.getPayload().getClass().getName().contains("PositionTargetGlobalInt") || //x
                            message2.getPayload().getClass().getName().contains("EscTelemetry1To4") //EscTelemetry1To4{temperature=[B@553a3d88, voltage=[0, 0, 0, 0], current=[0, 0, 0, 0], totalcurrent=[0, 0, 0, 0], rpm=[0, 0, 0, 0], count=[0, 0, 0, 0]}
                        //여기서부터 내가작성
                    ) {
                    } else {
                        log.info(message2.toString());
                        if (message.getPayload() instanceof LogData) {
                            MavlinkMessage<LogData> logData = (MavlinkMessage<LogData>) message;

                            String data = new String(logData.getPayload().data());
                            log.info("{}", logData.getPayload().data());
                            System.out.println(data);
                        }


                    }
                }
            }

        } catch (Exception ioe) {
            System.out.println(ioe);
        } finally {
            System.out.println("logTest");


        }
        return "stop";
    }

    public void changeYaw(int yaw) {
        try {
            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timeBootMs = 0;
            long minTimeBootMs = 0;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            byte[] secretKey = new byte[0];

            secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));


            MavlinkMessage message;


            connection.send2(systemId, componentId, new CommandLong.Builder().
                    command(MavCmd.MAV_CMD_CONDITION_YAW)
                    .param1(yaw)
                    .param2(0)
                    .param3(1)
                    .param4(0)
                    .build(), linkId, timestamp, secretKey);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}




