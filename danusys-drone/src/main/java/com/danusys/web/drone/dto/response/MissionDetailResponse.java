package com.danusys.web.drone.dto.response;

import com.danusys.web.drone.model.MissionDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MissionDetailResponse {
    private Long id;
    private String name;
    private int index;
    private double mapX;
    private double mapY;
    private int mapZ;
    private int speed;
    private int time;
    private double yaw;
    private int radius;
    private String koName;

    public MissionDetailResponse(MissionDetails missionDetails) {
        this.id = missionDetails.getId();
        this.name = missionDetails.getName();
        this.index = missionDetails.getIndex();
        this.mapX = missionDetails.getGpsX();
        this.mapY = missionDetails.getGpsY();
        this.mapZ = missionDetails.getAlt();
        this.speed = missionDetails.getSpeed();
        this.time = missionDetails.getTime();
        this.yaw = missionDetails.getYaw();
        this.radius = missionDetails.getRadius();
        this.koName = missionDetails.getKoName();

    }
}
