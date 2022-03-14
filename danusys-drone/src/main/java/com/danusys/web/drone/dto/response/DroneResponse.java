package com.danusys.web.drone.dto.response;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneBase;
import com.danusys.web.drone.model.DroneDetails;
import com.danusys.web.drone.model.DroneInMission;
import com.danusys.web.drone.repository.DroneInMissionRepository;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class DroneResponse {

    private Long id;
    private String droneDeviceName;
    private String userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private    Timestamp updateDt;
    private DroneDetailsResponse droneDetails;
    private String status;
    private DroneInMissionResponse droneInmission;
    private DroneBase droneBase;

    public DroneResponse(Drone drone){
        this.id=drone.getId();
        this.droneDeviceName=drone.getDroneDeviceName();
        this.userId=drone.getUserId();
        this.updateDt=drone.getUpdateDt();
        this.droneDetails=new DroneDetailsResponse(drone.getDroneDetails());
        this.status=drone.getStatus();
       // this.droneInmission=drone.getDroneInmission();
        this.droneInmission=new DroneInMissionResponse(drone.getDroneInmission());
        this.droneBase=drone.getDroneBase();
    }






}
