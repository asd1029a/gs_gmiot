package com.danusys.web.drone.utils;



import com.danusys.web.drone.socket.CustomServerSocket;
import com.danusys.web.drone.dto.response.ArmDisArm;
import com.danusys.web.drone.dto.response.Gps;
import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneLog;
import com.danusys.web.drone.model.DroneLogDetails;
import com.danusys.web.drone.service.ConnectionService;
import com.danusys.web.drone.service.DroneLogDetailsService;
import com.danusys.web.drone.service.DroneService;
import com.danusys.web.drone.service.FlightManager;
import com.danusys.web.drone.socket.ServerThread;
import com.google.gson.Gson;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Service
@Slf4j
@RequiredArgsConstructor
public class Flight {


    private final CustomServerSocket ServerSocket;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DroneLogDetailsService droneLogDetailsService;
    private final DroneService droneService;
    private final ConnectionService connectionService;
    private final FlightManager flightManager;
    private MavlinkConnection connection = null;
    private Gps gps = new Gps();
    private Gps wayPointGps = new Gps();
    private Gson gson = new Gson();

    private DroneLog droneLog = null;
    private int sec = 0;
    private int min = 0;
    private int hour = 0;
    private String stringSeconds = null;
    private String stringMinutes = null;
    private String stringHours = null;

    private Timer t = null;
    private TimerTask tt = null;
    private Timer waypointTimer = null;
    private TimerTask waypointTimerTask = null;

    private int EOFCheck = 0;
    private boolean isEnd = false;
    private boolean isPauseOrStopEnd = false;
    private boolean alreadyDo = false;
    private boolean alreadyWayPoint = false;
    private boolean isMissionAndDrone = false;
    private boolean isFirstArming = true;
    private boolean isFirstTimer = true;
    private int isArm = 0;
    private boolean isStarted = false;
    private boolean isReturn = false;

    private int flightHeight=1000;

    private int maxFlag = 0;
    private int flag = 0;
    private HashMap<String, MissionItemInt> missionItemMap = new HashMap<>();
    private boolean setTimer = false;
    private boolean istakeoffMissionItemMap = false;
    private boolean isTakeOffEnd = false;
    private Map <Integer,Object> systemIdConnectionMap=null;
    public HashMap<String, MissionItemInt> missionTakeoff(DroneLog inputDroneLog, int droneId) {
        log.info("startMissionTakeOff");
        isStarted = true;

        //시간 초기화
        //마지막에 추가됨
        droneLog = inputDroneLog;
        isMissionAndDrone = true;
        int systemId = 0;
        int componentId = 0;
        int linkId = 1;
        long timestamp = System.currentTimeMillis();/* provide microsecond time */
        ;
        byte[] secretKey = new byte[0];
        if (alreadyDo) {
            try {
                secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));
                CommandLong doSetModeCommandLong = new CommandLong.Builder().command(MavCmd.MAV_CMD_DO_SET_MODE)
                        .param1(1).param2(3).build();
                connection.send2(systemId, componentId, doSetModeCommandLong, linkId, timestamp, secretKey);

                DroneLogDetails droneLogDetailsSetMode = new DroneLogDetails();
                writeLog(droneLogDetailsSetMode, droneLog, "gcs", "drone", "MAV_CMD_DO_SET_MODE",
                        "1", "3", "0", "0", "0", "0", "0");

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        log.info("isArm={}", isArm);
        if (!alreadyDo && isArm != -1) {



            int index = -1;
            try {

                Heartbeat heartbeat = null;
                MavlinkMessage message;


                connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_GET_HOME_POSITION).build(), linkId, timestamp, secretKey);
                DroneLogDetails droneLogDetailsHomePosition = new DroneLogDetails();
                writeLog(droneLogDetailsHomePosition, droneLog, "gcs", "drone", "MAV_CMD_GET_HOME_POSITION", "0", "0"
                        , "0", "0", "0", "0", "0");
                while (!istakeoffMissionItemMap) {

                }
                gps.setStatus(1);
                log.info("break");

                //4 guided mode
                //new command


                /**
                 * takeoff -> 450 m 로 고정하고 특정 높이가 됬을때 미션 수행하도록 logging에 설정 되있음
                 */
                CommandLong takeoffCommandLong = new CommandLong.Builder().command(MavCmd.MAV_CMD_NAV_TAKEOFF).
                        param1(15).param2(0).param3(0).param4(0).param5(0).param6(0).param7(450).build();

                connection.send2(systemId, componentId, takeoffCommandLong, linkId, timestamp, secretKey);
                DroneLogDetails droneLogDetailsTakeOff = new DroneLogDetails();

                writeLog(droneLogDetailsTakeOff, droneLog, "gcs", "drone", "MAV_CMD_NAV_TAKEOFF", "15", "0",
                        "0", "0", "0", "0", "450");


            } catch (EOFException e) {
                EOFCheck = 1;
            } catch (SocketException e) {
                EOFCheck = 1;
            } catch (Exception ioe) {
                ioe.printStackTrace();
            } finally {

                log.info("endtakeoff");


            }

        }
        return missionItemMap;
    }
    //x,y 반대로 넣어야되기떄문에
    public String wayPoint(int gpsY, int gpsX, int gpsZ, int yaw) {

        isMissionAndDrone = false;
        log.info("yaw={}", yaw);
        if (gpsZ == 0)
            gpsZ = 100;

        Gson gson = new Gson();

        gps.setStatus(1);
        if (!alreadyWayPoint) {
            waypointTimerTask = new TimerTask() {
                @Override
                public void run() {
                    wayPointGps.setGpsX(gps.getGpsX());
                    wayPointGps.setGpsY(gps.getGpsY());
                    wayPointGps.setCurrentHeight(gps.getCurrentHeight());
                    wayPointGps.setWpDist(gps.getWpDist());
                    wayPointGps.setHeading(gps.getHeading());
                    wayPointGps.setAirSpeed(gps.getAirSpeed());
                    wayPointGps.setSec(gps.getSec());
                    wayPointGps.setMin(gps.getMin());
                    wayPointGps.setHour(gps.getHour());
                    wayPointGps.setDroneId(gps.getDroneId());
                    wayPointGps.setStatus(gps.getStatus());
                    wayPointGps.setMissionType("waypoint");
                    simpMessagingTemplate.convertAndSend("/topic/waypoint", gson.toJson(wayPointGps));

                }
            };
            waypointTimer = new Timer();

            waypointTimer.schedule(waypointTimerTask, 0, 1000);

        }
        alreadyWayPoint = true;

        try {

            int systemId = 0;
            int componentId = 0;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));

            MavlinkMessage message;
            log.info("x={},y={}", gpsX, gpsY);

            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_DO_SET_MODE).param1(1).param2(4).build(), linkId, timestamp, secretKey);
            DroneLogDetails droneLogDetailsDoSetMode2 = new DroneLogDetails();
            writeLog(droneLogDetailsDoSetMode2, droneLog, "gcs", "drone", "MAV_CMD_DO_SET_MODE", "1", "4", "0"
                    , "0", "0", "0", "0");

            connection.send2(systemId, componentId, new MissionItemInt.Builder().command(MavCmd.MAV_CMD_NAV_WAYPOINT).param1(0)
                    .targetSystem(0).targetComponent(0).seq(0).current(2).autocontinue(1).frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                    .x(gpsX)
                    .y(gpsY)
                    .z(gpsZ).build(), linkId, timestamp, secretKey);
            DroneLogDetails droneLogDetailsWayPoint = new DroneLogDetails();
            writeLog(droneLogDetailsWayPoint, droneLog, "gcs", "drone", "MAV_CMD_NAV_WAYPOINT", "0", "0", "0"
                    , "0", Integer.toString(gpsX), Integer.toString(gpsY), Integer.toString(gpsZ));

            connection.send2(systemId, componentId, new CommandLong.Builder().
                    command(MavCmd.MAV_CMD_CONDITION_YAW)
                    .param1(yaw)
                    .param2(0)
                    .param3(1)
                    .param4(0)
                    .build(), linkId, timestamp, secretKey);
            DroneLogDetails droneLogDetailsConditionYaw = new DroneLogDetails();
            writeLog(droneLogDetailsConditionYaw, droneLog, "gcs", "drone", "MAV_CMD_CONDITION_YAW", Integer.toString(yaw),
                    "0", "1", "0", "0", "0", "0");

            Heartbeat heartbeat = null;


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
                    int z = globalPositionIntMavlinkMessage.getPayload().relativeAlt();
                    int heading = globalPositionIntMavlinkMessage.getPayload().hdg();

                    gps.setGpsX((double) y / 10000000);
                    gps.setGpsY((double) x / 10000000);
                    gps.setCurrentHeight((double) z / 1000);
                    gps.setHeading(heading / 100);

                }else if (message.getPayload().getClass().getName().contains("VfrHud")) {
                    MavlinkMessage<VfrHud> vfrHudMavlinkMessage = (MavlinkMessage<VfrHud>) message;

                    float airSpeed = vfrHudMavlinkMessage.getPayload().airspeed();
                    gps.setAirSpeed(Float.parseFloat(String.format("%.1f", airSpeed)));

                } else if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {//wpdist
                    MavlinkMessage<NavControllerOutput> navControllerOutputMavlinkMessage = (MavlinkMessage<NavControllerOutput>) message;

                    int wpDist = navControllerOutputMavlinkMessage.getPayload().wpDist();

                    if (isMissionAndDrone)
                        break;
                    gps.setWpDist(wpDist);
                    if (wpDist == 0) {
                        break;
                    }

                } else if (message.getPayload() instanceof Statustext) {        //statusMessage


                    MavlinkMessage<Statustext> statustextMavlinkMessage = (MavlinkMessage<Statustext>) message;


                    String missionText = statustextMavlinkMessage.getPayload().text();
                    log.info("waypointMessage={}", missionText);

                    wayPointGps.setMissionType("waypoint");

                } else if (message.getPayload() instanceof CommandAck) {
                    MavlinkMessage<CommandAck> commandAckMavlinkMessage = (MavlinkMessage<CommandAck>) message;
                    log.info("commandAck={}", message);
                    DroneLogDetails droneLogDetailsCommandAck = new DroneLogDetails();
                    writeLog(droneLogDetailsCommandAck, droneLog, "drone", "gcs", "CommandAck", commandAckMavlinkMessage.getPayload().command().toString(),
                            commandAckMavlinkMessage.getPayload().result().toString(), "0", "0", "0", "0", "0");
                }

            }


        } catch (EOFException e) {
            EOFCheck = 1;
        } catch (SocketException e) {
            EOFCheck = 1;
        } catch (Exception ioe) {


        } finally {
//
            alreadyWayPoint = false;
            waypointTimer.cancel();
            waypointTimerTask.cancel();

            wayPointGps.setMissionType("end");
            simpMessagingTemplate.convertAndSend("/topic/waypoint", gson.toJson(wayPointGps));
            System.out.println("wayPoint");


        }
        return "end";
    }


    public String returnDrone() {
        log.info("startReturnDrone");
        try {
            isReturn = true;
            isMissionAndDrone = true;
            int systemId = 0;
            int componentId = 0;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));
            gps.setMissionType("return");
            MavlinkMessage message;

            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_NAV_RETURN_TO_LAUNCH).build(), linkId, timestamp, secretKey);

            DroneLogDetails droneLogDetailsReturnToLaunch = new DroneLogDetails();
            writeLog(droneLogDetailsReturnToLaunch, droneLog, "gcs", "drone", "MAV_CMD_NAV_RETURN_TO_LAUNCH", "0",
                    "0", "0", "0", "0", "0", "0");

            Heartbeat heartbeat = null;

            log.info("isEnd={}", isEnd);


        } catch (EOFException e) {
            EOFCheck = 1;
        } catch (SocketException e) {
            EOFCheck = 1;
        } catch (Exception ioe) {

        } finally {
            log.info("returnDroneTimerOut");

            isStarted = false;
            alreadyWayPoint = false;
            System.out.println("returnDrone");
            return "return";
        }

    }

    public String pauseOrPlay(int pauseOrPlay) {
        try {

            int systemId = 0;
            int componentId = 0;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */

            byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));
            if (pauseOrPlay == 0)
                gps.setStatus(2);
            else if (pauseOrPlay == 1)
                gps.setStatus(1);
            Heartbeat heartbeat = null;
            //1 play 0 pause

            MavlinkMessage message;

            connection.send2(systemId, componentId, new CommandInt.Builder().command(MavCmd.MAV_CMD_DO_PAUSE_CONTINUE)
                    .param1(pauseOrPlay).param2(0).param3(0).param4(0).x(0).y(0).z(0).frame(MavFrame.MAV_FRAME_GLOBAL_INT).build());
            DroneLogDetails droneLogDetailsDoPauseContinue = new DroneLogDetails();
            writeLog(droneLogDetailsDoPauseContinue, droneLog, "gcs", "drone", "MAV_CMD_DO_PAUSE_CONTINUE",
                    Integer.toString(pauseOrPlay), "0", "0", "0", "0", "0", "0");

            while ((message = connection.next()) != null) {
                if (isPauseOrStopEnd)
                    break;
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

                    //TODO statusMessage Logging 따로 level하고 합쳐서
                    String missionText = statustextMavlinkMessage.getPayload().text();
                    log.info("pauseOrPlay={}", missionText);
                    //  gps.setMissionType(missionText);

                    if (missionText.equals("Paused mission")) {
                        log.info("break");
                        isPauseOrStopEnd = true;
                        break;
                    } else if (missionText.equals("Resumed mission")) {
                        log.info("break");
                        isPauseOrStopEnd = true;
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
                    writeLog(droneLogDetailsCommandAck, droneLog, "drone", "gcs", "CommandAck",
                            commandAckMavlinkMessage.getPayload().command().toString(), commandAckMavlinkMessage.getPayload().result().toString(),
                            "0", "0", "0", "0", "0");
                }
            }


        } catch (Exception ioe) {
            ioe.printStackTrace();

        } finally {


            System.out.println("pause or play");


        }
        return "stop";
    }



    public String doMission(HashMap<String, MissionItemInt> missionItemMap, int maxFlag, HashMap<String, Integer> speeds
            , HashMap<String, Float> yaws, HashMap<Integer, String> missionIndex) {


            try {
                this.maxFlag = maxFlag;
                this.missionItemMap = missionItemMap;

                alreadyDo = true;
                int systemId = 0;
                int componentId = 0;
                int linkId = 1;
                long timeBootMs = 0;
                long minTimeBootMs = 0;
                long timestamp = System.currentTimeMillis();/* provide microsecond time */
                byte[] secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));

                MavlinkMessage message;

                    MissionCount count = MissionCount.builder().count(maxFlag).targetComponent(0).targetSystem(0).missionType(MavMissionType.MAV_MISSION_TYPE_MISSION).build();
                    connection.send2(systemId, componentId, count, linkId, timestamp, secretKey);

                DroneLogDetails droneLogDetailsMissionCount = new DroneLogDetails();
                writeLog(droneLogDetailsMissionCount, droneLog, "drone", "gcs", "MissionCount",
                        Integer.toString(maxFlag), "0",
                        "0", "0", "0", "0", "0");

                Heartbeat heartbeat = null;



            } catch (EOFException e) {
                EOFCheck = 1;
            } catch (SocketException e) {
                EOFCheck = 1;
            } catch (Exception ioe) {
                ioe.printStackTrace();


            } finally {
                System.out.println("Mission");
                alreadyDo = false;
                if (!isReturn) {

                    sec = 0;
                    min = 0;
                    hour = 0;
                    stringSeconds = null;
                    stringMinutes = null;
                    stringHours = null;
                    isStarted = false;

                }

                isReturn = false;
                isFirstTimer = true;


            }
        if (!isReturn) {
            isStarted = false;

        }
        isReturn = false;
        return "no";
    }

    public void changeYaw(int yaw) {
        try {
            int systemId = 0;
            int componentId = 0;
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
            DroneLogDetails droneLogDetailsChangeYaw = new DroneLogDetails();
            writeLog(droneLogDetailsChangeYaw, droneLog, "gcs", "drone", "MAV_CMD_CONDITION_YAW",
                    Integer.toString(yaw), "0", "1", "0", "0", "0", "0");
        } catch (EOFException e) {
            EOFCheck = 1;
        } catch (SocketException e) {
            EOFCheck = 1;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void setMissionCurrent(int seq) {


        try {
            int systemId = 0;
            int componentId = 0;
            int linkId = 1;
            long timeBootMs = 0;
            long minTimeBootMs = 0;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            byte[] secretKey = new byte[0];
            secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));
            connection.send2(systemId, componentId, new CommandLong.Builder().
                    command(MavCmd.MAV_CMD_DO_SET_MISSION_CURRENT)
                    .param1(seq)
                    .build(), linkId, timestamp, secretKey);
            DroneLogDetails droneLogDetailsDoSetMissionCurrent = new DroneLogDetails();
            writeLog(droneLogDetailsDoSetMissionCurrent, droneLog, "gcs", "drone", "MAV_CMD_DO_SET_MISSION_CURRENT",
                    Integer.toString(seq), "0", "0", "0", "0", "0", "0");
        } catch (EOFException e) {
            EOFCheck = 1;
        } catch (SocketException e) {
            EOFCheck = 1;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void writeLog(DroneLogDetails droneLogDetails, DroneLog inputDroneLog, String fromTarget, String toTarget, String type, String param1,
                          String param2, String param3, String param4, String param5, String param6, String param7) {

        droneLogDetails.setDroneLog(inputDroneLog);
        droneLogDetails.setFromTarget(fromTarget);
        droneLogDetails.setToTarget(toTarget);
        droneLogDetails.setType(type);
        droneLogDetails.setParam1(param1);
        droneLogDetails.setParam2(param2);
        droneLogDetails.setParam3(param3);
        droneLogDetails.setParam4(param4);
        droneLogDetails.setParam5(param5);
        droneLogDetails.setParam6(param6);
        droneLogDetails.setParam7(param7);
        droneLogDetailsService.saveDroneLogDetails(droneLogDetails);
    }

    /**
     * 작성자 :엄태혁 연구원
     * mode : 3 -> auto 4-> guided
     *
     * @param
     */

    public void armDisarm(int armDisarm, int droneId) {
        log.info("startArmDisArm");
        try {
            int systemId = 0;
            int componentId = 0;
            int linkId = 1;
            long timeBootMs = 0;
            long minTimeBootMs = 0;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            byte[] secretKey = new byte[0];

            secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));

            Socket socket = null;

            HashMap<Integer, Socket> socketList = ServerSocket.getServerThread().getSocketList();
            //log.info("socketIndex={}", index);
            int index = -1;


            Drone searchDrone = new Drone();
            searchDrone.setId(Long.valueOf(droneId));
            Drone findDrone = droneService.findDrone(searchDrone);
            index = findDrone.getSocketIndex().intValue();
            if (isFirstArming) {
                socket = socketList.get(index);
                connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
                isFirstArming = false;
            }
            MavlinkMessage message;
            //arm 1 disarm 0
            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_DO_SET_MODE).param1(1).param2(4).build(), linkId, timestamp, secretKey);
            DroneLogDetails droneLogDetailsDoSetMode = new DroneLogDetails();
            droneLogDetailsDoSetMode.setDroneLog(droneLog);

            writeLog(droneLogDetailsDoSetMode, droneLog, "gcs", "drone", "MAV_CMD_DO_SET_MODE", "1", "4", "0"
                    , "0", "0", "0", "0");
            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM).
                    param1(armDisarm).param2(0).build(), linkId, timestamp, secretKey);


            DroneLogDetails droneLogDetailsArmDisarm = new DroneLogDetails();
            writeLog(droneLogDetailsArmDisarm, droneLog, "gcs", "drone", "MAV_CMD_COMPONENT_ARM_DISARM", Integer.toString(armDisarm), "0", "0"
                    , "0", "0", "0", "0");

        } catch (EOFException e) {
            EOFCheck = 1;
        } catch (SocketException e) {
            EOFCheck = 1;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            log.info("Arm end");
        }


    }

    private Timer makeTimer(TimerTask tt) {
        Timer t = new Timer();
        t.schedule(tt, 0, 1000);
        return t;
    }

    private TimerTask setTimerTask() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                stringSeconds = Integer.toString(sec);
                stringMinutes = Integer.toString(min);
                stringHours = Integer.toString(hour);

                if (sec < 10) {
                    stringSeconds = "0" + stringSeconds;
                }
                if (min < 10) {
                    stringMinutes = "0" + stringMinutes;
                }
                if (hour < 10) {
                    stringHours = "0" + stringHours;
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


        return timerTask;
    }


    public void connect(int droneId) {
        Socket socket = null;
        HashMap<Integer, Socket> socketList = ServerSocket.getServerThread().getSocketList();
        //log.info("socketIndex={}", index);
        int index = -1;
        Drone searchDrone = new Drone();
        searchDrone.setId(Long.valueOf(droneId));
        Drone findDrone = droneService.findDrone(searchDrone);
        index = findDrone.getSocketIndex().intValue();
        Map<String,Object> socketMap=(Map<String, Object>) systemIdConnectionMap.get(index);
        socket=(Socket) socketMap.get("socket");

        try {
            connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getEOFCheck() {
        return this.EOFCheck;
    }
    public boolean getIsTakeOffEnd(){
        return this.isTakeOffEnd;
    }
    public void setEOFCheck(int EOFCheck) {
        this.EOFCheck = EOFCheck;
    }

    public void logging(int droneId,float takeOffAlt) {
        try {
            int systemId = 0;
            int componentId = 0;
            int linkId = 1;
            long timeBootMs = 0;
            long minTimeBootMs = 0;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            byte[] secretKey = new byte[0];
            Heartbeat heartbeat = null;
            secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));

            Socket socket = null;

            systemIdConnectionMap=ServerSocket.getServerThread().getSystemIdConnectionMap();
            log.info("systemIdConnectionMap={}",systemIdConnectionMap);
            //log.info("socketIndex={}", index);
            int index = -1;


            Drone searchDrone = new Drone();
            searchDrone.setId(Long.valueOf(droneId));
            Drone findDrone = droneService.findDrone(searchDrone);
            index = findDrone.getSocketIndex().intValue();
            if (isFirstArming) {

                Map<String,Object> socketMap=(Map<String, Object>) systemIdConnectionMap.get(index);
                socket=(Socket) socketMap.get("socket");
                connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
                //                droneLog = inputDroneLog;
                gps.setMissionType("0");
                gps.setStatus(1);
                gps.setDroneId(droneId);
                tt = setTimerTask();
                t = makeTimer(tt);
                isFirstArming = false;
            }


            MavlinkMessage message;
            //arm 1 disarm 0
            while ((message = connection.next()) != null) {

                if (message.getPayload().getClass().getName().contains("GlobalPositionInt")) {      //x,y,z
                    MavlinkMessage<GlobalPositionInt> globalPositionIntMavlinkMessage = (MavlinkMessage<GlobalPositionInt>) message;
                    int x = globalPositionIntMavlinkMessage.getPayload().lat();
                    int y = globalPositionIntMavlinkMessage.getPayload().lon();
                    int z = globalPositionIntMavlinkMessage.getPayload().relativeAlt();
                    int heading = globalPositionIntMavlinkMessage.getPayload().hdg();

                    gps.setGpsX((double) y / 10000000);
                    gps.setGpsY((double) x / 10000000);
                    gps.setCurrentHeight((double) z / 1000);
                    gps.setHeading(heading / 100);

                    if ((double) z / 1000 > takeOffAlt - 1.5) {
                        isTakeOffEnd = true;
                    }
                } else if (message.getPayload().getClass().getName().contains("NavControllerOutput")) {//wpdist
                    MavlinkMessage<NavControllerOutput> navControllerOutputMavlinkMessage = (MavlinkMessage<NavControllerOutput>) message;
                    int wpDist = navControllerOutputMavlinkMessage.getPayload().wpDist();
                    gps.setWpDist(wpDist);

                } else if (message.getPayload().getClass().getName().contains("VfrHud")) {
                    MavlinkMessage<VfrHud> vfrHudMavlinkMessage = (MavlinkMessage<VfrHud>) message;
//                    log.info("{}",vfrHudMavlinkMessage.getPayload().);
                    float airSpeed = vfrHudMavlinkMessage.getPayload().airspeed();
                    gps.setAirSpeed(Float.parseFloat(String.format("%.1f", airSpeed)));
                } else if (message.getPayload().getClass().getName().contains("Attitude")) {    //time
                    MavlinkMessage<Attitude> attitudeMavlinkMessage = (MavlinkMessage<Attitude>) message;

                    timeBootMs = attitudeMavlinkMessage.getPayload().timeBootMs();

                    if (timeBootMs <= minTimeBootMs || minTimeBootMs == 0) {
                        minTimeBootMs = timeBootMs;
                    }

                } else if (message.getPayload() instanceof Statustext) {        //statusMessage
                    MavlinkMessage<Statustext> statustextMavlinkMessage = (MavlinkMessage<Statustext>) message;
                    String missionText = statustextMavlinkMessage.getPayload().text();
                    ArmDisArm armDisArm = new ArmDisArm();
                    log.info("logginMessage={}", missionText);
                    String missionNumber = missionText.substring(9, 10);
                    if (missionText.contains("Mission")) {

                        if (missionText.contains("RTL"))
                            gps.setMissionType("return");
                        else {

                            gps.setMissionType(missionNumber);
                        }

                    }

                    if (missionText.equals("Paused mission")) {
                        isPauseOrStopEnd = true;

                    } else if (missionText.equals("Resumed mission")) {
                        isPauseOrStopEnd = true;
                    }
                    if (missionText.equals("Arming motors")) {
                        armDisArm.setArmDisarm(1);
                        armDisArm.setDroneId(droneId);
                        isArm = 1;
                        gps.setStatus(2);

                    }
                    if (missionText.equals("Disarming motors")) {

                        armDisArm.setArmDisarm(0);
                        armDisArm.setDroneId(droneId);
                        isArm = -1;
                        gps.setStatus(0);

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

                    writeLog(droneLogDetailsCommandAck, droneLog, "drone", "gcs", "CommandAck",
                            commandAckMavlinkMessage.getPayload().command().toString(), commandAckMavlinkMessage.getPayload().result().toString(),
                            "0", "0", "0", "0", "0");

                } else if (message.getPayload() instanceof HomePosition) {
                    MavlinkMessage<HomePosition> homePositionMavlinkMessage = (MavlinkMessage<HomePosition>) message;
                    //           log.info("home Position = {}", homePositionMavlinkMessage.getPayload());
                    int latitude = homePositionMavlinkMessage.getPayload().latitude();//x
                    int longitude = homePositionMavlinkMessage.getPayload().longitude();//y
                    int altitude = homePositionMavlinkMessage.getPayload().altitude();//z

                    MissionItemInt missionItemInt0 = new MissionItemInt.Builder().command(MavCmd.MAV_CMD_NAV_WAYPOINT).
                            param1(0).param2(0).param3(0).param4(0)
                            .x(latitude).y(longitude).z(altitude).seq(0)
                            .targetComponent(0).targetSystem(0).current(0).autocontinue(1)
                            .frame(MavFrame.MAV_FRAME_GLOBAL_INT).missionType(MavMissionType.MAV_MISSION_TYPE_MISSION).build();

                    missionItemMap.put("missionItemInt0", missionItemInt0);
                    istakeoffMissionItemMap = true;
                } else if (message.getPayload() instanceof MissionRequest) {
                    while (message.getPayload() instanceof MissionRequest && flag <= maxFlag - 1) {     //missioncount
                        // log.info("찾았다{}", missionItemMap.get("missionItemInt" + flag));
                        log.info("loginfo={}", message.getPayload());
                        log.info("maxFlag={}", maxFlag);
                        log.debug("flag={}", flag);

                        connection.send2(systemId, componentId, missionItemMap.get("missionItemInt" + flag), linkId, timestamp, secretKey);
                        MissionItemInt missionItemInt = missionItemMap.get("missionItemInt" + flag);
                        DroneLogDetails droneLogDetailsMissionRequest = new DroneLogDetails();
                        writeLog(droneLogDetailsMissionRequest, droneLog, "gcs", "drone", "missionItemInt",
                                Float.toString(missionItemInt.param1()), Float.toString(missionItemInt.param2()),
                                Float.toString(missionItemInt.param3()), Float.toString(missionItemInt.param4()),
                                Integer.toString(missionItemInt.x()), Integer.toString(missionItemInt.y()), Float.toString(missionItemInt.z()));

                        flag++;
                        if (message.getPayload() instanceof MissionRequest && flag == maxFlag) {    //changemode
                            log.info("미션등록");
                            //param2 automode

                            connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_DO_SET_MODE)
                                    .param1(1).param2(3).build(), linkId, timestamp, secretKey);
                            DroneLogDetails droneLogDetailsSetMode = new DroneLogDetails();
                            writeLog(droneLogDetailsSetMode, droneLog, "gcs", "drone", "MAV_CMD_DO_SET_MODE",
                                    "1", "3", "0", "0", "0", "0", "0");

                            flag++;
                        }
                    }
                }


            }


        } catch (EOFException e) {
            EOFCheck = 1;
        } catch (SocketException e) {
            EOFCheck = 1;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch(NullPointerException e){
            log.info("connection =null");
        }finally {
            if(t!=null)
            t.cancel();
            if(tt!=null)
            tt.cancel();
            flightHeight=1000;
            flag = 0;
            maxFlag = 0;
            sec = 0;
            min = 0;
            hour = 0;
            stringSeconds = null;
            stringMinutes = null;
            stringHours = null;
            isTakeOffEnd = false;
            connection =null;
            gps.setMissionType("end");
            gps.setStatus(0);
            simpMessagingTemplate.convertAndSend("/topic/log", gson.toJson(gps));
            isFirstArming = true;
            setTimer = false;
            missionItemMap = new HashMap<>();
        }
    }
}




