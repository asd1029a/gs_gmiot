package com.danusys.web.drone.dto.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Gps {
    private float gpsX;  //z
    private float gpsY;  //y
    private float currentHeight; // 높이
    private String wpDistString; // 목표까지의거리
    private String missionId; //미션 종류 (귀환,이륙,이동 등등)
    private String heading; //방향 0이 north
    private float airspeed; //수직속도
    private float groundspeed;//수평속도





}
