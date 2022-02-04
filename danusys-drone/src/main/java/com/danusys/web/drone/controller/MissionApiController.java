package com.danusys.web.drone.controller;


import com.danusys.web.drone.model.DroneLog;
import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.model.MissionDetails;
import com.danusys.web.drone.service.DroneDetailsService;
import com.danusys.web.drone.service.DroneLogService;
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
    private final DroneLogService droneLogService;


    @GetMapping("/return")
    public void returnDrone() {
        flight.returnDrone();
    }


    @GetMapping("/takeoff")
    public void takeOffDrone(int takeOffAlt) {

        flight.flightTakeoff(takeOffAlt);

    }

    @GetMapping("/waypoint")
    public void wayPointDrone(int gpsX, int gpsY, int gpsZ, int speed) {

        flight.wayPoint(gpsX, gpsY, gpsZ, speed);
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
        DroneLog inputDroneLog = new DroneLog();
        inputDroneLog.setMissionName(missionResponse.getName());
        DroneLog droneLog = droneLogService.saveDroneLog(inputDroneLog);
        int i = 2;
        int step = 1;
        int flag = 0;
        HashMap<Integer, String> missionIndex = new HashMap<>();
        HashMap<String, Integer> gpsXs = new HashMap<>();
        HashMap<String, Integer> gpsYs = new HashMap<>();
        HashMap<String, Integer> gpsZs = new HashMap<>();
        HashMap<String, Integer> speeds = new HashMap<>();
        HashMap<String, String> result = new HashMap<>();
        HashMap<String, Integer> times = new HashMap<>();
        HashMap<String, MissionItemInt> missionMap = new HashMap<>();
        HashMap<String, Integer> radiusMap =new HashMap<>();
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
                gpsXs.put("loiter" + missionDetails.getIndex(), missionDetails.getGpsX());
                gpsYs.put("loiter" + missionDetails.getIndex(), missionDetails.getGpsY());
                gpsZs.put("loiter" + missionDetails.getIndex(), missionDetails.getAlt());
                radiusMap.put("loiter" + missionDetails.getIndex(), missionDetails.getRadius());
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
            int radius=0;
            log.info("step={}", step);

            x = gpsXs.getOrDefault(missionIndex.get(step), 0);
            y = gpsYs.getOrDefault(missionIndex.get(step), 0);
            z = gpsZs.getOrDefault(missionIndex.get(step), 0);
            time = times.getOrDefault(missionIndex.get(step), 0);
            speed = speeds.getOrDefault(missionIndex.get(step), 0);

            if (missionIndex.getOrDefault(step, "finish").equals("takeoff")) {

                missionMap = flight.missionTakeoff(droneLog);
                flag++;

            } else if (missionIndex.getOrDefault(step, "finish").contains("waypoint")) {

//                log.info("x={},y={},z{}", x, y, z);
                MissionItemInt missionItemInt = new MissionItemInt.Builder()
                        .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                        .param1(0)
                        .param2(0)
                        .param3(0)
                        .param4(0)      //yaw
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
                        .build();
                missionMap.put("missionItemInt" + flag, missionItemInt);
                flag++;

            } else if (missionIndex.getOrDefault(step, "finish").contains("loiter")) {
                MissionItemInt missionItemInt = new MissionItemInt.Builder()
                        .command(MavCmd.MAV_CMD_NAV_LOITER_TIME)
                        .param1(10)
                        .param2(1)
                        .param3(50)
                        .param4(1)
                        .seq(flag)
                        .targetComponent(0)
                        .targetSystem(0)
                        .current(0)
                        .autocontinue(1)
                        .frame(MavFrame.MAV_FRAME_GLOBAL_INT)
                        .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
                        .build();
                missionMap.put("missionItemInt" + flag, missionItemInt);
                flag++;

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
                        .build();
                missionMap.put("missionItemInt" + flag, missionItemInt);
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
        flight.doMission(missionMap, flag);


    }

    @MessageMapping("/pause")
    @SendTo("/topic/pause")
    public void pause() {
        // flight.loiter(30);
        flight.pauseOrPlay(0);
        // flight.returnDrone();
    }

    @MessageMapping("/play")
    @SendTo("/topic/play")
    public void play() {
        // flight.loiter(30);
        flight.pauseOrPlay(1);
        // flight.returnDrone();
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

    @GetMapping("/logtest")
    public void logTest() {
        // flight.loiter(30);
        flight.logTest();

        // flight.returnDrone();
    }

}



