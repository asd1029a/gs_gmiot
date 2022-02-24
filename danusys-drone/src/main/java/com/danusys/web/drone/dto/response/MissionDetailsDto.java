package com.danusys.web.drone.dto.response;

import com.danusys.web.drone.model.Mission;
import com.danusys.web.drone.model.MissionDetails;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
public class MissionDetailsDto {


    private Long id;
    private String name;
    private int index;
    private double gpsX;
    private double gpsY;
    private int alt;
    private int speed;
    private int time;
    private double yaw;
    private int radius;
    private String koName;

    public MissionDetailsDto(MissionDetails missionDetails){
        this.id= missionDetails.getId();
        this.name= missionDetails.getName();
        this.index=missionDetails.getIndex();
        this.gpsX=missionDetails.getGpsX();
        this.gpsY=missionDetails.getGpsY();
        this.alt=missionDetails.getAlt();
        this.speed=missionDetails.getSpeed();
        this.time=missionDetails.getTime();
        this.yaw=missionDetails.getRadius();
        this.radius=missionDetails.getRadius();
        this.koName=missionDetails.getKoName();
    }


}
