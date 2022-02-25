package com.danusys.web.drone.dto.request;

import com.danusys.web.drone.model.DroneDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DroneDetailRequest {
    private long droneId;
    private DroneDetails droneDetails;
    private long droneBase;

}
