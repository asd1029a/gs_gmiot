package com.danusys.web.drone.dto.request;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DroneRequest {

    private String droneDeviceName;
    private String userId;
    private String droneStatus;

}
