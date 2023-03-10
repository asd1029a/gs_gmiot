package com.danusys.web.commons.api.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_area_code_name")
public class AdmInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String areaCode;

    @Column(nullable = false)
    private String areaName;
}
