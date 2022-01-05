package com.danusys.web.drone.dto.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Gps {
    private float gpsX;
    private float gpsY;
    private float currentHeight;
    private String wpDistString;
    private String missionId;




}
