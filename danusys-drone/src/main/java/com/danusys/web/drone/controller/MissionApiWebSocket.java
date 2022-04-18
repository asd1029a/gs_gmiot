package com.danusys.web.drone.controller;


import com.danusys.web.commons.socket.config.CustomServerSocket;
import com.danusys.web.drone.dto.response.DroneResponse;
import com.danusys.web.drone.dto.response.Gps;
import com.danusys.web.drone.dto.response.MissionDetailResponse;
import com.danusys.web.drone.dto.response.MissionResponse;
import com.danusys.web.drone.model.DroneLog;
import com.danusys.web.drone.service.*;
import com.danusys.web.drone.utils.Flight;
import io.dronefleet.mavlink.common.MavCmd;
import io.dronefleet.mavlink.common.MavFrame;
import io.dronefleet.mavlink.common.MavMissionType;
import io.dronefleet.mavlink.common.MissionItemInt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/drone/api")
@Slf4j
@RequiredArgsConstructor
public class MissionApiWebSocket {

    private final MissionService missionService;
    private final MissionDetailsService missionDetailsService;

    private final DroneLogService droneLogService;
    private final DroneService droneService;

    private final CustomServerSocket ServerSocket;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DroneLogDetailsService droneLogDetailsService;
    private final ConnectionService connectionService;


    //    private Flight flight;
    private Map<Integer, Flight> flightMap = new HashMap<>();
    private Map<Integer, Integer> isStarted = new HashMap<>();
    private Map<Integer, Integer> hasFlightMap = new HashMap<>();
/*

    url:/return
    do: 해당 드론 귀환
    param: paramMap{droneId}

 */

    @MessageMapping("/logging")
    public void logging(Map<String, Object> paramMap) {
        int droneId = 0;

        if (paramMap.get("droneId") != null) {
            droneId = Integer.parseInt(paramMap.get("droneId").toString());
        }
        log.info("LoggingDroneId={}", droneId);
        AtomicBoolean alreadyStartMission = new AtomicBoolean(false);
        int finalDroneId = droneId;
        flightMap.forEach((k, v) -> {
            if (finalDroneId == k)
                alreadyStartMission.set(true);
        });
        if (alreadyStartMission.get() == false) {
            Flight flight = new Flight(ServerSocket, simpMessagingTemplate, droneLogDetailsService, droneService, connectionService);
            flightMap.put(droneId, flight);
            hasFlightMap.put(droneId, 2);
        }
        Flight flight = flightMap.get(droneId);
        flight.logging(droneId);
        alreadyStartMission.set(false);
    }

    @MessageMapping("/return")

    public void returnDrone(Map<String, Object> paramMap) {
        int droneId = 0;
        if (paramMap.get("droneId") != null)
            droneId = Integer.parseInt(paramMap.get("droneId").toString());
        log.info("droneId={}", droneId);
        Flight flight = flightMap.get(droneId);
        flight.returnDrone();
    }

    @MessageMapping("/waypoint")
    public void wayPointDrone(Map<String, Object> paramMap) {
        log.info("paramMap={}", paramMap);
        double gpsX = 0;
        double gpsY = 0;
        double gpsZ = 0;


        int intGpsX = 0;
        int intGpsY = 0;
        int intGpsZ = 0;
        int yaw = 0;
        int droneId = 0;
        if (paramMap.get("droneId") != null)
            droneId = Integer.parseInt(paramMap.get("droneId").toString());
        log.info("droneId={}", droneId);
        if (paramMap.get("gpsX") != null)
            gpsX = Double.parseDouble(paramMap.get("gpsX").toString()) * 10000000;
        if (paramMap.get("gpsY") != null)
            gpsY = Double.parseDouble(paramMap.get("gpsY").toString()) * 10000000;
        if (paramMap.get("alt") != null && !paramMap.get("alt").equals("")) {
            gpsZ = Double.parseDouble(paramMap.get("alt").toString());
            intGpsZ = (int) gpsZ;
        }

        if (paramMap.get("yaw") != null)
            yaw = Integer.parseInt(paramMap.get("yaw").toString());
        intGpsX = (int) gpsX;
        intGpsY = (int) gpsY;

        Flight flight = flightMap.get(droneId);
        flight.wayPoint(intGpsX, intGpsY, intGpsZ, yaw);


    }

    @MessageMapping("/changeyaw")
    public void changeYaw(@RequestBody Map<String, Object> paramMap) {

        int yaw = 0;
        int droneId = 0;
        if (paramMap.get("yaw") != null)
            yaw = Integer.parseInt(paramMap.get("yaw").toString());
        if (paramMap.get("droneId") != null)
            droneId = Integer.parseInt(paramMap.get("droneId").toString());
        log.info("droneId={}", droneId);
        Flight flight = flightMap.get(droneId);
        flight.changeYaw(yaw);
    }


    @MessageMapping("/setmissioncurrent")
    public void setMissionCurrent(@RequestBody Map<String, Object> paramMap) {

        int seq = 0;
        int droneId = 0;
        if (paramMap.get("seq") != null)
            seq = Integer.parseInt(paramMap.get("seq").toString());
        if (paramMap.get("droneId") != null)
            droneId = Integer.parseInt(paramMap.get("droneId").toString());
        log.info("droneId={}", droneId);
        Flight flight = flightMap.get(droneId);
        flight.setMissionCurrent(seq);
    }

    @MessageMapping("/startmission")
    public void startMission(@RequestBody Map<String, Object> paramMap) {

        int droneId = 0;
        int started = -1;
        final String[] isEnd = {""};
        if (paramMap.get("droneId") != null)
            droneId = Integer.parseInt(paramMap.get("droneId").toString());
        //flight check

//        if ((started = isStarted.getOrDefault(droneId, -1)) != 2) {
        isStarted.put(droneId, 2);

        log.info("처음시작됨");

        Flight flight = flightMap.get(droneId);
        if (flight.getEOFCheck() == 1) {
            flight.connect(droneId);
            flight.setEOFCheck(0);

        }
        DroneResponse drone = droneService.findOneDrone(droneId);
        MissionResponse missionResponse = drone.getDroneInmission().getMission();
        DroneLog inputDroneLog = new DroneLog();

        inputDroneLog.setMissionName(missionResponse.getName());
        String droneName = drone.getDroneDeviceName();
        inputDroneLog.setDroneDeviceName(droneName);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        inputDroneLog.setInsertDt(timestamp);
        DroneLog droneLog = droneLogService.saveDroneLog(inputDroneLog);


        //미션 수행
//        int i = 2;
        int step = 0;
        int flag = 0;
        HashMap<Integer, String> missionIndex = new HashMap<>();
        HashMap<String, Integer> gpsXs = new HashMap<>();
        HashMap<String, Integer> gpsYs = new HashMap<>();
        HashMap<String, Integer> gpsZs = new HashMap<>();
        HashMap<String, Integer> speeds = new HashMap<>();
        HashMap<String, Integer> times = new HashMap<>();
        HashMap<String, Float> yaws = new HashMap<>();
        HashMap<String, MissionItemInt> missionMap = new HashMap<>();
        HashMap<String, Integer> radiusMap = new HashMap<>();
        Iterator iterator = missionResponse.getMissionDetails().iterator();
        while (iterator.hasNext()) {
            MissionDetailResponse missionDetails = (MissionDetailResponse) iterator.next();
            if (missionDetails.getName().equals("takeOff")) {
                gpsZs.put("takeOff", missionDetails.getMapZ());
                //  log.info("Index={}", missionDetails.getIndex());
                missionIndex.put(missionDetails.getIndex(), "takeOff");
            } else if (missionDetails.getName().equals("waypoint")) {
                //각 waypoint에 값 받아와서 넣기
                missionIndex.put(missionDetails.getIndex(), "waypoint" + missionDetails.getIndex());
                gpsXs.put("waypoint" + missionDetails.getIndex(), (int) (missionDetails.getMapX() * 10000000));
                speeds.put("waypoint" + missionDetails.getIndex(), missionDetails.getSpeed());
                gpsYs.put("waypoint" + missionDetails.getIndex(), (int) (missionDetails.getMapY() * 10000000));
                gpsZs.put("waypoint" + missionDetails.getIndex(), missionDetails.getMapZ());
                yaws.put("waypoint" + missionDetails.getIndex(), (float) (missionDetails.getYaw()));
                times.put("waypoint" + missionDetails.getIndex(), missionDetails.getTime());
            } else if (missionDetails.getName().equals("loi")) {
                missionIndex.put(missionDetails.getIndex(), "loi" + missionDetails.getIndex());
                times.put("loi" + missionDetails.getIndex(), missionDetails.getTime());
                gpsXs.put("loi" + missionDetails.getIndex(), (int) (missionDetails.getMapX() * 10000000));
                gpsYs.put("loi" + missionDetails.getIndex(), (int) (missionDetails.getMapY() * 10000000));
                gpsZs.put("loi" + missionDetails.getIndex(), missionDetails.getMapZ());
                radiusMap.put("loi" + missionDetails.getIndex(), missionDetails.getRadius());
            } else if (missionDetails.getName().equals("return")) {

                missionIndex.put(missionDetails.getIndex(), "return");

                gpsXs.put("return" + missionDetails.getIndex(), (int) (missionDetails.getMapX() * 10000000));
                speeds.put("return" + missionDetails.getIndex(), missionDetails.getSpeed());
                gpsYs.put("return" + missionDetails.getIndex(), (int) (missionDetails.getMapY() * 10000000));
                gpsZs.put("return" + missionDetails.getIndex(), missionDetails.getMapZ());

            }
        }
        //TODO takeoff 높이 지정 해야됨


        // log.info("{}", missionDetailsService.findByNameAndMission("takeOff", mission).getIndex());
        // float takeOffAlt = gpsZs.get(missionIndex.get(missionDetailsService.findByNameAndMission("takeOff", mission).getIndex()));
        //float takeOffAlt = gpsZs.get(missionIndex.get(1));


        while (!missionIndex.getOrDefault(step, "finish").equals("finish")) {
            int x = 0;
            int y = 0;
            int z = 0;
            int time = 0;

            int radius = 0;
            float yaw = 0;
            //      log.info("step={}", step);
//x,y 지금 바뀐 상황임 latitude longtitude 떄문에 바꿧음
            y = gpsXs.getOrDefault(missionIndex.get(step), 0);
            x = gpsYs.getOrDefault(missionIndex.get(step), 0);
            z = gpsZs.getOrDefault(missionIndex.get(step), 0);
            yaw = yaws.getOrDefault(missionIndex.get(step), 0f);
            time = times.getOrDefault(missionIndex.get(step), 0);

            radius = radiusMap.getOrDefault(missionIndex.get(step), 0);
            if (missionIndex.getOrDefault(step, "finish").equals("takeOff")) {

                missionMap = flight.missionTakeoff(droneLog, droneId);
                isStarted.put(droneId, 1);
                flag++;

            } else if (missionIndex.getOrDefault(step, "finish").contains("waypoint")) {
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


        //TODO isEnd 두번실행시 오류
        Timer startMissionTimer = new Timer();
        Timer startMissionTimer2 = new Timer();
        HashMap<String, MissionItemInt> finalMissionMap = missionMap;
        int finalFlag = flag;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (flight.getIsTakeOffEnd()) {
                    isEnd[0] = flight.doMission(finalMissionMap, finalFlag, speeds, yaws, missionIndex);
                    this.cancel();

                }
            }
        };
        int finalDroneId = droneId;
        TimerTask timerTask2 = new TimerTask() {
            @Override
            public void run() {
                if (isEnd[0].equals("stop")) {
                    log.info("flightMap && isStarted remove");
                    flightMap.remove(finalDroneId);
                    isStarted.remove(finalDroneId);
                    this.cancel();
                }

            }
        };
        startMissionTimer.schedule(timerTask,0,1000);
        startMissionTimer2.schedule(timerTask2,0,1000);


        log.info("isStarted={}", isStarted);
        log.info("Mission2");


        isStarted.put(droneId, 0);
        log.info("isEnd={}", isEnd[0]);

    }


    @MessageMapping("/pause")
    public void pause(@RequestBody Map<String, Object> paramMap) {
        int droneId = 0;
        if (paramMap.get("droneId") != null)
            droneId = Integer.parseInt(paramMap.get("droneId").toString());
        log.info("droneId={}", droneId);
        Flight flight = flightMap.get(droneId);
        flight.pauseOrPlay(0);

    }

    @MessageMapping("/play")
    public void play(@RequestBody Map<String, Object> paramMap) {
        int droneId = 0;
        if (paramMap.get("droneId") != null)
            droneId = Integer.parseInt(paramMap.get("droneId").toString());
        log.info("droneId={}", droneId);
        Flight flight = flightMap.get(droneId);
        flight.pauseOrPlay(1);
    }

    @MessageMapping("/arm")

    public void arm(@RequestBody Map<String, Object> paramMap) {
        int droneId = 0;
        if (paramMap.get("droneId") != null)
            droneId = Integer.parseInt(paramMap.get("droneId").toString());
        log.info("droneId={}", droneId);

        Flight flight = flightMap.get(droneId);
        flight.armDisarm(1, droneId);

    }

    @MessageMapping("/disarm")
    public void disarm(@RequestBody Map<String, Object> paramMap) {
        int droneId = 0;
        if (paramMap.get("droneId") != null)
            droneId = Integer.parseInt(paramMap.get("droneId").toString());
        log.info("droneId={}", droneId);
        Flight flight = flightMap.get(droneId);
        flight.armDisarm(0, droneId);
    }


}



