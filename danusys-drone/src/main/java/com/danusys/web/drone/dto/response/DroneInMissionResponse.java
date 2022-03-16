package com.danusys.web.drone.dto.response;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneInMission;
import com.danusys.web.drone.model.DroneLog;
import com.danusys.web.drone.model.Mission;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DroneInMissionResponse {


    private Long index;
    private MissionResponse mission;

    public DroneInMissionResponse(DroneInMission droneInMission) {
        if (droneInMission != null) {
            this.index = droneInMission.getIndex();
            this.mission = new MissionResponse(droneInMission.getMission());
        }

    }


}
