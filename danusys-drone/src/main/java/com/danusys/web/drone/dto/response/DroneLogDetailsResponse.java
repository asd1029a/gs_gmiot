package com.danusys.web.drone.dto.response;

import com.danusys.web.drone.model.DroneLog;
import com.danusys.web.drone.model.DroneLogDetails;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DroneLogDetailsResponse {

    private Long id;

    private String fromTarget;

    private String toTarget;

    private String type;

    private String param1;

    private String param2;

    private String param3;

    private String param4;

    private String param5;

    private String param6;

    private String param7;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp insertTime;

    public DroneLogDetailsResponse(DroneLogDetails droneLogDetails) {
        this.id = droneLogDetails.getId();
        this.fromTarget = droneLogDetails.getFromTarget();
        this.toTarget = droneLogDetails.getToTarget();
        this.type = droneLogDetails.getType();
        this.param1 = droneLogDetails.getParam1();
        this.param2 = droneLogDetails.getParam2();
        this.param3 = droneLogDetails.getParam3();
        this.param4 = droneLogDetails.getParam4();
        this.param5 = droneLogDetails.getParam5();
        this.param6 = droneLogDetails.getParam6();
        this.param7 = droneLogDetails.getParam7();
        this.insertTime = droneLogDetails.getInsertTime();
    }
}
