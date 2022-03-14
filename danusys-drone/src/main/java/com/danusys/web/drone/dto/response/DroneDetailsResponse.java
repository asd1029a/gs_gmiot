package com.danusys.web.drone.dto.response;


import com.danusys.web.drone.model.DroneDetails;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class DroneDetailsResponse {

    private Long id;

    private String size;

    private float weight;

    private float maximumOperatingDistance;

    private float operationTemperatureRangeMin;

    private float operationTemperatureRangeMax;

    private String simNumber;

    private String masterManager;

    private String subManager;

    private String manufacturer;

    private String type;

    private int maximumManagementAltitude;

    private int maximumOperatingSpeed;

    private int maximumSpeed;

    private long insertUserId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp insertDt;

    private long updateUserId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp updateDt;

    private String thumbnailImg;

    private String thumbnailRealImg;

    private int size1;

    private int size2;

    private int size3;

    private int maximumOperatingWeight;

    private int flightTime;


    public DroneDetailsResponse(DroneDetails droneDetails){
        this.id=droneDetails.getId();
        this.size=droneDetails.getSize();
        this.weight=droneDetails.getWeight();
        this.maximumOperatingDistance=droneDetails.getMaximumOperatingDistance();
        this.operationTemperatureRangeMin=droneDetails.getOperationTemperatureRangeMin();
        this.operationTemperatureRangeMax=droneDetails.getOperationTemperatureRangeMax();
        this.simNumber=droneDetails.getSimNumber();
        this.masterManager=droneDetails.getMasterManager();
        this.subManager=droneDetails.getSubManager();
        this.manufacturer=droneDetails.getManufacturer();
        this.type=droneDetails.getType();
        this.maximumManagementAltitude=droneDetails.getMaximumManagementAltitude();
        this.maximumOperatingSpeed=droneDetails.getMaximumOperatingSpeed();
        this.maximumSpeed=droneDetails.getMaximumSpeed();
        this.insertUserId=droneDetails.getInsertUserId();
        this.insertDt=droneDetails.getInsertDt();
        this.updateUserId=droneDetails.getUpdateUserId();
        this.thumbnailImg=droneDetails.getThumbnailImg();
        this.thumbnailRealImg=droneDetails.getThumbnailRealImg();
        this.size1=droneDetails.getSize1();
        this.size2=droneDetails.getSize2();
        this.size3=droneDetails.getSize3();
        this.maximumOperatingWeight=droneDetails.getMaximumOperatingWeight();
        this.flightTime=droneDetails.getFlightTime();
    }

}

