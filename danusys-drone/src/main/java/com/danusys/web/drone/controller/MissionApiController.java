package com.danusys.web.drone.controller;


import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.model.MissionDetails;
import com.danusys.web.drone.service.MissionDetailsService;
import com.danusys.web.drone.service.MissionService;
import com.danusys.web.drone.utils.Substring;
import com.google.gson.Gson;
import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

import com.danusys.web.drone.utils.Flight;

@RestController
@RequestMapping("/drone/api")
@Slf4j
@RequiredArgsConstructor
public class MissionApiController {


    @Value("${tcp.server.host}")
    private String tcpServerHost;

    @Value("${tcp.server.port}")
    private int tcpServerPort;
    private final MissionService missionService;
    private final MissionDetailsService missionDetailsService;
    private final Flight flight;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Substring substring;

    @GetMapping("/return")
    public void returnDrone() {
        flight.returnDrone();
    }


    @GetMapping("/takeoff")
    public void takeOffDrone() {

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
            HashMap<String, MissionItemInt> missionItemMap = new HashMap<>();


//            connection.send2(systemId, componentId, new CommandLong.Builder()
//                    .command(MavCmd.MAV_CMD_DO_SET_MODE)
//                    .param1(1)
//                    .param2(4)
//                    .build(), linkId, timestamp, secretKey);
//
//            connection.send2(systemId, componentId, new CommandLong.Builder()
//                    .command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM)
//                    .param1(1)
//                    .param2(0)
//                    .build(), linkId, timestamp, secretKey);
//
//
//            connection.send2(systemId, componentId, new CommandLong.Builder()
//                    .command(MavCmd.MAV_CMD_NAV_TAKEOFF)
//                    .param1(15).param2(0).param3(0).param4(0).param5(0).param6(0).param7(100)
//                    .build(), linkId, timestamp, secretKey);


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
                    if (message2.getPayload() instanceof HomePosition) {
                        MavlinkMessage<HomePosition> homePositionMavlinkMessage = (MavlinkMessage<HomePosition>) message;
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
                        float takeoff=terrainReportMavlinkMessage.getPayload().currentHeight();
                        if(takeoff >100-1.5){
                            break;
                        }
                    }

                }
            }//end while
            MissionItemInt missionItemInt1=new MissionItemInt.Builder()
                    .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                    .param1(0)
                    .param2(0)
                    .param3(0)
                    .param4(0)
                    .x(374416740)
                    .y(1269000701)
                    .z(80)
                    .seq(1)
                    .targetComponent(0)
                    .targetSystem(0)
                    .current(0)
                    .autocontinue(1)
                    .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                    .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
//                                .build(), linkId, timestamp, secretKey);
                    .build();

            missionItemMap.put("missionItemInt1",missionItemInt1);

            MissionItemInt missionItemInt2=new MissionItemInt.Builder()
                    .command(MavCmd.MAV_CMD_NAV_RETURN_TO_LAUNCH)
                    .seq(2)
                    .targetComponent(0)
                    .targetSystem(0)
                    .current(0)
                    .autocontinue(1)
                   // .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                    .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
//                                .build(), linkId, timestamp, secretKey);
                    .build();

            missionItemMap.put("missionItemInt2",missionItemInt2);

            //automode


          //  flight.loiter(missionItemMap);




        } catch (Exception ioe) {


        } finally {
            System.out.println("holddrone");


        }

    }


    @MessageMapping("/droneinfo")
    public void droneInfo() {
        String missionResult = null;
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            missionResult = flight.droneInfo();
            log.info(missionResult);
        }
        while (missionResult.equals("onemore"));

    }


    @MessageMapping("/startmission")
    @SendTo("/topic/startmission")
    public void startMission(Mission mission) {


        Mission missionResponse = missionService.missionResponseList2(mission.getId());
        int i = 2;
        int step = 1;
        int flag= 0;
        HashMap<Integer, String> missionIndex = new HashMap<>();
        HashMap<String, Integer> gpsXs = new HashMap<>();
        HashMap<String, Integer> gpsYs = new HashMap<>();
        HashMap<String, Integer> gpsZs = new HashMap<>();
        HashMap<String, Integer> speeds = new HashMap<>();
        HashMap<String, String> result = new HashMap<>();
        HashMap<String, Integer> times = new HashMap<>();
        HashMap<String,MissionItemInt> missionMap =new HashMap<>();
        Iterator iterator = missionResponse.getMissonDetails().iterator();
        while (iterator.hasNext()) {
            MissionDetails missionDetails = (MissionDetails) iterator.next();
            if (missionDetails.getName().equals("takeoff")) {

                gpsZs.put("takeoff", missionDetails.getAlt());
                missionIndex.put(missionDetails.getIndex(), "takeoff");


            } else if (missionDetails.getName().equals("waypoint")) {
                //각 waypoint에 값 받아와서 넣기
                missionIndex.put(missionDetails.getIndex(), "waypoint" + missionDetails.getIndex());
                gpsXs.put("waypoint" + missionDetails.getIndex(), missionDetails.getGpsX());
                speeds.put("waypoint" + missionDetails.getIndex(), missionDetails.getSpeed());
                gpsYs.put("waypoint" + missionDetails.getIndex(), missionDetails.getGpsY());
                gpsZs.put("waypoint" + missionDetails.getIndex(), missionDetails.getAlt());
            } else if (missionDetails.getName().equals("loiter")) {
                missionIndex.put(missionDetails.getIndex(), "loiter" + missionDetails.getIndex());
                times.put("loiter" + missionDetails.getIndex(), missionDetails.getTime());

            } else if (missionDetails.getName().equals("return")) {

                missionIndex.put(missionDetails.getIndex(), "return");

                gpsXs.put("return" + missionDetails.getIndex(), missionDetails.getGpsX());
                speeds.put("return" + missionDetails.getIndex(), missionDetails.getSpeed());
                gpsYs.put("return" + missionDetails.getIndex(), missionDetails.getGpsY());
                gpsZs.put("return" + missionDetails.getIndex(), missionDetails.getAlt());

            }
        }

        float takeOffAlt = gpsZs.get(missionIndex.get(missionDetailsService.findByNameAndMission("takeoff", mission).getIndex()));

        //float takeOffAlt = gpsZs.get(missionIndex.get(1));


        while (!missionIndex.getOrDefault(step, "finish").equals("finish")) {
            int x = 0;
            int y = 0;
            int z = 0;
            int time = 0;
            int speed = 0;

            log.info("step={}", step);

            x = gpsXs.getOrDefault(missionIndex.get(step), 0);
            y = gpsYs.getOrDefault(missionIndex.get(step), 0);
            z = gpsZs.getOrDefault(missionIndex.get(step), 0);
            time = times.getOrDefault(missionIndex.get(step), 0);
            speed = speeds.getOrDefault(missionIndex.get(step), 0);

            if (missionIndex.getOrDefault(step, "finish").equals("takeoff")) {

//                String missionResult = null;
//                log.info("takeoffalt={}", takeOffAlt);
//                do {
//                    //missionResult = flight.takeoff(takeOffAlt);
//                }
//                while (missionResult.equals("onemore"));
                missionMap=flight.missionTakeoff();
                flag++;

            } else if (missionIndex.getOrDefault(step, "finish").contains("waypoint")) {

//                String missionResult = null;
//                do {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    missionResult = flight.wayPoint(x, y, z, speed);
//                }
//                while (missionResult.equals("onemore"));
                log.info("x={},y={},z{}",x,y,z);
                MissionItemInt missionItemInt = new MissionItemInt.Builder()
                        .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                        .param1(0)
                        .param2(0)
                        .param3(0)
                        .param4(0)
                        .x(x)
                        .y(y)
                        .z(z)
                        .seq(flag)
                        .targetComponent(1)
                        .targetSystem(1)
                        .current(0)
                        .autocontinue(1)
                        .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                        .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
//                                .build(), linkId, timestamp, secretKey);
                        .build();
                missionMap.put("missionItemInt"+flag,missionItemInt);
                flag++;

            } else if (missionIndex.getOrDefault(step, "finish").contains("loiter")) {
                String missionResult = null;
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                //    missionResult = flight.loiter(time);
                    log.info(missionResult);
                }
                while (missionResult.equals("onemore"));

            } else if (missionIndex.getOrDefault(step, "finish").contains("return")) {

                MissionItemInt missionItemInt = new MissionItemInt.Builder()
                        .command(MavCmd.MAV_CMD_NAV_RETURN_TO_LAUNCH)
                        .seq(flag)
                        .targetComponent(0)
                        .targetSystem(0)
                        .current(0)
                        .autocontinue(1)
                        .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                        .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
//                                .build(), linkId, timestamp, secretKey);
                        .build();
                missionMap.put("missionItemInt"+flag,missionItemInt);
                flag++;




//                String missionResult = null;
//                do {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    missionResult = flight.returnDrone();
//                    log.info(missionResult);
//                }
//                while (missionResult.equals("onemore"));
            } else if (missionIndex.getOrDefault(step, "finish").contains("finish")) {
                break;
            }
            step++;


        }
        log.info("들어감");
        flight.doMission(missionMap,flag);


    }

//    public void startMission(Mission mission) {
//
//        Mission missionResponse = missionService.missionResponseList(mission.getId());
//        int i = 2;
//        int step = 1;
//        HashMap<Integer, String> missionIndex = new HashMap<>();
//        HashMap<String, Float> gpsXs = new HashMap<>();
//        HashMap<String, Float> gpsYs = new HashMap<>();
//        HashMap<String, Float> gpsZs = new HashMap<>();
//        HashMap<String, Float> speeds = new HashMap<>();
//        HashMap<String, String> result = new HashMap<>();
//        HashMap<String, Float> times =new HashMap<>();
//        Iterator iterator = missionResponse.getMissonDetails().iterator();
//        while (iterator.hasNext()) {
//            MissionDetails missionDetails = (MissionDetails) iterator.next();
//            if (missionDetails.getName().equals("takeoff")) {
//
//                gpsZs.put("takeoff", missionDetails.getAlt());
//                missionIndex.put(missionDetails.getIndex(), "takeoff");
//
//
//            } else if (missionDetails.getName().equals("waypoint")) {
//                missionIndex.put(missionDetails.getIndex(), "waypoint" + missionDetails.getIndex());
//
//                gpsXs.put("waypoint" + missionDetails.getIndex(), missionDetails.getGpsX());
//                speeds.put("waypoint" + missionDetails.getIndex(), missionDetails.getSpeed());
//                gpsYs.put("waypoint" + missionDetails.getIndex(), missionDetails.getGpsY());
//                gpsZs.put("waypoint" + missionDetails.getIndex(), missionDetails.getAlt());
//            } else if (missionDetails.getName().equals("loiter")) {
//                missionIndex.put(missionDetails.getIndex(), "loiter" + missionDetails.getIndex());
//                times.put("loiter"+missionDetails.getIndex(),missionDetails.getTime());
//
//            } else if (missionDetails.getName().equals("return")) {
//
//                missionIndex.put(missionDetails.getIndex(), "return");
//
//                gpsXs.put("return" + missionDetails.getIndex(), missionDetails.getGpsX());
//                speeds.put("return" + missionDetails.getIndex(), missionDetails.getSpeed());
//                gpsYs.put("return" + missionDetails.getIndex(), missionDetails.getGpsY());
//                gpsZs.put("return" + missionDetails.getIndex(), missionDetails.getAlt());
//
//            }
//        }
//
//        float takeOffAlt = gpsZs.get(missionIndex.get(missionDetailsService.findByNameAndMission("takeoff", mission).getIndex()));
//
//        //float takeOffAlt = gpsZs.get(missionIndex.get(1));
//
//
//        while (!missionIndex.getOrDefault(step, "finish").equals("finish")) {
//            float x = 0;
//            float y = 0;
//            float z = 0;
//            float time=0;
//            float speed = 0;
//
//            log.info("{}", step);
//
//            x = gpsXs.getOrDefault(missionIndex.get(step), 0f);
//            y = gpsYs.getOrDefault(missionIndex.get(step), 0f);
//            z = gpsZs.getOrDefault(missionIndex.get(step), 0f);
//            time=times.getOrDefault(missionIndex.get(step),0f);
//            speed = speeds.getOrDefault(missionIndex.get(step), 0f);
//
//            if (missionIndex.getOrDefault(step, "finish").equals("takeoff")) {
//
//                String missionResult = null;
//                log.info("takeoffalt={}", takeOffAlt);
//                do {
//                    missionResult = flight.takeoff(takeOffAlt);
//                }
//                while (missionResult.equals("onemore"));
//
//            }
//
//
//            if (missionIndex.getOrDefault(step, "finish").contains("waypoint")) {
//
//                String missionResult = null;
//                do {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    missionResult = flight.wayPoint(x, y, z, speed);
//                }
//                while (missionResult.equals("onemore"));
//            } else if (missionIndex.getOrDefault(step, "finish").contains("loiter")) {
//                String missionResult = null;
//                do {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    missionResult = flight.loiter(time);
//                    log.info(missionResult);
//                }
//                while (missionResult.equals("onemore"));
//
//            } else if (missionIndex.getOrDefault(step, "finish").contains("return")) {
//                String missionResult = null;
//                do {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    missionResult = flight.returnDrone();
//                    log.info(missionResult);
//                }
//                while (missionResult.equals("onemore"));
//            } else if (missionIndex.getOrDefault(step, "finish").contains("finish")) {
//                break;
//            }
//            step++;
//
//
//        }
//
//
////        if (!missionIndex.get(2).isEmpty()) {
////            String missionResult=null;
////            float x=gpsXs.get(missionIndex.get(step));
////            float y=gpsXs.get(missionIndex.get(step));
////            float z=gpsXs.get(missionIndex.get(step));
////            float speed=speeds.get(missionIndex.get(step));
////            do{
////                try {
////                    Thread.sleep(1000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////                missionResult= flight.wayPoint(x,y,z,speed);
////            }
////            while (missionResult.equals("onemore"));
////
////
////        }
////
////        if (missionIndex.get(4).equals("return")) {
////            String missionResult=null;
////            do{
////                try {
////                    Thread.sleep(1000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////                missionResult=flight.returnDrone();
////                log.info(missionResult);
////            }
////            while (missionResult.equals("onemore"));
////
////        }
////
////      //  this.simpMessagingTemplate.convertAndSend("/topic/drone3", gson.toJson(result));
////
//
//
//    }


    @GetMapping("/loiter")
    public void loiter() {
        //      flight.setHome();
        String missionResult = null;
        do {
            missionResult = flight.returnDrone();
            log.info(missionResult);
        }
        while (missionResult.equals("onemore"));
//        flight.takeoff(100);
//        flight.wayPoint(37.4476f,126.89591f,100f,5);
//        flight.loiter(30);
//        flight.wayPoint(37.4567f,126.89597f,100f,5);


    }


    @GetMapping("/test")
    public void test() {
       // flight.loiter(30);
        //flight.camera();
       flight.returnDrone();
    }

    @GetMapping("/test2")
    public void test2() {
        // flight.loiter(30);
        flight.camera();
       // flight.returnDrone();
    }

    @GetMapping("/test3")
    public void test3() {
        // flight.loiter(30);
        flight.heartBeat();

        // flight.returnDrone();
    }
    @GetMapping("/test4")
    public void test4() {
        // flight.loiter(30);
        flight.gimbal();
        // flight.returnDrone();
    }
}



