package com.danusys.web.commons.api.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/15
 * Time : 16:24
 */
@Entity
@Data
@Table(name = "t_facility_opt")
public class FacilityOpt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityOptSeq;

    @Column(nullable = false)
    private Long facilitySeq;

    @Column(nullable = false)
    private String facilityOptName;

    @Column(nullable = false)
    private String facilityOptValue;

    private String facilityOptGeom;
}
