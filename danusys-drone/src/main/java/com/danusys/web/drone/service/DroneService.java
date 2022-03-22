package com.danusys.web.drone.service;


import com.danusys.web.drone.api.MAVLinkConnection;
import com.danusys.web.drone.dto.request.DroneRequest;
import com.danusys.web.drone.dto.response.DroneMissionDetailsResponse;
import com.danusys.web.drone.dto.response.DroneResponse;
import com.danusys.web.drone.dto.response.MissionDetailResponse;
import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.model.DroneLogDetails;
import com.danusys.web.drone.repository.DroneDetailsRepository;
import com.danusys.web.drone.repository.DroneInMissionRepository;
import com.danusys.web.drone.repository.DroneRepository;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class DroneService {

    private final DroneRepository droneRepository;
    private final DroneDetailsRepository droneDetailsRepository;
    private final DroneInMissionRepository droneInMissionRepository;

    @Transactional
    public String updateDrone(Drone drone) {

        Optional optionalDrone = droneRepository.findById(drone.getId());
        if (!optionalDrone.isPresent()) {
            return "fail";
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Drone updateDrone = (Drone) optionalDrone.get();

        if (drone.getDroneDeviceName() != null)
            updateDrone.setDroneDeviceName(drone.getDroneDeviceName());
        updateDrone.setUserId(drone.getUserId());

        updateDrone.setUpdateDt(timestamp);

        return "success";

    }


    @Transactional
    public String saveDrone(Drone drone) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        drone.setUpdateDt(timestamp);
        droneRepository.save(drone);
        return "success";
    }

    @Transactional
    public String deleteDrone(Drone drone) {
        DroneDetails droneDetails = droneDetailsRepository.findByDrone(drone);
        if (droneDetails == null) {
            return "fail";
        }


        droneDetailsRepository.deleteByDrone(drone);
        droneRepository.deleteById(drone.getId());
        droneInMissionRepository.deleteDroneInMissionbySeq(drone.getId());

        return "success";
    }

    @Transactional
    public List<?> findDroneList(DroneRequest droneRequest) {
        log.info("droneRequest={}", droneRequest);
        List<Drone> droneList = null;
        Sort sort = sortByupdateDt();

        String droneStatus = null;
        if (droneRequest.getDroneStatus() != null) {
            if (droneRequest.getDroneStatus().equals("all")) {
                droneStatus = "대기중";
            } else if (droneRequest.getDroneStatus().equals("wait")) {
                droneStatus = "대기중";
            } else if (droneRequest.getDroneStatus().equals("run")) {
                droneStatus = "운영중";
            }
            if (droneRequest.getUserId() != null) {
                log.info("id로검색");

                droneList = droneRepository.findAllByUserIdLikeAndStatus("%" + droneRequest.getUserId() + "%", droneStatus, sort);

                droneList.forEach(r -> {
                    log.info("droneId={},{},{},{}", r.getUserId(), r.getDroneDeviceName(),
                            r.getId(), r.getStatus());

                });
            } else if (droneRequest.getDroneDeviceName() != null) {
                log.info("devicename으로검색");
                droneList = droneRepository.findAllByDroneDeviceNameLikeAndStatus("%" + droneRequest.getDroneDeviceName() + "%",
                        droneStatus, sort);
            }

        } else {
            if (droneRequest.getUserId() != null) {
                log.info("id로검색");
                droneList = droneRepository.findAllByUserIdLike("%" + droneRequest.getUserId() + "%", sort);

            } else if (droneRequest.getDroneDeviceName() != null) {
                log.info("devicename으로검색");
                droneList = droneRepository.findAllByDroneDeviceNameLike("%" + droneRequest.getDroneDeviceName() + "%", sort);
            }
        }


        // log.info("droneStatus={}", droneStatus);

        log.info("여긴오나?");
        return droneList.stream().map(DroneResponse::new).collect(Collectors.toList());
        //return droneList;
    }

    private Sort sortByupdateDt() {
        return Sort.by(Sort.Direction.DESC, "updateDt");
    }


    public Drone findDrone(Drone drone) {
        //log.info("droneName={}", drone.getDroneDeviceName());
        if (drone.getDroneDeviceName() != null) {
            return droneRepository.findByDroneDeviceName(drone.getDroneDeviceName());
        } else {
            return null;
        }
    }

    public DroneResponse findOneDrone(long droneId) {
        Optional<Drone> optionalDrone = droneRepository.findById(droneId);
        if (!optionalDrone.isPresent())
            return null;
        Drone drone = optionalDrone.get();
        DroneResponse droneResponse = new DroneResponse(drone);
        return droneResponse;
    }

    public List<Drone> findAllDrone() {

        return droneRepository.findAllByIdNot(0l);
    }


    public Object getSocketDrone() {

        ServerSocket server_socket = null;  //서버 생성을 위한 ServerSocket
        try {
            server_socket = new ServerSocket(8600);

        } catch (IOException e) {
            log.info("해당 포트가 열려있습니다.");
        }
        try {

            int systemId = 1;
            int componentId = 1;
            int linkId = 1;
            long timestamp = System.currentTimeMillis();/* provide microsecond time */
            ;
            byte[] secretKey = new byte[0];
            secretKey = MessageDigest.getInstance("SHA-256").digest("danusys".getBytes(StandardCharsets.UTF_8));
            Socket socket = server_socket.accept();    //서버 생성 , Client 접속 대기
            MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
            Heartbeat heartbeat = null;

            MavlinkMessage message;
            //   connection.send2(systemId, componentId, new CommandLong.Builder().command(MavCmd.MAV_CMD_GET_HOME_POSITION).build(), linkId, timestamp, secretKey);

            while ((message = connection.next()) != null) {
                if (message.getPayload() instanceof TerrainReport) {
                    MavlinkMessage<TerrainReport> terrainReportMavlinkMessage = (MavlinkMessage<TerrainReport>) message;
                    float takeoff = terrainReportMavlinkMessage.getPayload().currentHeight();


                } else if (message.getPayload() instanceof Heartbeat) {
                    MavlinkMessage<Heartbeat> heartbeatMavlinkMessage = (MavlinkMessage<Heartbeat>) message;
                    heartbeat = Heartbeat.builder().autopilot(heartbeatMavlinkMessage.getPayload().autopilot())
                            .type(heartbeatMavlinkMessage.getPayload().type())
                            .systemStatus(heartbeatMavlinkMessage.getPayload().systemStatus())
                            .baseMode()
                            .mavlinkVersion(heartbeatMavlinkMessage.getPayload().mavlinkVersion())
                            .build();
                    connection.send2(systemId, componentId, heartbeat, linkId, timestamp, secretKey);
                    log.info("heartbeat={}", message);


                } else if (message.getPayload() instanceof Statustext) {        //statusMessage

                    //  log.info(message.toString());
                    MavlinkMessage<Statustext> statustextMavlinkMessage = (MavlinkMessage<Statustext>) message;
                    String missionText = statustextMavlinkMessage.getPayload().text();
                    log.info(missionText);
                    //   gps.setMissionType(missionText);
                    if (missionText.equals("ArduPilot Ready"))
                        break;

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        //new connection
        //  connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());


        //4 guided mode


        //end while


        finally {

        }


        return null;
    }
}
