package com.danusys.web.commons.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/08
 * Time : 15:34
 */
@Entity
@Data
@Table(name = "t_facility")
public class Facility implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilitySeq;

    @Column(nullable = false)
    private int facilityKind;

    @Column(nullable = false)
    private int facilityStatus;

    @Column
    private String facilityImage;

    @Column
    private String facilityInstlInfo;

    @Column
    private Timestamp facilityInstlDt;

    @Column
    private Timestamp insertDt;

    @Column
    private int insertUserSeq;

    @Column
    private Time updateDt;

    @Column
    private int updateUserSeq;

    @Column(unique = true)
    private String facilityId;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column(nullable = false)
    private Long stationSeq;
}
