package com.danusys.web.commons.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
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
@Table(name = "t_station")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stationSeq;

    @Column(nullable = false, unique = true)
    private String stationName;

    @Column(nullable = false)
    private Long stationKind;

    @Column
    private String administZone;

    @Column
    private String address;

    @Column
    private String stationImage;

    @Column
    private Timestamp stationCompetDt;

    @Column
    private int stationSize;

    @Column
    private String stationMaterial;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "stationSeq")
    @JsonManagedReference
    private List<Facility> facilities = new ArrayList<Facility>();
}
