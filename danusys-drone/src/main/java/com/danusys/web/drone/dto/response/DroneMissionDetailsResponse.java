package com.danusys.web.drone.dto.response;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneBase;
import com.danusys.web.drone.model.Mission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class DroneMissionDetailsResponse {
    private Long id;
    private String droneDeviceName;
    private MissionDto mission;
    private String droneBase;

    //private List<MissionDetailResponse> missionDetailResponseList;


    public DroneMissionDetailsResponse(Drone drone) {
        this.id = drone.getId();
        this.droneDeviceName = drone.getDroneDeviceName();
        if (drone.getDroneInmission() != null) {
            this.mission = new MissionDto(drone.getDroneInmission().getMission());
        }else{
            Mission nullMission=new Mission();
            nullMission.setId(null);
            nullMission.setName(null);
            nullMission.setUpdateDt(null);
            nullMission.setTotalDistance(0);
            nullMission.setEstimatedTime(0);

            this.mission =new MissionDto(nullMission);
        }

        this.droneBase = drone.getDroneBase().getBaseName();
        //this.missionDetailResponseList= mission.getMissonDetails().stream().map(MissionDetailResponse::new).collect(Collectors.toList());


    }

}
