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
    private int mapX;
    private int mapY;
    private int mapZ;
    private int speed;
    private int time;
    private int yaw;
    public MissionDetailResponse(MissionDetails missonDetails){
        this.id = missonDetails.getId();
        this.name= missonDetails.getName() ;
        this.index= missonDetails.getIndex();
        this.mapX= missonDetails.getGpsX();
        this.mapY= missonDetails.getGpsY();
        this.mapZ= missonDetails.getAlt();
        this.speed=missonDetails.getSpeed();
        this.time= missonDetails.getTime();
        this.yaw=missonDetails.getYaw();

    }
}
