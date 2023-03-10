package com.danusys.web.drone.dto.response;

import com.danusys.web.drone.model.Mission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MissionResponse {
    private Long id;
    private String name;
    private String UserId;
    private String updateDt;
    private int droneId;
    private int totalDistance;
    private int estimatedTime;
    

    private List<MissionDetailResponse> missionDetails;



    public MissionResponse(Mission mission){
        this.id = mission.getId();
        this.name=mission.getName();
        this.UserId=mission.getUserId();
        this.droneId= mission.getDroneId();
        this.updateDt=mission.getUpdateDt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        this.totalDistance=mission.getTotalDistance();
        this.estimatedTime=mission.getEstimatedTime();
        this.missionDetails= mission.getMissionDetails().stream().map(MissionDetailResponse::new).collect(Collectors.toList());



    }

}
