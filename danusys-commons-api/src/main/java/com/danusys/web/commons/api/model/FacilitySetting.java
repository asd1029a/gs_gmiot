package com.danusys.web.commons.api.model;

import com.danusys.web.commons.api.dto.SettingDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_facility_setting")
public class FacilitySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilitySettingSeq;

    @Column(nullable = false)
    private Long facilitySeq;

    @Column(nullable = false)
    private String facilitySettingName;

    @Column(nullable = false)
    private String facilitySettingValue;

    @Column(nullable = false)
    private String facilitySettingTime;

    @Column(nullable = false)
    private String facilitySettingDay;

    private Integer facilitySettingType;

    private String facilityId;

    private String administZone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp insertDt;


    public FacilitySetting(SettingDTO settingDTO){
        this.facilitySeq = settingDTO.getFacilitySeq();
        this.facilitySettingName = settingDTO.getFacilitySettingName();
        this.facilitySettingValue = settingDTO.getFacilitySettingValue();
        this.facilitySettingTime = settingDTO.getFacilitySettingTime();
        this.facilitySettingType = settingDTO.getFacilitySettingType();
        this.facilitySettingDay = settingDTO.getFacilitySettingDay();
    }

    @Builder
    public FacilitySetting setFacilitySetting(SettingDTO settingDTO){
        this.facilitySeq = settingDTO.getFacilitySeq();
        this.facilitySettingName = settingDTO.getFacilitySettingName();
        this.facilitySettingValue = settingDTO.getFacilitySettingValue();
        this.facilitySettingTime = settingDTO.getFacilitySettingTime();
        this.facilitySettingType = settingDTO.getFacilitySettingType();
        this.facilitySettingDay = settingDTO.getFacilitySettingDay();

        return this;
    }

    public void setFacilityId(String facilityId){
        this.facilityId = facilityId;
    }

    public void setAdministZone(String administZone){
        this.administZone = administZone;
    }
}
