package com.danusys.web.commons.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/08
 * Time : 15:34
 */
@Entity
@Data
@NoArgsConstructor
@DynamicUpdate
@Table(name = "t_facility")
public class Facility implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilitySeq;

    @Column(nullable = false)
    private Long facilityKind;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facilityKind", referencedColumnName = "codeSeq", updatable = false, insertable = false)
    @JsonManagedReference
    private CommonCode facilityKindCode;

    @Column(nullable = false)
    private int facilityStatus;

    @Column
    private String facilityImage;

    @Column
    private String facilityInstlInfo;

    @Column
    private Timestamp facilityInstlDt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp insertDt;

    @Column
    private Integer insertUserSeq;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp updateDt;

    @Column
    private Integer updateUserSeq;

    @Column(unique = true)
    private String facilityId;

    @Column
    private String facilityName;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private Long stationSeq;

    @Column
    private String administZone;

    @Column
    private Long aliveCheck;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "facilitySeq")
    @JsonManagedReference
    private List<FacilityOpt> facilityOpts = new ArrayList<FacilityOpt>();

    @Builder
    public Facility(String facilityId, String facilityName,
                    int facilityStatus, double latitude,
                    double longitude, Long facilitySeq,
                    Long stationSeq, Long facilityKind,
                    String administZone) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.facilityStatus = facilityStatus;
        this.latitude = latitude;
        this.longitude = longitude;
        this.facilitySeq = facilitySeq;
        this.stationSeq = stationSeq;
        this.facilityKind = facilityKind;
        this.administZone = administZone;
    }

}
