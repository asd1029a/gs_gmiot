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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.*;

import com.danusys.web.drone.utils.Flight;

@RestController
@RequestMapping("/drone/api")
@Slf4j
@RequiredArgsConstructor
public class MissionApiController {

    private final MissionService missionService;
    private final MissionDetailsService missionDetailsService;
    private final Flight flight;
    private final DroneLogService droneLogService;



/*

    url:/return
    do: 해당 드론 귀환
    param: paramMap{droneId}

 */

    @MessageMapping("/return")
    //@SendTo("/topic/return")
    public void returnDrone(Map<String, Object> paramMap) {
        int droneId = 0;
        if (paramMap.get("droneId") != null)
            droneId = Integer.parseInt(paramMap.get("droneId").toString());
        flight.returnDrone();
    }




    @GetMapping("/takeoff")
    public ResponseEntity<?> takeOffDrone(@RequestBody Map<String, Object> paramMap) {
        int takeOffAlt = 0;
        if (paramMap.get("alt") != null) {
            takeOffAlt = Integer.parseInt(paramMap.get("alt").toString());
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(flight.flightTakeoff(takeOffAlt));


    }

    //   @GetMapping("/waypoint")
    @MessageMapping("/waypoint")
    //@SendTo("/topic/waypoint")
//    public ResponseEntity<?> wayPointDrone(int gpsX, int gpsY, int gpsZ) {
    // public ResponseEntity<?> wayPointDrone(@RequestBody Map<String, Object> paramMap) {
    public void wayPointDrone(Map<String, Object> paramMap) {

        double gpsX = 0;
        double gpsY = 0;
        double gpsZ = 0;


        int intGpsX = 0;
        int intGpsY = 0;
        int intGpsZ = 0;
        int yaw = 0;
        int droneId=0;
        if (paramMap.get("droneId")!=null)
            droneId=Integer.parseInt(paramMap.get("droneId").toString());
        if (paramMap.get("gpsX") != null)
            gpsX = Double.parseDouble(paramMap.get("gpsX").toString()) * 10000000;
        if (paramMap.get("gpsY") != null)
            gpsY = Double.parseDouble(paramMap.get("gpsY").toString()) * 10000000;
        if (paramMap.get("alt") != null)
            gpsZ = Double.parseDouble(paramMap.get("alt").toString());
        if (paramMap.get("yaw") != null)
            yaw = Integer.parseInt(paramMap.get("yaw").toString());
        intGpsX = (int) gpsX * 10000000;
        intGpsY = (int) gpsY * 10000000;
        intGpsZ = (int) gpsZ;
        flight.wayPoint(intGpsX, intGpsY, intGpsZ, yaw);


    }

    @MessageMapping("/changeyaw")
//    @SendTo("/topic/changeyaw")
//    public ResponseEntity<?> wayPointDrone(int gpsX, int gpsY, int gpsZ) {
    // public ResponseEntity<?> wayPointDrone(@RequestBody Map<String, Object> paramMap) {
    public void changeYaw(@RequestBody Map<String, Object> paramMap) {

        int yaw=0;
        if(paramMap.get("yaw")!=null)
            yaw=Integer.parseInt(paramMap.get("yaw").toString());

            flight.changeYaw(yaw);
    }


    @MessageMapping("/startmission")
    @SendTo("/topic/startmission")
//    public void startMission(Mission mission) {
    public void startMission(@RequestBody Map<String, Object> paramMap) {

        long id = 0l;

        if (paramMap.get("id") != null)
            id = Long.parseLong(paramMap.get("id").toString());
        int droneId=0;
        if (paramMap.get("droneId")!=null)
            droneId=Integer.parseInt(paramMap.get("droneId").toString());
        Mission mission=new Mission();
        mission.setId(id);

        //로그 저장
      //  Mission missionResponse = missionService.missionResponseList2(mission.getId());
        Mission missionResponse = missionService.missionResponseList2(id);
        DroneLog inputDroneLog = new DroneLog();

        inputDroneLog.setMissionName(missionResponse.getName());
        inputDroneLog.setDroneDeviceName(missionResponse.getDrone().getDroneDeviceName());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        inputDroneLog.setInsertDt(timestamp);
        DroneLog droneLog = droneLogService.saveDroneLog(inputDroneLog);


        //미션 수행
        int i = 2;
        int step = 0;
        int flag = 0;
        HashMap<Integer, String> missionIndex = new HashMap<>();
        HashMap<String, Integer> gpsXs = new HashMap<>();
        HashMap<String, Integer> gpsYs = new HashMap<>();
        HashMap<String, Integer> gpsZs = new HashMap<>();
        HashMap<String, Integer> speeds = new HashMap<>();
        HashMap<String, String> result = new HashMap<>();
        HashMap<String, Integer> times = new HashMap<>();
        HashMap<String, Float> yaws = new HashMap<>();
        HashMap<String, MissionItemInt> missionMap = new HashMap<>();
        HashMap<String, Integer> radiusMap = new HashMap<>();
        Iterator iterator = missionResponse.getMissionDetails().iterator();
        while (iterator.hasNext()) {
            MissionDetails missionDetails = (MissionDetails) iterator.next();
            if (missionDetails.getName().equals("takeOff")) {

                gpsZs.put("takeOff", missionDetails.getAlt());
                log.info("Index={}", missionDetails.getIndex());
                missionIndex.put(missionDetails.getIndex(), "takeOff");


            } else if (missionDetails.getName().equals("waypoint")) {
                //각 waypoint에 값 받아와서 넣기
                missionIndex.put(missionDetails.getIndex(), "waypoint" + missionDetails.getIndex());
                gpsXs.put("waypoint" + missionDetails.getIndex(), (int) (missionDetails.getGpsX() * 10000000));
                speeds.put("waypoint" + missionDetails.getIndex(), missionDetails.getSpeed());
                gpsYs.put("waypoint" + missionDetails.getIndex(), (int) (missionDetails.getGpsY() * 10000000));
                gpsZs.put("waypoint" + missionDetails.getIndex(), missionDetails.getAlt());
                yaws.put("waypoint" + missionDetails.getIndex(), (float) (missionDetails.getYaw()));
            } else if (missionDetails.getName().equals("loi")) {
                missionIndex.put(missionDetails.getIndex(), "loi" + missionDetails.getIndex());
                times.put("loi" + missionDetails.getIndex(), missionDetails.getTime());
                gpsXs.put("loi" + missionDetails.getIndex(), (int) (missionDetails.getGpsX() * 10000000));
                gpsYs.put("loi" + missionDetails.getIndex(), (int) (missionDetails.getGpsY() * 10000000));
                gpsZs.put("loi" + missionDetails.getIndex(), missionDetails.getAlt());
                radiusMap.put("loi" + missionDetails.getIndex(), missionDetails.getRadius());
            } else if (missionDetails.getName().equals("return")) {

                missionIndex.put(missionDetails.getIndex(), "return");

                gpsXs.put("return" + missionDetails.getIndex(), (int) (missionDetails.getGpsX() * 10000000));
                speeds.put("return" + missionDetails.getIndex(), missionDetails.getSpeed());
                gpsYs.put("return" + missionDetails.getIndex(), (int) (missionDetails.getGpsY() * 10000000));
                gpsZs.put("return" + missionDetails.getIndex(), missionDetails.getAlt());

            }
        }

        log.info("{}", missionDetailsService.findByNameAndMission("takeOff", mission).getIndex());
        float takeOffAlt = gpsZs.get(missionIndex.get(missionDetailsService.findByNameAndMission("takeOff", mission).getIndex()));

        //float takeOffAlt = gpsZs.get(missionIndex.get(1));


        while (!missionIndex.getOrDefault(step, "finish").equals("finish")) {
            int x = 0;
            int y = 0;
            int z = 0;
            int time = 0;
            int speed = 0;
            int radius = 0;
            float yaw = 0;
            log.info("step={}", step);
//x,y 지금 바뀐 상황임 latitude longtitude 떄문에 바꿧음
            y = gpsXs.getOrDefault(missionIndex.get(step), 0);
            x = gpsYs.getOrDefault(missionIndex.get(step), 0);
            z = gpsZs.getOrDefault(missionIndex.get(step), 0);
            yaw = yaws.getOrDefault(missionIndex.get(step), 0f);
            time = times.getOrDefault(missionIndex.get(step), 0);
            speed = speeds.getOrDefault(missionIndex.get(step), 0);
            radius = radiusMap.getOrDefault(missionIndex.get(step), 0);
            log.info("x={},y={},z={}", x, y, z);
            if (missionIndex.getOrDefault(step, "finish").equals("takeOff")) {

                missionMap = flight.missionTakeoff(droneLog, missionResponse.getDrone().getId().intValue());
                flag++;

            } else if (missionIndex.getOrDefault(step, "finish").contains("waypoint")) {

//                log.info("x={},y={},z{}", x, y, z);
                log.info("yaw={}", yaw);
                MissionItemInt missionItemInt = new MissionItemInt.Builder()
                        .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                        .param1(time)
                        .param2(0)
                        .param3(0)
                        .param4(yaw)      //yaw
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

            } else if (missionIndex.getOrDefault(step, "finish").contains("loi")) {
                //      log.info("loitime={}", time);
                MissionItemInt missionItemInt = new MissionItemInt.Builder()
                        .command(MavCmd.MAV_CMD_NAV_LOITER_TURNS)
                        .param1(time)
                        .param2(0)
                        .param3(radius)
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

            } else if (missionIndex.getOrDefault(step, "finish").contains("finish")) {
                break;
            }
            step++;


        }

        flight.doMission(missionMap, flag, speeds, yaws, missionIndex);


    }


    @MessageMapping("/pause")
  //  @SendTo("/topic/pause")
    public void pause(@RequestBody Map<String,Object> paramMap) {
        int droneId=0;
        if (paramMap.get("droneId")!=null)
            droneId=Integer.parseInt(paramMap.get("droneId").toString());
        // flight.loiter(30);
        flight.pauseOrPlay(0);
        // flight.returnDrone();
    }

    @MessageMapping("/play")
   // @SendTo("/topic/play")
    public void play(@RequestBody Map<String,Object> paramMap) {
        int droneId=0;
        if (paramMap.get("droneId")!=null)
            droneId=Integer.parseInt(paramMap.get("droneId").toString());
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
        flight.flightTakeoff(100);
        //flight.returnDrone();
       // flight.setHome();
    }
    @GetMapping("/test2")
    public void test2() {
        // flight.loiter(30);
        //flight.camera();
        //flight.flightTakeoff(100);
        flight.returnDrone();
        // flight.setHome();
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



